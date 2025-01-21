package com.example.meroPASAL.service.category;

import com.example.meroPASAL.model.Category;

import java.util.List;

public interface ICategoryService {
    Category getCategoryById(Long id);
    void deleteCategoryById(Long id);
    Category addCategory(Category category);
    Category updateCategory(Category category, Long categoryId);
    List<Category> getAllCategories();
    Category getCategoriesByName(String name);
}
