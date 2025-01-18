package com.example.meroPASAL.security.userModel;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Customer extends User {
    private String address;
}
