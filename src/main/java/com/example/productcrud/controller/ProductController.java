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

    // ==================== LIST PRODUCTS DENGAN SEARCH & FILTER ====================

    @GetMapping("/products")
    public String listProducts(
            @RequestParam(required = false) String keyword,
            @RequestParam(value = "category", required = false) String categoryId,
            Model model,
            Authentication authentication) {

        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User tidak ditemukan"));

        List<Product> products;

        // Konversi categoryId dari String ke Long (aman dari error)
        Long categoryIdLong = null;
        if (categoryId != null && !categoryId.trim().isEmpty() && !categoryId.equals("null")) {
            try {
                categoryIdLong = Long.parseLong(categoryId.trim());
            } catch (NumberFormatException e) {
                // Jika bukan angka valid, set ke null
                categoryIdLong = null;
            }
        }

        // Jika ada parameter search atau filter, gunakan query khusus
        boolean hasKeyword = (keyword != null && !keyword.trim().isEmpty());
        boolean hasCategory = (categoryIdLong != null);

        if (hasKeyword || hasCategory) {
            products = productService.findByKeywordAndCategory(
                    hasKeyword ? keyword.trim() : null,
                    categoryIdLong,
                    user.getId()
            );
        } else {
            // Tampilkan semua produk user
            products = productService.findAllByUserId(user.getId());
        }

        // Ambil semua kategori untuk dropdown filter
        List<Category> categories = categoryService.findAllByUserId(user.getId());

        // Cari nama kategori yang dipilih (untuk ditampilkan di info)
        String selectedCategoryName = null;
        if (categoryIdLong != null) {
            for (Category cat : categories) {
                if (cat.getId().equals(categoryIdLong)) {
                    selectedCategoryName = cat.getName();
                    break;
                }
            }
        }

        // Kirim data ke view
        model.addAttribute("products", products);
        model.addAttribute("categories", categories);
        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedCategory", categoryIdLong);
        model.addAttribute("selectedCategoryName", selectedCategoryName);

        return "product/list";
    }

    @GetMapping("/products/{id}")
    public String detailProduct(@PathVariable Long id, Model model) {
        return productService.findById(id)
                .map(product -> {
                    model.addAttribute("product", product);
                    return "product/detail";
                })
                .orElse("redirect:/products");
    }

    @GetMapping("/products/new")
    public String showCreateForm(Model model, Authentication authentication) {
        Product product = new Product();
        product.setCreatedAt(LocalDate.now());
        model.addAttribute("product", product);

        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User tidak ditemukan"));

        List<Category> categories = categoryService.findAllByUserId(user.getId());
        model.addAttribute("categories", categories);

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
    public String saveProduct(@ModelAttribute Product product, Authentication authentication, RedirectAttributes redirectAttributes) {
        String username = authentication.getName();
                .orElseThrow(() -> new RuntimeException("User tidak ditemukan"));
        product.setUser(user);

        productService.save(product);
        redirectAttributes.addFlashAttribute("successMessage", "Produk berhasil disimpan!");
        return "redirect:/products";
    }

    @GetMapping("/products/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model, Authentication authentication) {
        return productService.findById(id)
                .map(product -> {
                    model.addAttribute("product", product);

                    String username = authentication.getName();
                    User user = userRepository.findByUsername(username)
                            .orElseThrow(() -> new RuntimeException("User tidak ditemukan"));

                    List<Category> categories = categoryService.findAllByUserId(user.getId());
                    model.addAttribute("categories", categories);

                    return "product/form";
                })
                .orElse("redirect:/products");
    public String saveProduct(@ModelAttribute Product product) {
        // Logic save product...
    }

    /**
     * Method untuk menampilkan form edit produk
     */
    @GetMapping("/products/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        // Logic load product by id...
        return "product/form";
    }

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
        // Logic delete product...
        return "redirect:/products";
    }
}