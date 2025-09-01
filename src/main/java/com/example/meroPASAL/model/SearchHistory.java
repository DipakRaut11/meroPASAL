package com.example.meroPASAL.model;

import com.example.meroPASAL.security.userModel.Customer;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Getter
@Setter
@NoArgsConstructor
public class SearchHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String searchType;   // "name", "brand", "category"
    private String searchValue;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    public SearchHistory(String searchType, String searchValue, Customer customer) {
        this.searchType = searchType;
        this.searchValue = searchValue;
        this.customer = customer;
    }
}
