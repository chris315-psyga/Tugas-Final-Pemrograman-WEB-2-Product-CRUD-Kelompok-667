package com.example.productcrud.service;

import com.example.productcrud.model.Product;
import com.example.productcrud.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    // ⭐ KONSTANTA: 10 PRODUK PER HALAMAN (sesuai requirement!)
    private static final int PAGE_SIZE = 10;
    public Page<Product> getProducts(int page, String keyword, Long categoryId, Long userId) {
        Pageable pageable = PageRequest.of(page, PAGE_SIZE);

        boolean hasKeyword = keyword != null && !keyword.trim().isEmpty();
        boolean hasCategory = categoryId != null;

        if (hasKeyword || hasCategory) {
            return productRepository.findWithFilters(
                    userId,
                    hasKeyword ? keyword.trim() : null,
                    categoryId,
                    pageable
            );
        }

        return productRepository.findByUserId(userId, pageable);
    }

    // ==================== SEARCH & FILTER METHOD ====================

    public List<Product> findByKeywordAndCategory(String keyword, Long categoryId, Long userId){
        return productRepository.findByKeywordAndCategory(keyword, categoryId, userId);
    }

    public Optional<Product> findById(Long id) {
        return productRepository.findById(id);
    }

    public Product save(Product product) {
        return productRepository.save(product);
    }

    public void deleteById(Long id) {
        productRepository.deleteById(id);
    }

    //Dashboard

    public Long countByUserId(Long userId) {
        return productRepository.countByUserId(userId);
    }

    public Long sumInventoryValueByUserId(Long userId) {
        return productRepository.sumInventoryValueByUserId(userId);
    }

    public Long countActiveByUserId(Long userId) {
        return productRepository.countActiveByUserId(userId);
    }

    public Long countInactiveByUserId(Long userId) {
        return productRepository.countInactiveByUserId(userId);
    }

    public List<Object[]> countProductsPerCategoryByUserId(Long userId) {
        return productRepository.countProductsPerCategoryByUserId(userId);
    }

    public List<Product> findLowStockByUserId(Long userId) {
        return productRepository.findLowStockByUserId(userId);
    }
}