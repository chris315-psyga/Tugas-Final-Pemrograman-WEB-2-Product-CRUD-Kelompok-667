package com.example.productcrud.repository;

import com.example.productcrud.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long>{

    List<Product> findByUserId(Long userId);

    // ==================== SEARCH & FILTER ====================

    @Query("SELECT p FROM Product p WHERE " +
            "(:keyword IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
            "(:categoryId IS NULL OR p.category.id = :categoryId) AND " +
            "p.user.id = :userId")
    List<Product> findByKeywordAndCategory(
            @Param("keyword") String keyword,
            @Param("categoryId") Long categoryId,
            @Param("userId") Long userId);
}