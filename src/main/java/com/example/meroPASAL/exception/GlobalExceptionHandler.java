package com.example.meroPASAL.exception;

import com.example.meroPASAL.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> handleAllExceptions(Exception ex) {
        return new ResponseEntity<>(
                new ApiResponse("Error", ex.getMessage()),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse> handleResourceNotFound(ResourceNotFoundException ex) {
        return new ResponseEntity<>(
                new ApiResponse("Not Found", ex.getMessage()),
                HttpStatus.NOT_FOUND
        );
    }
}

