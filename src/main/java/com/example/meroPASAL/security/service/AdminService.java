package com.example.meroPASAL.security.service;


import com.example.meroPASAL.security.repository.UserRepository;
import com.example.meroPASAL.security.userModel.Customer;
import com.example.meroPASAL.security.userModel.Shopkeeper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;

    public List<Customer> getAllCustomers() {
        return userRepository.findAllCustomers();
    }

    // Return only approved shopkeepers
    public List<Shopkeeper> getAllShopkeepers() {
        return userRepository.findAllApprovedShopkeepers();
    }

    // Return pending shopkeepers
    public List<Shopkeeper> getPendingShopkeepers() {
        return userRepository.findByApprovedFalse();
    }

    public String approveShopkeeper(Long id) {
        Shopkeeper sk = (Shopkeeper) userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Shopkeeper not found"));
        sk.setApproved(true);
        userRepository.save(sk);
        return "Shopkeeper approved successfully";
    }
}

