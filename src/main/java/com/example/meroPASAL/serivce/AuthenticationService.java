package com.example.meroPASAL.serivce;

import com.example.meroPASAL.dto.LoginRequest;
import com.example.meroPASAL.dto.SignUpRequest;
import com.example.meroPASAL.exception.CustomException;
import com.example.meroPASAL.model.user.Customer;
import com.example.meroPASAL.model.user.Shopkeeper;

import com.example.meroPASAL.response.SignUpResponse;

import com.example.meroPASAL.userRepo.CustomerRepo;
import com.example.meroPASAL.userRepo.ShopkeeperRepo;
import com.example.meroPASAL.utils.PasswordEncoderUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final CustomerRepo customerRepository;

    private final ShopkeeperRepo shopkeeperRepository;

    public SignUpResponse signupCustomer(SignUpRequest request) {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new CustomException("Passwords do not match");
        }
        if (customerRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new CustomException("Email already exists");
        }

        Customer customer = new Customer();
        customer.setName(request.getName());
        customer.setEmail(request.getEmail());
        customer.setPassword(PasswordEncoderUtil.encode(request.getPassword()));

        customerRepository.save(customer);
        return new SignUpResponse("Customer registered successfully");
    }

    public SignUpResponse signupShopkeeper(SignUpRequest request) {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new CustomException("Passwords do not match");
        }
        if (shopkeeperRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new CustomException("Email already exists");
        }

        Shopkeeper shopkeeper = new Shopkeeper();
        shopkeeper.setShopName(request.getShopName());
        shopkeeper.setEmail(request.getEmail());
        shopkeeper.setPassword(PasswordEncoderUtil.encode(request.getPassword()));
        shopkeeper.setPAN(request.getPAN());

        shopkeeperRepository.save(shopkeeper);
        return new SignUpResponse("Shopkeeper registered successfully");
    }

    public String loginCustomer(LoginRequest request) {
        Optional<Customer> customer = customerRepository.findByEmail(request.getEmail());
        if (customer.isPresent() && PasswordEncoderUtil.matches(request.getPassword(), customer.get().getPassword())) {
            return "Customer logged in successfully";
        }
        throw new CustomException("Invalid email or password");
    }

    public String loginShopkeeper(LoginRequest request) {
        Optional<Shopkeeper> shopkeeper = shopkeeperRepository.findByEmail(request.getEmail());
        if (shopkeeper.isPresent() && PasswordEncoderUtil.matches(request.getPassword(), shopkeeper.get().getPassword())) {
            return "Shopkeeper logged in successfully";
        }
        throw new CustomException("Invalid email or password");
    }
}
