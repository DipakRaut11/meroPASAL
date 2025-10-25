package com.example.meroPASAL.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class Payment {

    private String transactionId;
    private double amount;
    private double taxAmount;
    private double serviceCharge;
    private double deliveryCharge;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private double totalAmount;
    private String productCode;
    private String signature;

    private String dropLocation;
    private String landmark;
    private String receiverContact;

    public Payment(double amount, String dropLocation, String landmark, String receiverContact) {
        this.amount = amount;
        this.taxAmount = 0.0;
        this.serviceCharge = 0.0;
        this.deliveryCharge = 0.0;
        this.totalAmount = amount + taxAmount + serviceCharge + deliveryCharge;
        this.productCode = "EPAYTEST"; // Sandbox
        this.transactionId = "txn-" + UUID.randomUUID().toString().substring(0, 10);

        this.dropLocation = dropLocation;
        this.landmark = landmark;
        this.receiverContact = receiverContact;
    }

}
