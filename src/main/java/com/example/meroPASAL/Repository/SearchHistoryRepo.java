package com.example.meroPASAL.Repository;

import com.example.meroPASAL.model.SearchHistory;
import com.example.meroPASAL.security.userModel.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SearchHistoryRepo extends JpaRepository<SearchHistory, Long> {
    Page<SearchHistory> findByCustomerOrderByIdDesc(Customer customer, Pageable pageable);
}
