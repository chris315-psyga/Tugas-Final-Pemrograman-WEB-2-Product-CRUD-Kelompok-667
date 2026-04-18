package com.example.productcrud.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.productcrud.model.User;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}
