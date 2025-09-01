package com.example.meroPASAL.service.product;

import com.example.meroPASAL.Repository.CategoryRepo;
import com.example.meroPASAL.Repository.ImageRepos;
import com.example.meroPASAL.Repository.ProductRepo;
import com.example.meroPASAL.Repository.SearchHistoryRepo;
import com.example.meroPASAL.Repository.order.OrderItemRepo;
import com.example.meroPASAL.Repository.order.OrderRepository;
import com.example.meroPASAL.dto.ImageDto;
import com.example.meroPASAL.dto.ProductDto;
import com.example.meroPASAL.exception.ResourceAlreadyExistException;
import com.example.meroPASAL.exception.ResourceNotFoundException;
import com.example.meroPASAL.model.Category;
import com.example.meroPASAL.model.Image;
import com.example.meroPASAL.model.Product;
import com.example.meroPASAL.model.SearchHistory;
import com.example.meroPASAL.security.service.AuthenticationService;
import com.example.meroPASAL.security.userModel.Customer;
import com.example.meroPASAL.security.userModel.Shopkeeper;
import com.example.meroPASAL.request.product.AddProductRequest;
import com.example.meroPASAL.request.product.ProductUpdateRequest;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
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
    @Override
    public void deleteProduct(Long id) {
        Product product = productRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id " + id));

        // Check if this product exists in any order
        if (orderItemRepo.existsByProduct_Id(product.getId())) {
            throw new IllegalStateException(
                    "This product has been ordered by some customer and cannot be deleted until it is removed from orders."
            );
        }

        productRepo.delete(product);
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
        } catch (RuntimeException e) {
            // user not found → anonymous / not logged in
        }

        List<Product> allProducts = productRepo.findAll(Sort.by(Sort.Direction.ASC, "price"));

        if (customer == null) {
            return allProducts; // first-time / anonymous user
        }

        Optional<SearchHistory> lastSearchOpt = searchHistoryRepo.findTopByCustomerOrderByIdDesc(customer);

        if (lastSearchOpt.isEmpty()) {
            return allProducts; // first-time logged-in customer
        }

        SearchHistory lastSearch = lastSearchOpt.get();
        String type = lastSearch.getSearchType();
        String value = lastSearch.getSearchValue().toLowerCase();

        List<Product> prioritized = new ArrayList<>();
        List<Product> others = new ArrayList<>();

        for (Product p : allProducts) {
            boolean matches = switch (type) {
                case "brand" -> p.getBrand() != null && p.getBrand().toLowerCase().contains(value);
                case "category" -> p.getCategory() != null
                        && p.getCategory().getName() != null
                        && p.getCategory().getName().toLowerCase().contains(value);
                case "name" -> p.getName() != null && p.getName().toLowerCase().contains(value);
                default -> false;
            };
            if (matches) prioritized.add(p);
            else others.add(p);
        }

        prioritized.addAll(others);
        return prioritized;
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
        SearchHistory history = new SearchHistory("category", category, customer);
        searchHistoryRepo.save(history);
        return productRepo.findByCategory_Name(category);
    }

    @Override
    public List<Product> getProductByBrand(String brand) {
        Customer customer = (Customer) authService.getAuthenticatedUser();
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