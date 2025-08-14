package com.example.meroPASAL.controller;

import com.example.meroPASAL.dto.ProductDto;
import com.example.meroPASAL.exception.ResourceNotFoundException;
import com.example.meroPASAL.model.Product;
import com.example.meroPASAL.response.ApiResponse;
import com.example.meroPASAL.security.service.AuthenticationService;
import com.example.meroPASAL.security.userModel.Shopkeeper;
import com.example.meroPASAL.service.product.IProductService;
import com.example.meroPASAL.request.product.AddProductRequest;
import com.example.meroPASAL.request.product.ProductUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/products")
@RequiredArgsConstructor
public class ProductController {

    private final IProductService productService;
    private final AuthenticationService authService;


    @GetMapping("/customer/all")
    public ResponseEntity<ApiResponse> getAllProductsForCustomer() {
        List<Product> products = productService.getAllProducts();
        List<ProductDto> productDtos = productService.getConvertedProducts(products);
        return ResponseEntity.ok(new ApiResponse("All products for customer", productDtos));
    }


    @GetMapping("/shopkeeper/all")
    @PreAuthorize("hasRole('SHOPKEEPER')")
    public ResponseEntity<ApiResponse> getAllProductsForShopkeeper() {
        var user = authService.getAuthenticatedUser();
        if (!(user instanceof Shopkeeper)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse("Access denied", null));
        }
        Shopkeeper shopkeeper = (Shopkeeper) user;
        List<Product> products = productService.getProductsByShopkeeper(shopkeeper.getId());
        List<ProductDto> productDtos = productService.getConvertedProducts(products);
        return ResponseEntity.ok(new ApiResponse("All products for shopkeeper", productDtos));
    }

    // ---------------- COMMON ----------------
    @GetMapping("/product/{id}")
    public ResponseEntity<ApiResponse> getProductById(@PathVariable Long id) {
        try {
            Product product = productService.getProductById(id);
            ProductDto productDto = productService.convertToDto(product);
            return ResponseEntity.ok(new ApiResponse("Product found by id " + id, productDto));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse("Product with ID " + id + " not found", e.getMessage()));
        }
    }

    @PostMapping("/add")
    @PreAuthorize("hasRole('SHOPKEEPER')")
    public ResponseEntity<ApiResponse> addProduct(@RequestBody AddProductRequest request) {
        Product product = productService.addProduct(request);
        return ResponseEntity.ok(new ApiResponse("Product Added", productService.convertToDto(product)));
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('SHOPKEEPER')")
    public ResponseEntity<ApiResponse> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok(new ApiResponse("Product Deleted", null));
    }

    @PutMapping("/{id}/update")
    @PreAuthorize("hasRole('SHOPKEEPER')")
    public ResponseEntity<ApiResponse> updateProduct(@RequestBody ProductUpdateRequest request, @PathVariable Long id) {
        Product product = productService.updateProduct(request, id);
        return ResponseEntity.ok(new ApiResponse("Product Updated", productService.convertToDto(product)));
    }

    @GetMapping("/sorted-by-price")
    public ResponseEntity<List<Product>> getSortedProducts() {
        return ResponseEntity.ok(productService.getProductsSortedByPriceAsc());
    }
}
