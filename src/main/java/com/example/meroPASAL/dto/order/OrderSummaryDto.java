package com.example.meroPASAL.dto.order;


import com.example.meroPASAL.enums.OderStatus;
import com.example.meroPASAL.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderSummaryDto {
    private Long id;
    private Long userId;
    private LocalDate orderDate;
    private BigDecimal totalAmount;
    private OderStatus orderStatus;
    private String paymentScreenshotUrl; // ✅ proof image
    private List<OrderItemDto> items;
    private PaymentStatus paymentStatus;


}
