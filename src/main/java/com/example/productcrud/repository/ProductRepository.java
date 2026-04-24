package com.example.productcrud.repository;

import com.example.productcrud.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {



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
        // ==================== PAGINATION METHODS ====================

    Page<Product> findByUserId(Long userId, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE " +
            "p.user.id = :userId AND " +
            "(:categoryId IS NULL OR p.category.id = :categoryId) AND " +
            "(:keyword IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Product> findWithFilters(
            @Param("userId") Long userId,
            @Param("keyword") String keyword,
            @Param("categoryId") Long categoryId,
            Pageable pageable
    );
    //dashboard

        @Query("SELECT COUNT(p) FROM Product p WHERE p.user.id = :userId")
        Long countByUserId(@Param("userId") Long userId);

        @Query("SELECT COALESCE(SUM(p.price * p.stock), 0) FROM Product p WHERE p.user.id = :userId")
        Long sumInventoryValueByUserId(@Param("userId") Long userId);

        @Query("SELECT COUNT(p) FROM Product p WHERE p.user.id = :userId AND p.active = true")
        Long countActiveByUserId(@Param("userId") Long userId);

        @Query("SELECT COUNT(p) FROM Product p WHERE p.user.id = :userId AND p.active = false")
        Long countInactiveByUserId(@Param("userId") Long userId);

        @Query("SELECT c.name, COUNT(p) FROM Product p JOIN p.category c WHERE p.user.id = :userId GROUP BY c.id, c.name ORDER BY COUNT(p) DESC")
        List<Object[]> countProductsPerCategoryByUserId(@Param("userId") Long userId);

        @Query("SELECT p FROM Product p WHERE p.user.id = :userId AND p.stock < 5 ORDER BY p.stock ASC")
        List<Product> findLowStockByUserId(@Param("userId") Long userId);
    }