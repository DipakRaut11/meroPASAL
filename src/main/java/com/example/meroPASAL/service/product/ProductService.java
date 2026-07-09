package com.example.meroPASAL.service.product;

import com.example.meroPASAL.Repository.*;
import com.example.meroPASAL.Repository.order.OrderItemRepo;
import com.example.meroPASAL.dto.ImageDto;
import com.example.meroPASAL.dto.ProductDto;
import com.example.meroPASAL.exception.ResourceAlreadyExistException;
import com.example.meroPASAL.exception.ResourceNotFoundException;
import com.example.meroPASAL.model.*;
import com.example.meroPASAL.security.service.AuthenticationService;
import com.example.meroPASAL.security.userModel.Customer;
import com.example.meroPASAL.security.userModel.Shopkeeper;
import com.example.meroPASAL.request.product.AddProductRequest;
import com.example.meroPASAL.request.product.ProductUpdateRequest;
import com.example.meroPASAL.service.recommendation.RecommendationService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ProductService implements IProductService {

    private final ProductRepo productRepo;
    private final CategoryRepo categoryRepo;
    private final ModelMapper modelMapper;
    private final ImageRepos imageRepo;
    private final AuthenticationService authService;
    private final OrderItemRepo orderItemRepo;
    private final SearchHistoryRepo searchHistoryRepo;
    private final RecommendationService recommendationService;

    // ---------------- ADD PRODUCT ----------------
    @Override
    public Product addProduct(AddProductRequest request) {
        Shopkeeper currentShopkeeper = (Shopkeeper) authService.getAuthenticatedUser();
        if (productExist(request.getName(), request.getBrand(), currentShopkeeper)) {
            throw new ResourceAlreadyExistException("Product already exists with name "
                    + request.getName() + " and brand " + request.getBrand());
        }

        Category category = Optional.ofNullable(categoryRepo.findByName(request.getCategory().getName()))
                .orElseGet(() -> categoryRepo.save(new Category(request.getCategory().getName())));

        request.setCategory(category);

        Product product = new Product(
                request.getName(),
                request.getBrand(),
                request.getPrice(),
                request.getInventory(),
                request.getDescription(),
                category,
                currentShopkeeper
        );

        return productRepo.save(product);
    }

    private boolean productExist(String name, String brand, Shopkeeper shopkeeper) {
        return productRepo.existsByNameAndBrandAndShopkeeper(name, brand, shopkeeper);
    }

    // ---------------- GET BY ID ----------------
    @Override
    public Product getProductById(Long id) {
        return productRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id " + id));
    }

    // ---------------- DELETE ----------------
    @Transactional
    @Override
    public void deleteProduct(Long id) {
        Product product = productRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id " + id));

        boolean exists = orderItemRepo.existsByProductId(product.getId());

        if (exists) {
            throw new IllegalStateException("Product already used in orders. Cannot delete.");
        }

        productRepo.deleteById(id); // safer than delete(entity)
    }



    // ---------------- UPDATE ----------------
    @Override
    public Product updateProduct(ProductUpdateRequest request, Long productId) {
        return productRepo.findById(productId)
                .map(existingProduct -> {
                    existingProduct.setName(request.getName());
                    existingProduct.setBrand(request.getBrand());
                    existingProduct.setPrice(request.getPrice());
                    existingProduct.setInventory(request.getInventory());
                    existingProduct.setDescription(request.getDescription());

                    Category category = categoryRepo.findByName(request.getCategory().getName());
                    existingProduct.setCategory(category);

                    return existingProduct;
                })
                .map(productRepo::save)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id " + productId));
    }

    // ---------------- CUSTOMER ----------------

    @Override
    public List<Product> getAllProducts() {

        Customer customer = null;

        try {
            var user = authService.getAuthenticatedUser();
            if (user instanceof Customer c) {
                customer = c;
            }
        } catch (Exception e) {
            customer = null;
        }

        List<Product> products = productRepo.findAll();

        // guest user → simple sorting
        if (customer == null) {
            return products.stream()
                    .sorted(Comparator.comparing(Product::getPrice))
                    .toList();
        }

        // load learned preferences
        List<UserPreference> prefs = recommendationService.getPreferences(customer);

        Map<String, Integer> categoryWeight = new HashMap<>();
        Map<String, Integer> brandWeight = new HashMap<>();

        for (UserPreference p : prefs) {
            if (p.getType().equals("category")) {
                categoryWeight.put(p.getValue(), p.getWeight());
            } else if (p.getType().equals("brand")) {
                brandWeight.put(p.getValue(), p.getWeight());
            }
        }

        List<ScoredProduct> scored = new ArrayList<>();

        for (Product p : products) {

            int score = 0;

            // 🧠 AI LEARNING CORE
            if (p.getCategory() != null) {
                score += categoryWeight.getOrDefault(
                        p.getCategory().getName().toLowerCase(), 0
                );
            }

            if (p.getBrand() != null) {
                score += brandWeight.getOrDefault(
                        p.getBrand().toLowerCase(), 0
                );
            }

            // 🔥 popularity boost
            int orders = orderItemRepo.countByProduct_Id(p.getId());
            score += Math.min(orders, 5);

            scored.add(new ScoredProduct(p, score));
        }

        // AI ranking
        scored.sort((a, b) -> Integer.compare(b.score, a.score));

        return scored.stream()
                .map(s -> s.product)
                .toList();
    }


    // helper class
    private static class ScoredProduct {
        Product product;
        int score;

        ScoredProduct(Product product, int score) {
            this.product = product;
            this.score = score;
        }
    }



    // ---------------- SHOPKEEPER ----------------
    @Override
    public List<Product> getProductsByShopkeeper(Long shopkeeperId) {
        return productRepo.findByShopkeeperId(shopkeeperId);
    }

    // ---------------- FILTER ----------------
    @Override
    public List<Product> getProductsByCategory(String category) {

        Customer customer = (Customer) authService.getAuthenticatedUser();

        recommendationService.updatePreference(customer, "category", category, 1);

        SearchHistory history = new SearchHistory("category", category, customer);
        searchHistoryRepo.save(history);

        return productRepo.findByCategory_Name(category);
    }

    @Override
    public List<Product> getProductByBrand(String brand) {

        Customer customer = (Customer) authService.getAuthenticatedUser();

        recommendationService.updatePreference(customer, "brand", brand, 1);

        SearchHistory history = new SearchHistory("brand", brand, customer);
        searchHistoryRepo.save(history);

        return productRepo.findByBrand(brand);
    }

    @Override
    public List<Product> getProductByCategoryAndBrand(String category, String brand) {

        return productRepo.findProductByBrandAndCategory_Name(brand, category);
    }

    @Override
    public List<Product> getProductByName(String name) {
        Customer customer = (Customer) authService.getAuthenticatedUser();
        SearchHistory history = new SearchHistory("name", name, customer);
        searchHistoryRepo.save(history);
        return productRepo.findProductByName(name);
    }

    @Override
    public List<Product> getProductByBrandAndName(String brand, String name) {
        return productRepo.findProductByBrandAndName(brand, name);
    }

    @Override
    public Long countProductByBrandAndName(String brand, String name) {
        return productRepo.countByBrandAndName(brand, name);
    }

    // ---------------- DTO CONVERSION ----------------
    @Override
    public List<ProductDto> getConvertedProducts(List<Product> products) {
        return products.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public ProductDto convertToDto(Product product) {
        ProductDto productDto = modelMapper.map(product, ProductDto.class);

        if (product.getCategory() == null) {
            productDto.setCategory(new Category("No Category"));
        }

        List<Image> images = imageRepo.findByProduct_Id(product.getId());
        List<ImageDto> imageDtos = images != null
                ? images.stream().map(img -> modelMapper.map(img, ImageDto.class)).collect(Collectors.toList())
                : new ArrayList<>();
        productDto.setImages(imageDtos);

        return productDto;
    }

    // ---------------- SORTED BY PRICE ----------------
    @Override
    public List<Product> getProductsSortedByPriceAsc() {
        List<Product> products = productRepo.findAll();
        products.sort(Comparator.comparing(Product::getPrice));
        return products;
    }
}