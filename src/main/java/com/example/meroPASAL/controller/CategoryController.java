package com.example.meroPASAL.controller;

import com.example.meroPASAL.model.Category;
import com.example.meroPASAL.response.ApiResponse;
import com.example.meroPASAL.service.category.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.prefix}/category")
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping("/all")
    public ResponseEntity<ApiResponse> getAllCategories(){
            List<Category> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(new ApiResponse("Categories fetched successfully", categories));
    }
    @GetMapping("/id/{id}")
    public ResponseEntity<ApiResponse> getCategoryById(@PathVariable Long id){
        Category category = categoryService.getCategoryById(id);
        return ResponseEntity.ok(new ApiResponse("Category fetched successfully", category));
    }
    @GetMapping("/name/{name}")
    public ResponseEntity<ApiResponse> getCategoryByName(@PathVariable String name){
        Category category = categoryService.getCategoriesByName(name);
        return ResponseEntity.ok(new ApiResponse("Category fetched successfully", category));
    }
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ROLE_SHOPKEEPER')")
    public ResponseEntity<ApiResponse> deleteCategoryById(@PathVariable Long id){
        categoryService.deleteCategoryById(id);
       return ResponseEntity.ok(new ApiResponse("Category deleted successfully", null));
    }
    @PostMapping("/add")
    @PreAuthorize("hasRole('ROLE_SHOPKEEPER')")
    public ResponseEntity<ApiResponse> addCategory(@RequestBody Category category){
        Category cat = categoryService.addCategory(category);
        return ResponseEntity.ok(new ApiResponse("Category added successfully", cat));
    }
    @PutMapping("update/{id}")
    @PreAuthorize("hasRole('ROLE_SHOPKEEPER')")
    public ResponseEntity<ApiResponse> updateCategory(@RequestBody Category category, @PathVariable Long id){
        Category cat = categoryService.updateCategory(category, id);
        return ResponseEntity.ok(new ApiResponse("Category updated successfully", cat));
    }



}
