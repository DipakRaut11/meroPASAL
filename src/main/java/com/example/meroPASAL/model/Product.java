package com.example.meroPASAL.model;

import com.example.meroPASAL.security.userModel.Shopkeeper;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Product name is required")
    private String name;

    @NotBlank(message = "Brand name is required")
    private String brand;

    @Positive(message = "Price must be positive")
    private BigDecimal price;

    @Positive(message = "Inventory must be positive")
    private int inventory;

    private String description;

    @JsonIgnore
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "category_id") // Foreign key mapping
    private Category category;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Image> images;

    // 🔹 New field linking to Shopkeeper
    @ManyToOne
    @JoinColumn(name = "shopkeeper_id", nullable = false)
    private Shopkeeper shopkeeper;

    public Product(String name, String brand, BigDecimal price, int inventory,
                   String description, Category category, Shopkeeper shopkeeper) {
        this.name = name;
        this.brand = brand;
        this.price = price;
        this.inventory = inventory;
        this.description = description;
        this.category = category;
        this.shopkeeper = shopkeeper;
    }
}
