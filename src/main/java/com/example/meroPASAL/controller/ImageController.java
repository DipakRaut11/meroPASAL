package com.example.meroPASAL.controller;

import com.example.meroPASAL.dto.ImageDto;
import com.example.meroPASAL.exception.ResourceNotFoundException;
import com.example.meroPASAL.model.Image;
import com.example.meroPASAL.response.ApiResponse;
import com.example.meroPASAL.service.image.IImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.sql.SQLException;
import java.util.List;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.prefix}/image")
public class ImageController {
    private final IImageService iImageService;

    @PostMapping("/upload")
    public ResponseEntity<ApiResponse> saveImages(@RequestParam List<MultipartFile> file, @RequestParam Long productId) {

        try {
            List<ImageDto> imageDtos = iImageService.saveImages(file, productId);
            return ResponseEntity.ok(new ApiResponse("Images saved successfully", imageDtos));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("Error while saving images", e.getMessage()));
        }

    }

//    @GetMapping("/images/download/{imageId}")
//    public ResponseEntity<Resource> downloadImage(@PathVariable Long imageId) throws SQLException {
//        Image image = iImageService.getImageById(imageId);
//        ByteArrayResource resource = new ByteArrayResource(image.getImage()
//                .getBytes(1, (int) image.getImage().length()));
//
//        return ResponseEntity.ok().contentType(MediaType.parseMediaType(image.getFileType()))
//                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + image.getFileName() + "\"")
//                .body(resource);
//    }

    @GetMapping("/images/download/{imageId}")
    public ResponseEntity<Resource> downloadImage(@PathVariable Long imageId) {
        return iImageService.downloadImage(imageId);
    }

    @PutMapping("/image/{imageId}/update")
    public ResponseEntity<ApiResponse> updateImage(@PathVariable Long imageId, @RequestParam MultipartFile file) {
        try {
            Image image = iImageService.getImageById(imageId);

            if (image != null){
                iImageService.updateImage(file, imageId);
                return ResponseEntity.ok(new ApiResponse("Image updated successfully", null));
            }
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse( e.getMessage(), null));
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse("Error while updating image", INTERNAL_SERVER_ERROR));


    }
    @DeleteMapping("/image/{imageId}/delete")
    public ResponseEntity<ApiResponse> deleteImage(@PathVariable Long imageId) {
        try {
            Image image = iImageService.getImageById(imageId);

            if (image != null){
                iImageService.deleteImageById(imageId);
                return ResponseEntity.ok(new ApiResponse("Image delete successfully", null));
            }
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse( e.getMessage(), null));
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse("Error while deleting image", INTERNAL_SERVER_ERROR));


    }
    @GetMapping("/images/view/{imageId}")
    public ResponseEntity<Resource> viewImage(@PathVariable Long imageId) throws SQLException {
        Image image = iImageService.getImageById(imageId);
        ByteArrayResource resource = new ByteArrayResource(image.getImage()
                .getBytes(1, (int) image.getImage().length()));

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(image.getFileType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + image.getFileName() + "\"")
                .body(resource);
    }


}
