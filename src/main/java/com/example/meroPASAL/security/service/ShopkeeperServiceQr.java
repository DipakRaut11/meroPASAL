package com.example.meroPASAL.security.service;


import com.example.meroPASAL.exception.ResourceNotFoundException;
import com.example.meroPASAL.security.repository.ShopkeeperRepositoryQr;
import com.example.meroPASAL.security.userModel.Shopkeeper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ShopkeeperServiceQr {
    private final ShopkeeperRepositoryQr shopkeeperRepository;

    public byte[] getBusinessQR(Long shopkeeperId) {
        Shopkeeper shopkeeper = shopkeeperRepository.findById(shopkeeperId)
                .orElseThrow(() -> new ResourceNotFoundException("Shopkeeper not found with id: " + shopkeeperId));
        return shopkeeper.getBusinessQR();
    }

}
