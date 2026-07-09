package com.example.meroPASAL.Repository;

import com.example.meroPASAL.model.UserPreference;
import com.example.meroPASAL.security.userModel.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserPreferenceRepo extends JpaRepository<UserPreference, Long> {

    List<UserPreference> findByCustomer(Customer customer);

    Optional<UserPreference> findByCustomerAndTypeAndValue(
            Customer customer,
            String type,
            String value
    );
}