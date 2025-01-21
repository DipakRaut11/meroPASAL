package com.example.meroPASAL.service.category;

import com.example.meroPASAL.Repository.CategoryRepo;
import com.example.meroPASAL.exception.ResourceAlreadyExistException;
import com.example.meroPASAL.exception.ResourceNotFoundException;
import com.example.meroPASAL.model.Category;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryService implements ICategoryService{

    private final CategoryRepo categoryRepo;
    private final ModelMapper modelMapper;

    @Override
    public Category getCategoryById(Long id) {
        return categoryRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id " + id));
    }

    @Override
    public void deleteCategoryById(Long id) {
        categoryRepo.findById(id)
                .ifPresentOrElse(category -> categoryRepo.deleteById(id),
                        () -> new ResourceNotFoundException("Category not found with id " + id));

    }

    @Override
    public Category addCategory(Category category) {
         return Optional.of(category)
                .filter(c -> !categoryRepo.existsByName(c.getName()))
                .map(categoryRepo::save)
                .orElseThrow(()-> new ResourceAlreadyExistException(category.getName()+" Category already exists"));
    }

    @Override
    public Category updateCategory(Category category, Long categoryId) {
        return Optional.ofNullable(getCategoryById(categoryId))
                .map(oldCategory -> {
                    oldCategory.setName(category.getName());
                    return categoryRepo.save(oldCategory);
                })
                .orElseThrow(()-> new ResourceNotFoundException("Category not found"));
    }

    @Override
    public List<Category> getAllCategories() {
        return categoryRepo.findAll();
    }

    @Override
    public Category getCategoriesByName(String name) {
        return categoryRepo.findByName(name);
    }
}
