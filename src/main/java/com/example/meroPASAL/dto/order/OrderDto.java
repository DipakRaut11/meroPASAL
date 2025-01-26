package com.example.meroPASAL.dto.order;


import com.example.meroPASAL.enums.OderStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

@Data
public class OrderDto {
    private Long id;
    private Long userId;
    private LocalDate orderDate;
    private BigDecimal totalAmount;
    private OderStatus orderStatus;
    private Set<OrderItemDto> items;
}
