package com.example.meroPASAL.security.repository;

import com.example.meroPASAL.security.userModel.Customer;
import com.example.meroPASAL.security.userModel.Shopkeeper;
import com.example.meroPASAL.security.userModel.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);

    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END FROM Shopkeeper s WHERE s.panNumber = :panNumber")
    boolean existsByPanNumber(@Param("panNumber") String panNumber);

    // ✅ Fetch all customers for admin
    @Query("SELECT c FROM Customer c")
    List<Customer> findAllCustomers();

    // ✅ Fetch all shopkeepers for admin
    @Query("SELECT s FROM Shopkeeper s WHERE s.approved = true")
    List<Shopkeeper> findAllApprovedShopkeepers();

    // ✅ Fetch pending shopkeepers (approved = false)
    @Query("SELECT s FROM Shopkeeper s WHERE s.approved = false")
    List<Shopkeeper> findPendingShopkeepers();

    List<Shopkeeper> findByApprovedFalse();

    @Query("SELECT a FROM Admin a WHERE a.email = :email")
    Optional<User> findAdminByEmail(@Param("email") String email);





}
