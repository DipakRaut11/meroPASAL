package com.example.meroPASAL.service.image;

import com.example.meroPASAL.dto.ImageDto;
import com.example.meroPASAL.model.Image;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IImageService {

    Image getImageById(Long id);
    void deleteImageById(Long id);
    List<ImageDto> saveImages(List<MultipartFile> files, Long productId);
    void updateImage(MultipartFile file, Long imageId);
    ResponseEntity<Resource> downloadImage(Long imageId);
}