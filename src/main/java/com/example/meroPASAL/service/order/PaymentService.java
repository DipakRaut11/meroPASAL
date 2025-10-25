package com.example.meroPASAL.service.order;

import com.example.meroPASAL.model.Payment;
import com.example.meroPASAL.utility.HmacUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PaymentService {

    @Value("${esewa.secret}")
    private String esewaSecret; // from application.properties

    public Payment preparePayment(double amount, String dropLocation, String landmark, String receiverContact) throws Exception {
        Payment payment = new Payment(amount, dropLocation, landmark, receiverContact);

        payment.setTransactionId(UUID.randomUUID().toString());
        payment.setProductCode("EPAYTEST");

        // Ensure total_amount is formatted exactly like in JSON
        String formattedAmount = String.format("%.2f", payment.getTotalAmount());

        // Build the exact message to sign
        String message = "total_amount=" + formattedAmount +
                ",transaction_uuid=" + payment.getTransactionId() +
                ",product_code=" + payment.getProductCode();

        // Generate HMAC signature
        String signature = HmacUtil.hmacSha256Base64(esewaSecret, message);
        payment.setSignature(signature);

        // Optional: store formatted total back to the payment object
        payment.setTotalAmount(Double.parseDouble(formattedAmount));

        return payment;
    }

}
