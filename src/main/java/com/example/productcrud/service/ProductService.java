package com.example.productcrud.service;

import com.example.productcrud.model.Product;
import com.example.productcrud.repository.ProductRepository;
import com.example.productcrud.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    private final List<Product> products = new ArrayList<>();
    private Long nextId = 7L;

    public ProductService(ProductRepository productRepository, UserRepository userRepository) {
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public List<Product> findAllByUserId(Long userId){
        return productRepository.findByUserId(userId);
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
}