package com.example.meroPASAL.dto.order;

import lombok.Data;

@Data
public class EsewaResponseDto {
    private String transactionUuid;
    private double totalAmount;
    private String productCode;
    private String signature;
    private String dropLocation;
    private String landmark;
    private String receiverContact;

}
