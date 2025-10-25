package com.example.meroPASAL.controller;


import com.example.meroPASAL.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("${api.prefix}/esewa")
public class EsewaCallbackController {



    @PostMapping("/success")
    @ResponseBody
    public ApiResponse esewaSuccess(String response) {
        System.out.println("eSewa success response: " + response);
        return new ApiResponse("Payment Successful (Sandbox)", response);
    }

    @PostMapping("/failure")
    @ResponseBody
    public ApiResponse esewaFailure(String response) {
        System.out.println("eSewa failure response: " + response);
        return new ApiResponse("Payment Failed (Sandbox)", response);
    }

}
