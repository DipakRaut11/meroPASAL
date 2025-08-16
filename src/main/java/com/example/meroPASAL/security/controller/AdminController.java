package com.example.meroPASAL.security.controller;

import com.example.meroPASAL.security.service.AdminService;
import com.example.meroPASAL.security.userModel.Customer;
import com.example.meroPASAL.security.userModel.Shopkeeper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/admin")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;

    @GetMapping("/customers")
    public List<Customer> getAllCustomers() {
        return adminService.getAllCustomers();
    }

    @GetMapping("/shopkeepers")
    public List<Shopkeeper> getAllShopkeepers() {
        return adminService.getAllShopkeepers();
    }

    @GetMapping("/pending-shopkeepers")
    public List<Shopkeeper> getPendingShopkeepers() {
        return adminService.getPendingShopkeepers();
    }

    @PostMapping("/approve-shopkeeper/{id}")
    public String approveShopkeeper(@PathVariable Long id) {
        return adminService.approveShopkeeper(id);
    }

}
