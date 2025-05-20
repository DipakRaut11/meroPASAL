package com.example.meroPASAL.Repository;

import com.example.meroPASAL.model.Image;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ImageRepos extends JpaRepository<Image, Long> {
    List<Image> findByProduct_Id(Long id);
    //Optional<Image> findByFileName(String fileName);
}
