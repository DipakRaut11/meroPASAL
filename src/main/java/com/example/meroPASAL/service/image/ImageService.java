package com.example.meroPASAL.service.image;

import com.example.meroPASAL.Repository.ImageRepos;
import com.example.meroPASAL.dto.ImageDto;
import com.example.meroPASAL.exception.ImageProcessingException;
import com.example.meroPASAL.exception.ResourceNotFoundException;
import com.example.meroPASAL.model.Image;
import com.example.meroPASAL.model.Product;
import com.example.meroPASAL.service.product.IProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.rowset.serial.SerialBlob;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ImageService implements IImageService {
    private final ImageRepos imageRepository;
    private final IProductService productService;

    @Override
    @Transactional(readOnly = true)
    public Image getImageById(Long id) {
        log.debug("Fetching image with id: {}", id);
        return imageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Image not found with id: " + id));
    }

    @Override
    public void deleteImageById(Long id) {
        log.info("Deleting image with id: {}", id);
        Image image = getImageById(id);
        imageRepository.delete(image);
    }

    @Override
    public List<ImageDto> saveImages(List<MultipartFile> files, Long productId) {
        if (files == null || files.isEmpty()) {
            throw new IllegalArgumentException("At least one file must be provided for upload");
        }

        Product product = productService.getProductById(productId);
        log.info("Uploading {} images for product ID: {}", files.size(), productId);

        List<ImageDto> savedImageDTOs = new ArrayList<>(files.size());

        for (MultipartFile file : files) {
            if (file.isEmpty()) {
                log.warn("Skipping empty file in upload batch");
                continue;
            }

            try {
                // Log the uploaded file size before processing
                byte[] fileBytes = file.getBytes();
                log.info("Uploaded file '{}' size: {} bytes", file.getOriginalFilename(), fileBytes.length);

                Image image = Image.builder()
                        .fileName(file.getOriginalFilename())
                        .fileType(file.getContentType())
                        .image(new SerialBlob(fileBytes))
                        .product(product)
                        .build();

                Image savedImage = imageRepository.save(image);
                savedImage.setDownloadUrl(buildDownloadUrl(savedImage.getId()));
                savedImage = imageRepository.save(savedImage);

                savedImageDTOs.add(mapToImageDto(savedImage));

                // Verify the stored image size matches the uploaded size
                if (savedImage.getImage() != null) {
                    log.debug("Stored image size: {} bytes", savedImage.getImage().length());
                }

            } catch (IOException e) {
                log.error("Failed to read file bytes: {}", file.getOriginalFilename(), e);
                throw new ImageProcessingException("Failed to read image file: " + file.getOriginalFilename(), e);
            } catch (SQLException e) {
                log.error("Failed to create SerialBlob for file: {}", file.getOriginalFilename(), e);
                throw new ImageProcessingException("Failed to process image data: " + file.getOriginalFilename(), e);
            }
        }

        return savedImageDTOs;
    }

    @Override
    public void updateImage(MultipartFile file, Long imageId) {
        log.info("Updating image with ID: {}", imageId);
        Image image = getImageById(imageId);

        try {
            updateImageData(image, file);
            imageRepository.save(image);
        } catch (IOException | SQLException e) {
            log.error("Failed to update image ID: {}", imageId, e);
            throw new ImageProcessingException("Failed to update image ID: " + imageId, e);
        }
    }


    @Override
    public ResponseEntity<Resource> downloadImage(Long imageId) {
        try {
            // 1. Fetch image from database
            Image image = getImageById(imageId);
            log.info("Downloading image: {} (Type: {}, Size: {} bytes)",
                    image.getFileName(),
                    image.getFileType(),
                    image.getImage().length());

            // 2. Extract BLOB data with error handling
            byte[] bytes;
            try {
                bytes = image.getImage().getBytes(1, (int) image.getImage().length());
                log.info("Actual bytes read: {}", bytes.length);
            } catch (SQLException e) {
                throw new ImageProcessingException("Failed to read BLOB data for image: " + imageId, e);
            }

            // 3. Determine content type (fix for JFIF files)
            String contentType = image.getFileType();
            if (image.getFileName().toLowerCase().endsWith(".jfif")) {
                contentType = "image/jpeg";
            }

            // 4. Build response
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(
                            HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + image.getFileName() + "\""
                    )
                    .body(new ByteArrayResource(bytes));

        } catch (ResourceNotFoundException e) {
            throw e; // Re-throw as is
        } catch (Exception e) {
            throw new ImageProcessingException("Failed to download image: " + imageId, e);
        }
    }

//    private Image processAndSaveImage(MultipartFile file, Product product)
//            throws IOException, SQLException {
//        Image image = createImageEntity(file, product);
//        Image savedImage = imageRepository.save(image);
//        savedImage.setDownloadUrl(buildDownloadUrl(savedImage.getId()));
//        return imageRepository.save(savedImage);
//    }

//    private Image createImageEntity(MultipartFile file, Product product)
//            throws IOException, SQLException {
//        return Image.builder()
//                .fileName(file.getOriginalFilename())
//                .fileType(file.getContentType())
//                .image(new SerialBlob(file.getBytes()))
//                .product(product)
//                .build();
//    }

    private void updateImageData(Image image, MultipartFile file)
            throws IOException, SQLException {
        image.setFileName(file.getOriginalFilename());
        image.setFileType(file.getContentType());
        image.setImage(new SerialBlob(file.getBytes()));
    }

    private ImageDto mapToImageDto(Image image) {
        return ImageDto.builder()
                .imageId(image.getId())
                .imageName(image.getFileName())
                .downloadUrl(image.getDownloadUrl())
                .build();
    }

    private String buildDownloadUrl(Long imageId) {
        return "/api/v1/image/images/download/" + imageId;
    }
}