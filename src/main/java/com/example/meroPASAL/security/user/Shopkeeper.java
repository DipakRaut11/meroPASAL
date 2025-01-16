package com.example.meroPASAL.model.user;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Shopkeeper extends User {
    private String shopName;
    private String address;
    private String panNumber;
}
