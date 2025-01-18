package com.example.meroPASAL.service.category;

import com.example.meroPASAL.model.Category;

import java.util.List;

public class CategoryService implements ICategoryService{
    @Override
    public Category getCategoryById(Long id) {
        return null;
    }

    @Override
    public void deleteCategory(Long id) {

    }

    @Override
    public Category addCategory(Category category) {
        return null;
    }

    @Override
    public Category updateCategory(Category category, Long categoryId) {
        return null;
    }

    @Override
    public List<Category> getAllCategories() {
        return List.of();
    }

    @Override
    public Category getCategoriesByName(String name) {
        return null;
    }
}
