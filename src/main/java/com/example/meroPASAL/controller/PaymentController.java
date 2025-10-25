package com.example.meroPASAL.controller;

import com.example.meroPASAL.model.Payment;
import com.example.meroPASAL.response.ApiResponse;
import com.example.meroPASAL.service.order.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RequiredArgsConstructor
@RestController
@RequestMapping("${api.prefix}/payment")
public class PaymentController {

    private final PaymentService paymentService;

    @Value("${esewa.sandbox.url}")
    private String esewaSandboxUrl;

    // Return JSON payment info for React frontend
//    @GetMapping("/api/payment/esewa")
//    public ApiResponse getEsewaPayment(@RequestParam(defaultValue = "100") double amount) throws Exception {
//        Payment payment = paymentService.preparePayment(amount);
//        return new ApiResponse("eSewa payment details generated", payment);
//    }

}
