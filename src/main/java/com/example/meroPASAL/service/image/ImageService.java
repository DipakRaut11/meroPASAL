package com.example.meroPASAL.service.image;

import com.example.meroPASAL.Repository.ImageRepos;
import com.example.meroPASAL.dto.ImageDto;
import com.example.meroPASAL.exception.ResourceNotFoundException;
import com.example.meroPASAL.model.Image;
import com.example.meroPASAL.model.Product;
import com.example.meroPASAL.service.product.IProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.rowset.serial.SerialBlob;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ImageService implements IImageService{
    private final ImageRepos imageRepository;
    private final IProductService productService;

    @Override
    public Image getImageById(Long id) {
        return imageRepository.findById(id).
                orElseThrow(()-> new ResourceNotFoundException("Image not found "+id));
    }

    @Override
    public void deleteImageById(Long id) {
        imageRepository.findById(id)
                .ifPresentOrElse(imageRepository::delete,
                        ()-> {
                            throw new ResourceNotFoundException("Image not found");
                        }
                );


    }

    @Override
    public List<ImageDto> saveImages(List<MultipartFile> files, Long productId) {
        if (files == null || files.isEmpty()) {
            throw new RuntimeException("No files provided for upload");
        }

        // Retrieve product
        Product product = productService.getProductById(productId);
        System.out.println("✅ Product found: " + product.getName());

        List<ImageDto> savedImageDTo = new ArrayList<>();

        for (MultipartFile file : files) {
            try {
                Image image = new Image();
                image.setFileName(file.getOriginalFilename());
                image.setFileType(file.getContentType());
                image.setImage(new SerialBlob(file.getBytes()));
                image.setProduct(product);

                // Save image first
                Image savedImage = imageRepository.save(image);

                // Generate download URL
                String buildDownloadUrl = "/api/v1/image/images/download/" + savedImage.getId();
                savedImage.setDownloadUrl(buildDownloadUrl);
                imageRepository.save(savedImage); // Save again with updated URL

                // Prepare DTO response
                ImageDto imageDto = new ImageDto();
                imageDto.setImageId(savedImage.getId());
                imageDto.setImageName(savedImage.getFileName());
                imageDto.setDownloadUrl(savedImage.getDownloadUrl());
                savedImageDTo.add(imageDto);

            } catch (IOException | SQLException e) {
                throw new RuntimeException("Failed to upload image: " + e.getMessage());
            }
        }

        return savedImageDTo;
    }


    @Override
    public void updateImage(MultipartFile file, Long imageId) {
        Image image = getImageById(imageId);
        try {
            image.setFileName(file.getOriginalFilename());
            image.setFileName(file.getOriginalFilename());

            image.setImage(new SerialBlob(file.getBytes()));
        } catch (IOException | SQLException e) {
            throw new RuntimeException("Failed to update image "+e.getMessage());
        }

    }

}
