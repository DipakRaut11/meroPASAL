package com.example.meroPASAL.security.userModel;

import jakarta.persistence.Basic;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.Pattern;

@Entity
@Getter
@Setter
public class Shopkeeper extends User {
    private String shopName;
    private String address;
    @Pattern(regexp = "^\\d{9}$", message = "PAN must be exactly 9 digits (numbers only).")
    private String panNumber;


    @NotNull(message = "Business QR is required")
    @Lob
    @Basic(fetch = FetchType.LAZY)
    private byte[] businessQR;

}
