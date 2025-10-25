package com.example.meroPASAL.dto.order;

import lombok.Data;

@Data
public class OrderRequestDto {

        private double amount;
        private String dropLocation;
        private String landmark;
        private String receiverContact;
}
