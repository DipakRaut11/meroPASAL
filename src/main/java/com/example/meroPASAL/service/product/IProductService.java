package com.example.meroPASAL.service.product;

import com.example.meroPASAL.dto.ProductDto;
import com.example.meroPASAL.model.Product;
import com.example.meroPASAL.request.product.AddProductRequest;
import com.example.meroPASAL.request.product.ProductUpdateRequest;

import java.util.List;

public interface IProductService {

    Product addProduct(AddProductRequest request);
    Product getProductById(Long id);
    void deleteProduct(Long id);
    Product updateProduct(ProductUpdateRequest request, Long productId);
    List<Product> getAllProducts();
    List<Product> getProductsByCategory(String category);
    List<Product> getProductByBrand(String brand);
    List<Product> getProductByCategoryAndBrand(String category, String brand);
    List<Product> getProductByName(String name);
    List<Product> getProductByBrandAndName(String brand, String name);
    Long countProductByBrandAndName(String brand, String name);


    List<ProductDto> getConvertedProducts(List<Product> products);

    ProductDto convertToDto(Product product);

    List<Product> getProductsSortedByPriceAsc();

    List<Product> getProductsByShopkeeper(Long shopkeeperId);

}
