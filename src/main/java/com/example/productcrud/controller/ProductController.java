package com.example.productcrud.controller;


import com.example.productcrud.model.Category;
import com.example.productcrud.model.User;
import com.example.productcrud.model.Product;
import com.example.productcrud.repository.CategoryRepository;
import com.example.productcrud.repository.UserRepository;
import com.example.productcrud.service.CategoryService;
import org.springframework.security.core.Authentication;
import com.example.productcrud.service.ProductService;
import java.time.LocalDate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;

@Controller
public class ProductController {

    private final ProductService productService;
    private final CategoryService categoryService;
    private final UserRepository userRepository;

    public ProductController(ProductService productService, CategoryService categoryService, UserRepository userRepository) {
        this.productService = productService;
        this.categoryService = categoryService;
        this.userRepository = userRepository;
    }

    @GetMapping("/")
    public String index() {
        return "redirect:/products";
    }

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

        return "product/form";
    }

    @PostMapping("/products/save")
    public String saveProduct(@ModelAttribute Product product, Authentication authentication, RedirectAttributes redirectAttributes) {
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
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
    }

    @PostMapping("/products/{id}/delete")
    public String deleteProduct(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        productService.deleteById(id);
        redirectAttributes.addFlashAttribute("successMessage", "Produk berhasil dihapus!");
        return "redirect:/products";
    }
}