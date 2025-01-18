package com.example.meroPASAL.Repository;

import com.example.meroPASAL.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepo extends JpaRepository<Category, Long> {

    Category findByName(String name);
    boolean existsByName(String name);
}
