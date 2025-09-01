package com.example.meroPASAL.dto;
import lombok.Data;

@Data
public class SaveSearchRequest {
    private String searchType;   // "name", "brand", "category"
    private String searchValue;
}
