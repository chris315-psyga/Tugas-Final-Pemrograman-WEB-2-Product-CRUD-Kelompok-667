package com.example.productcrud.repository;

import com.example.productcrud.model.Category;
import com.example.productcrud.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByUserId(Long userId);
    @Query("SELECT c FROM Category c WHERE c.user.id = :userId AND c.name = :name")
    Category findByUserIdAndName(@Param("userId") Long userId, @Param("name") String name);
}
