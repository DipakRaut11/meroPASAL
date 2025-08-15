package com.example.meroPASAL.service.order;

import com.example.meroPASAL.Repository.ProductRepo;
import com.example.meroPASAL.Repository.order.OrderRepository;
import com.example.meroPASAL.dto.order.OrderDto;
import com.example.meroPASAL.enums.OderStatus;
import com.example.meroPASAL.exception.ResourceNotFoundException;
import com.example.meroPASAL.model.Cart;
import com.example.meroPASAL.model.Order;
import com.example.meroPASAL.model.OrderItem;
import com.example.meroPASAL.model.Product;
import com.example.meroPASAL.service.cart.CartService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService implements IOrderService {

    private final OrderRepository orderRpository;
    private final ProductRepo productRepository;
    private final CartService cartService;
    private final ModelMapper modelMapper;

    // ------------------- CUSTOMER -------------------

    @Override
    public Order placeOrder(Long userId) {
        Cart cart = cartService.getCartByUserId(userId);

        Order order = createOrder(cart);
        List<OrderItem> orderItemList = createOrderItems(order, cart);

        order.setOrderItems(new HashSet<>(orderItemList));
        order.setTotalAmount(calculateTotalAmount(orderItemList));
        Order savedOrder = orderRpository.save(order);
        cartService.clearCart(cart.getId());

        return savedOrder;
    }

    private Order createOrder(Cart cart) {
        Order order = new Order();
        order.setUser(cart.getUser());
        order.setOrderStatus(OderStatus.PENDING);
        order.setOrderDate(LocalDate.now());
        return order;
    }

    private List<OrderItem> createOrderItems(Order order, Cart cart) {
        return cart.getItems()
                .stream()
                .map(cartItem -> {
                    Product product = cartItem.getProduct();
                    product.setInventory(product.getInventory() - cartItem.getQuantity());
                    productRepository.save(product);
                    return new OrderItem(
                            order,
                            product,
                            cartItem.getQuantity(),
                            cartItem.getUnitPrice()
                    );
                }).toList();
    }

    private BigDecimal calculateTotalAmount(List<OrderItem> orderItemList) {
        return orderItemList.stream()
                .map(item -> item.getPrice().multiply(new BigDecimal(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public OrderDto getOrderById(Long orderId) {
        return orderRpository.findById(orderId)
                .map(this::convertToDto)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
    }

    @Override
    public OrderDto getOrderByIdForUser(Long orderId, Long userId) {
        Order order = orderRpository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if (!order.getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException("Order not found for this user");
        }

        return convertToDto(order);
    }

    @Override
    public List<OrderDto> getUserOrders(Long userId) {
        List<Order> orders = orderRpository.findByUserId(userId);
        return orders.stream()
                .map(this::convertToDto)
                .toList();
    }

    @Override
    public OrderDto convertToDto(Order order){
        return modelMapper.map(order, OrderDto.class);
    }

    // ------------------- SHOPKEEPER -------------------

    @Override
    public List<OrderDto> getOrdersByShop(Long shopId) {
        List<Order> allOrders = orderRpository.findAll();

        return allOrders.stream()
                .map(order -> {
                    // Keep only items for this shop
                    var filteredItems = order.getOrderItems().stream()
                            .filter(item -> item.getProduct().getShopkeeper().getId().equals(shopId))
                            .collect(Collectors.toSet());

                    if (filteredItems.isEmpty()) {
                        return null; // no items for this shop, skip
                    }

                    // Create a shallow copy so we don’t mutate the original order in DB
                    Order orderCopy = new Order();
                    orderCopy.setOrderId(order.getOrderId());
                    orderCopy.setUser(order.getUser());
                    orderCopy.setOrderDate(order.getOrderDate());
                    orderCopy.setOrderStatus(order.getOrderStatus());
                    orderCopy.setOrderItems(filteredItems);

                    // Calculate total for this shop only
                    BigDecimal totalForShop = filteredItems.stream()
                            .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    orderCopy.setTotalAmount(totalForShop);

                    return convertToDto(orderCopy);
                })
                .filter(Objects::nonNull)
                .toList();
    }


    @Override
    public OrderDto updateOrderStatus(Long orderId, OderStatus status, Long shopId) {
        Order order = orderRpository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        boolean belongsToShop = order.getOrderItems().stream()
                .anyMatch(item -> item.getProduct().getShopkeeper().getId().equals(shopId));

        if (!belongsToShop) {
            throw new ResourceNotFoundException("This order does not belong to your shop");
        }

        order.setOrderStatus(status);
        return convertToDto(orderRpository.save(order));
    }
}
