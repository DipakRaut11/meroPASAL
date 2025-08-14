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
import com.example.meroPASAL.security.service.AuthenticationService;
import com.example.meroPASAL.security.userModel.Shopkeeper;
import com.example.meroPASAL.request.product.AddProductRequest;
import com.example.meroPASAL.request.product.ProductUpdateRequest;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ProductService implements IProductService {

    private final ProductRepo productRepo;
    private final CategoryRepo categoryRepo;
    private final ModelMapper modelMapper;
    private final ImageRepos imageRepo;
    private final AuthenticationService authService;

    // ---------------- ADD PRODUCT ----------------
    @Override
    public Product addProduct(AddProductRequest request) {
        Shopkeeper currentShopkeeper = (Shopkeeper) authService.getAuthenticatedUser();
        if (productExist(request.getName(), request.getBrand(), currentShopkeeper)) {
            throw new ResourceAlreadyExistException("Product already exists with name "
                    + request.getName() + " and brand " + request.getBrand());
        }

        Category category = Optional.ofNullable(categoryRepo.findByName(request.getCategory().getName()))
                .orElseGet(() -> categoryRepo.save(new Category(request.getCategory().getName())));

        request.setCategory(category);

        Product product = new Product(
                request.getName(),
                request.getBrand(),
                request.getPrice(),
                request.getInventory(),
                request.getDescription(),
                category,
                currentShopkeeper
        );

        return productRepo.save(product);
    }

    private boolean productExist(String name, String brand, Shopkeeper shopkeeper) {
        return productRepo.existsByNameAndBrandAndShopkeeper(name, brand, shopkeeper);
    }

    // ---------------- GET BY ID ----------------
    @Override
    public Product getProductById(Long id) {
        return productRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id " + id));
    }

    // ---------------- DELETE ----------------
    @Override
    public void deleteProduct(Long id) {
        productRepo.findById(id)
                .ifPresentOrElse(productRepo::delete,
                        () -> { throw new ResourceNotFoundException("Product not found with id " + id); });
    }

    // ---------------- UPDATE ----------------
    @Override
    public Product updateProduct(ProductUpdateRequest request, Long productId) {
        return productRepo.findById(productId)
                .map(existingProduct -> {
                    existingProduct.setName(request.getName());
                    existingProduct.setBrand(request.getBrand());
                    existingProduct.setPrice(request.getPrice());
                    existingProduct.setInventory(request.getInventory());
                    existingProduct.setDescription(request.getDescription());

                    Category category = categoryRepo.findByName(request.getCategory().getName());
                    existingProduct.setCategory(category);

                    return existingProduct;
                })
                .map(productRepo::save)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id " + productId));
    }

    // ---------------- CUSTOMER ----------------
    @Override
    public List<Product> getAllProducts() {
        return productRepo.findAll();
    }

    // ---------------- SHOPKEEPER ----------------
    @Override
    public List<Product> getProductsByShopkeeper(Long shopkeeperId) {
        return productRepo.findByShopkeeperId(shopkeeperId);
    }

    // ---------------- FILTER ----------------
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

    // ---------------- DTO CONVERSION ----------------
    @Override
    public List<ProductDto> getConvertedProducts(List<Product> products) {
        return products.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public ProductDto convertToDto(Product product) {
        ProductDto productDto = modelMapper.map(product, ProductDto.class);

        if (product.getCategory() == null) {
            productDto.setCategory(new Category("No Category"));
        }

        List<Image> images = imageRepo.findByProduct_Id(product.getId());
        List<ImageDto> imageDtos = images != null
                ? images.stream().map(img -> modelMapper.map(img, ImageDto.class)).collect(Collectors.toList())
                : new ArrayList<>();
        productDto.setImages(imageDtos);

        return productDto;
    }

    // ---------------- SORTED BY PRICE ----------------
    @Override
    public List<Product> getProductsSortedByPriceAsc() {
        List<Product> products = productRepo.findAll();
        products.sort(Comparator.comparing(Product::getPrice));
        return products;
    }
}
