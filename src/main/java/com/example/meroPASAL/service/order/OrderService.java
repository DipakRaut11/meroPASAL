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

@Service
@RequiredArgsConstructor
public class OrderService implements IOrderService {

    private final OrderRepository orderRpository;
    private final ProductRepo productRepository;
    private final CartService cartService;
    private final ModelMapper modelMapper;


    @Override
    public Order placeOrder(Long userId) {
        Cart cart = cartService.getCartByUserId(userId);

        Order order = createOrder(cart);
        List<OrderItem> orderItemList = createOrerItems(order, cart);

        order.setOrderItems(new HashSet<>(orderItemList));
        order.setTotalAmount(calculateTotalAmount(orderItemList));
        Order saveOrder = orderRpository.save(order);
        cartService.clearCart(cart.getId());

        return saveOrder;
    }

    private Order createOrder(Cart cart) {
        Order order = new Order();
        order.setUser(cart.getUser());
        order.setOrderStatus(OderStatus.PENDING);
        order.setOrderDate(LocalDate.now());
        return order;

    }

    private List<OrderItem> createOrerItems(Order order, Cart cart) {
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

    private BigDecimal calculateTotalAmount(List<OrderItem> orderItermList) {
            return orderItermList.stream()
                    .map(item -> item.getPrice()
                            .multiply(new BigDecimal(item.getQuantity())))
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }


    @Override
    public OrderDto getOrderById(Long orderId) {
        return orderRpository.findById(orderId)
                .map(this :: convertToDto)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
    }

    @Override
    public List<OrderDto> getUserOrders(Long userId) {
        List<Order> orders = orderRpository.findByUserId(userId);
        return orders.stream()
                .map(this :: convertToDto)
                .toList();
    }


    


    @Override
    public OrderDto convertToDto(Order order){
        return modelMapper.map(order, OrderDto.class);
    }
}
