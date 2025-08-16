package com.example.meroPASAL.security.repository;

import com.example.meroPASAL.security.userModel.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);

    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END FROM Shopkeeper s WHERE s.panNumber = :panNumber")
    boolean existsByPanNumber(@Param("panNumber") String panNumber);


}
