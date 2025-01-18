package com.example.meroPASAL.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ApiResponse {
    private final String message;
    private Object Data;
}
