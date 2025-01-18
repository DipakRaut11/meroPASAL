package com.example.meroPASAL.controller;

import com.example.meroPASAL.dto.ProductDto;
import com.example.meroPASAL.exception.ProductAlreadyExists;
import com.example.meroPASAL.exception.ResourceNotFoundException;
import com.example.meroPASAL.model.Product;
import com.example.meroPASAL.request.product.AddProductRequest;
import com.example.meroPASAL.request.product.ProductUpdateRequest;
import com.example.meroPASAL.response.ApiResponse;
import com.example.meroPASAL.service.product.IProductService;
import com.example.meroPASAL.service.product.ProductService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.prefix}/products")
public class ProductController {
    private final IProductService productService;

    @GetMapping("/all")
    public ResponseEntity<ApiResponse> getAllProduct(){
        try {
            List<Product> products = productService.getAllProducts();
            List<ProductDto> productDtos = productService.getConvertedProducts(products);
            return ResponseEntity.ok().body(new ApiResponse("All Products", productDtos));
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<ApiResponse>(new ApiResponse("Empty", e.getMessage()), HttpStatus.NOT_FOUND);
        }
    }
    @GetMapping("/product/{id}")
    public ResponseEntity<ApiResponse> getProductById(@PathVariable Long id){
        try {
            Product products = productService.getProductById(id);
            ProductDto productDtos = productService.convertToDto(products);
            return ResponseEntity.ok().body(new ApiResponse("Product found by id "+ id, productDtos));
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<ApiResponse>(new ApiResponse("Product with ID "+ id+" does not found", e.getMessage()), HttpStatus.NOT_FOUND);
        }
    }
    @GetMapping("/category/{category}")
    public ResponseEntity<ApiResponse> getAllProductByCategory(@PathVariable String category){
        try {
            List<Product> products = productService.getProductsByCategory(category);
            List<ProductDto> productDtos = productService.getConvertedProducts(products);
            return ResponseEntity.ok().body(new ApiResponse("Product retrieve by category name "+category, productDtos));
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<ApiResponse>(new ApiResponse("Product with category "+ category+" does not found", e.getMessage()), HttpStatus.NOT_FOUND);

        }
    }
    @GetMapping("/brand/{brand}")
    public ResponseEntity<ApiResponse> getAllProductByBrand(@PathVariable String brand){
        try {
            List<Product> products = productService.getProductByBrand(brand);
            List<ProductDto> productDtos = productService.getConvertedProducts(products);
            return ResponseEntity.ok().body(new ApiResponse("Product found by brand name "+brand, productDtos));
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<ApiResponse>(new ApiResponse("Product with brand "+ brand+" does not found", e.getMessage()), HttpStatus.NOT_FOUND);
        }
    }
    @GetMapping("/all/category/brand")
    public ResponseEntity<ApiResponse> getAllProductByCategoryAndBrand(@RequestParam String category,@RequestParam String brand){
        try {
            List<Product> products = productService.getProductByCategoryAndBrand(category, brand);
            List<ProductDto> productDtos = productService.getConvertedProducts(products);
            return ResponseEntity.ok().body(new ApiResponse("Products found by category "+category+" and brand"+brand, productDtos));
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(new ApiResponse("Product with category "+ category+" and brand "+brand+" does not found", e.getMessage()), HttpStatus.NOT_FOUND);
        }
    }
    @GetMapping("/name/{name}")
    public ResponseEntity<ApiResponse> getAllProductByName(@PathVariable String name){
        try {
            List<Product> products = productService.getProductByName(name);
            List<ProductDto> productDtos = productService.getConvertedProducts(products);
            return ResponseEntity.ok().body(new ApiResponse("Product found by name "+name, productDtos));
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<ApiResponse>(new ApiResponse("Product with name "+ name+" does not found", e.getMessage()), HttpStatus.NOT_FOUND);
        }
    }
    @GetMapping("/brand/{brand}/name/{name}")
    public ResponseEntity<ApiResponse> getAllProductByBrandAndName(@PathVariable String brand,@PathVariable String name){
        try {
            List<Product> products = productService.getProductByBrandAndName(brand, name);
            List<ProductDto> productDtos = productService.getConvertedProducts(products);
            return ResponseEntity.ok().body(new ApiResponse("Product found by brand "+brand+" and name "+name, productDtos));
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(new ApiResponse("Product with brand "+ brand+" and name "+name+" does not found", e.getMessage()), HttpStatus.NOT_FOUND);
        }
    }
    @GetMapping("/count/brand/{brand}/name/{name}")
    public ResponseEntity<ApiResponse> countProductByBrandAndName(@PathVariable String brand,@PathVariable String name){
        try {
            Long count = productService.countProductByBrandAndName(brand, name);
            return ResponseEntity.ok().body(new ApiResponse("Count of Product", count));
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(new ApiResponse("Product with brand "+ brand+" and name "+name+" does not found", e.getMessage()), HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/add")
    public ResponseEntity<ApiResponse> addProduct(@RequestBody AddProductRequest request) {
        try {
            productService.addProduct(request);
            return ResponseEntity.ok().body(new ApiResponse("Product Added", null));
        } catch (ProductAlreadyExists e) {
            return ResponseEntity.badRequest().body(new ApiResponse("Product already exists", e.getMessage()));
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse> deleteProduct(@PathVariable Long id){
        try {
            productService.deleteProduct(id);
            return ResponseEntity.ok().body(new ApiResponse("Product Deleted", null));
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(new ApiResponse("Product with id "+ id+" does not found", e.getMessage()), HttpStatus.NOT_FOUND);
        }
    }
    @PutMapping("/{id}/update")
    public ResponseEntity<ApiResponse> updateProduct(@RequestBody ProductUpdateRequest request,@PathVariable Long id){
        try {
            productService.updateProduct(request, id);
            return ResponseEntity.ok().body(new ApiResponse("Product Updated", null));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND)
                    .body(new ApiResponse(e.getMessage(), null));        }

    }








}
