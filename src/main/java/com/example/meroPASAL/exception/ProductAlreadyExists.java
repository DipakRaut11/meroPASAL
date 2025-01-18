package com.example.meroPASAL.exception;

public class ProductAlreadyExists extends RuntimeException{
    public ProductAlreadyExists(String message) {
        super(message);
    }
}
