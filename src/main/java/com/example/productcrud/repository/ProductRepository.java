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

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

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
    // ==================== PAGINATION METHODS ====================

    // Method 1: Ambil semua produk dengan pagination
    Page<Product> findAll(Pageable pageable);

    // Method 2: Cari berdasarkan keyword (nama atau deskripsi)
    @Query("SELECT p FROM Product p WHERE " +
            "LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Product> findByKeyword(@Param("keyword") String keyword, Pageable pageable);

    // Method 3: Filter berdasarkan kategori ID
    Page<Product> findByCategoryId(Long categoryId, Pageable pageable);

    // Method 4: Filter berdasarkan status aktif
    Page<Product> findByActive(boolean active, Pageable pageable);

    // Method 5: Combined filter (keyword + category + active) - PALING PENTING!
    @Query("SELECT p FROM Product p WHERE " +
            "(:categoryId IS NULL OR p.category.id = :categoryId) AND " +
            "(:active IS NULL OR p.active = :active) AND " +
            "(:keyword IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Product> findWithFilters(
            @Param("keyword") String keyword,
            @Param("categoryId") Long categoryId,
            @Param("active") Boolean active,
            Pageable pageable
    );
}