package com.example.meroPASAL.service.product;

import com.example.meroPASAL.Repository.CategoryRepo;
import com.example.meroPASAL.Repository.ImageRepos;
import com.example.meroPASAL.Repository.ProductRepo;
import com.example.meroPASAL.dto.ImageDto;
import com.example.meroPASAL.dto.ProductDto;
import com.example.meroPASAL.exception.ResourceAlreadyExistException;
import com.example.meroPASAL.exception.ResourceNotFoundException;
import com.example.meroPASAL.model.Category;
import com.example.meroPASAL.model.Image;
import com.example.meroPASAL.model.Product;
import com.example.meroPASAL.request.product.AddProductRequest;
import com.example.meroPASAL.request.product.ProductUpdateRequest;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ProductService implements IProductService {

    private final ProductRepo productRepo;
    private final CategoryRepo categoryRepo;
    private final ModelMapper modelMapper;
    private final ImageRepos imageRepo;

    @Override
    public Product addProduct(AddProductRequest request) {
        if (productExist(request.getName(), request.getBrand())) {
            throw new ResourceAlreadyExistException("Product already exists with name "
                    + request.getName() + " and brand " + request.getBrand());
        }

        Category category = Optional.ofNullable(categoryRepo.findByName(request.getCategory().getName()))
                .orElseGet(() -> {
                    Category newCategory = new Category(request.getCategory().getName());
                    return categoryRepo.save(newCategory);
                });

        request.setCategory(category);
        return productRepo.save(createProduct(request, category));
    }

    private boolean productExist(String name, String brand) {
        return productRepo.existsByNameAndBrand(name, brand);
    }

    private Product createProduct(AddProductRequest request, Category category) {
        return new Product(
                request.getName(),
                request.getBrand(),
                request.getPrice(),
                request.getInventory(),
                request.getDescription(),
                category
        );
    }

    @Override
    public Product getProductById(Long id) {
        return productRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id " + id));
    }

    @Override
    public void deleteProduct(Long id) {
        productRepo.findById(id)
                .ifPresentOrElse(productRepo::delete,
                        () -> { throw new ResourceNotFoundException("Product not found with id " + id); });
    }

    @Override
    public Product updateProduct(ProductUpdateRequest request, Long productId) {
        return productRepo.findById(productId)
                .map(existingProduct -> updateExistingProduct(existingProduct, request))
                .map(productRepo::save)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id " + productId));
    }

    private Product updateExistingProduct(Product existingProduct, ProductUpdateRequest request) {
        existingProduct.setName(request.getName());
        existingProduct.setBrand(request.getBrand());
        existingProduct.setPrice(request.getPrice());
        existingProduct.setInventory(request.getInventory());
        existingProduct.setDescription(request.getDescription());

        Category category = categoryRepo.findByName(request.getCategory().getName());
        existingProduct.setCategory(category);
        return existingProduct;
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepo.findAll();
    }

    @Override
    public List<Product> getProductsByCategory(String category) {
        return productRepo.findByCategory_Name(category);
    }

    @Override
    public List<Product> getProductByBrand(String brand) {
        return productRepo.findByBrand(brand);
    }

    @Override
    public List<Product> getProductByCategoryAndBrand(String category, String brand) {
        return productRepo.findProductByBrandAndCategory_Name(brand, category);
    }

    @Override
    public List<Product> getProductByName(String name) {
        return productRepo.findProductByName(name);
    }

    @Override
    public List<Product> getProductByBrandAndName(String brand, String name) {
        return productRepo.findProductByBrandAndName(brand, name);
    }

    @Override
    public Long countProductByBrandAndName(String brand, String name) {
        return productRepo.countByBrandAndName(brand, name);
    }

    @Override
    public List<ProductDto> getConvertedProducts(List<Product> products) {
        return products.stream()
                .map(this::convertToDto).toList();
    }

    @Override
    public ProductDto convertToDto(Product product) {
        ProductDto productDto = modelMapper.map(product, ProductDto.class);
        List<Image> images = imageRepo.findByProduct_Id(product.getId());
        List<ImageDto> imageDtos = images
                .stream()
                .map(image -> modelMapper.map(image, ImageDto.class)).toList();
        productDto.setImages(imageDtos);

        return productDto;
    }
}
