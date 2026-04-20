package com.example.productcrud.controller;

import com.example.productcrud.model.Product;
import com.example.productcrud.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class ProductController {

    @Autowired
    private ProductService productService;

    /**
     * ⭐ METHOD UTAMA: Menampilkan daftar produk dengan PAGINATION
     *
     * URL Pattern: /products?page=0&keyword=&categoryId=&active=
     */
    @GetMapping("/products")
    public String listProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Boolean active,
            Model model) {

        // 1. Ambil data produk dari service (sudah include pagination)
        Page<Product> productPage = productService.getProducts(page, keyword, categoryId, active);

        // 2. Tambahkan data produk ke model
        model.addAttribute("products", productPage.getContent());

        // 3. Tambahkan metadata PAGINATION ke model
        model.addAttribute("currentPage", productPage.getNumber());           // Halaman saat ini (0-indexed)
        model.addAttribute("totalPages", productPage.getTotalPages());       // Total jumlah halaman
        model.addAttribute("totalItems", productPage.getTotalElements());   // Total jumlah item

        // 4. Tambahkan parameter filter (untuk maintain state di URL pagination)
        model.addAttribute("keyword", keyword != null ? keyword : "");
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("active", active);

        // 5. Hitung "Showing X to Y of Z entries"
        int startItem = page * 10 + 1;  // Item pertama di halaman ini
        int endItem = Math.min((page + 1) * 10, (int) productPage.getTotalElements());  // Item terakhir

        model.addAttribute("startItem", startItem);
        model.addAttribute("endItem", endItem);

        // 6. Return nama template Thymeleaf
        return "product/list";
    }

    /**
     * Method untuk menampilkan form create produk
     */
    @GetMapping("/products/create")
    public String showCreateForm(Model model) {
        model.addAttribute("product", new Product());
        return "product/form";
    }

    /**
     * Method untuk menyimpan produk baru
     */
    @PostMapping("/products/save")
    public String saveProduct(@ModelAttribute Product product) {
        // Logic save product...
        return "redirect:/products";
    }

    /**
     * Method untuk menampilkan form edit produk
     */
    @GetMapping("/products/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        // Logic load product by id...
        return "product/form";
    }

    /**
     * Method untuk update produk
     */
    @PostMapping("/products/update/{id}")
    public String updateProduct(@PathVariable Long id, @ModelAttribute Product product) {
        // Logic update product...
        return "redirect:/products";
    }

    /**
     * Method untuk delete produk
     */
    @GetMapping("/products/delete/{id}")
    public String deleteProduct(@PathVariable Long id) {
        // Logic delete product...
        return "redirect:/products";
    }
}