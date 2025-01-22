package com.example.meroPASAL.dto.cart;


import com.example.meroPASAL.dto.ProductDto;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CartItemDto {
    private Long ItemId;
    private Integer quantity;
    private BigDecimal unitPrice;
    private ProductDto product;
}
