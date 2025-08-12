package com.example.meroPASAL.Repository;

import com.example.meroPASAL.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepo extends JpaRepository<Product, Long> {

    boolean existsByNameAndBrand(String name, String brand);

    List<Product> findByCategory_Name(String categoryName);

    List<Product> findByBrand(String brand);

    // Updated query method
    List<Product> findProductByBrandAndCategory_Name(String brand, String categoryName);

    List<Product> findProductByName(String name);

    List<Product> findProductByBrandAndName(String brand, String name);

    Long countByBrandAndName(String brand, String name);

}
