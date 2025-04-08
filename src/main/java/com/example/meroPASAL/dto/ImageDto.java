package com.example.meroPASAL.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class ImageDto {
    private Long imageId;
    private String imageName;
    private String downloadUrl;
}
