package com.example.meroPASAL;

import com.example.meroPASAL.Repository.ProductRepo;
import com.example.meroPASAL.model.Product;

import com.example.meroPASAL.service.product.ProductService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ProductServiceTest {
    @Mock
    private ProductRepo productRepo;

    @InjectMocks
    private ProductService productService;

    ProductServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllProducts() {
        Product p = new Product();
        p.setName("Laptop");

        when(productRepo.findAll()).thenReturn(List.of(p));

        List<Product> result = productService.getAllProducts();

        assertEquals(1, result.size());
        assertEquals("Laptop", result.get(0).getName());
    }
}
