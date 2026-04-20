package com.example.productcrud.service;

import com.example.productcrud.model.Product;
import com.example.productcrud.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    // ⭐ KONSTANTA: 10 PRODUK PER HALAMAN (sesuai requirement!)
    private static final int PAGE_SIZE = 10;

    /**
     * Method utama untuk mendapatkan produk dengan pagination
     *
     * @param page Nomor halaman (dimulai dari 0)
     * @param keyword Kata kunci pencarian (opsional)
     * @param categoryId ID kategori untuk filter (opsional)
     * @param active Status aktif produk (opsional)
     * @return Page<Product> berisi data dan metadata pagination
     */
    public Page<Product> getProducts(int page, String keyword, Long categoryId, Boolean active) {

        // Buat objek Pageable dengan ukuran 10 per halaman
        Pageable pageable = PageRequest.of(page, PAGE_SIZE);

        // Cek apakah ada parameter filter
        boolean hasKeyword = keyword != null && !keyword.trim().isEmpty();
        boolean hasCategory = categoryId != null;
        boolean hasActiveFilter = active != null;

        // Jika ada filter, gunakan query combined
        if (hasKeyword || hasCategory || hasActiveFilter) {
            return productRepository.findWithFilters(
                    hasKeyword ? keyword.trim() : null,
                    categoryId,
                    active,
                    pageable
            );
        }

        // Default: ambil semua tanpa filter
        return productRepository.findAll(pageable);
    }

    /**
     * Hitung total semua produk (untuk info "Showing X to Y of Z entries")
     */
    public long getTotalProducts() {
        return productRepository.count();
    }
}