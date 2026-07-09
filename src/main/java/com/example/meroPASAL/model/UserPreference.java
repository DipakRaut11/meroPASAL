package com.example.meroPASAL.model;

import com.example.meroPASAL.security.userModel.Customer;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserPreference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String type;   // category / brand
    private String value;  // e.g. "mobile", "samsung"

    private int weight;    // learning score

    @ManyToOne
    private Customer customer;

}
