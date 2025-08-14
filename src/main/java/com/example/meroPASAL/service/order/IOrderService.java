package com.example.meroPASAL.service.order;



import com.example.meroPASAL.dto.order.OrderDto;
import com.example.meroPASAL.model.Order;

import java.util.List;

public interface IOrderService {

    Order placeOrder(Long userId);
    OrderDto getOrderById(Long orderId);

    List<OrderDto> getUserOrders(Long userId);


    OrderDto convertToDto(Order order);

    OrderDto getOrderByIdForUser(Long orderId, Long userId);
}
