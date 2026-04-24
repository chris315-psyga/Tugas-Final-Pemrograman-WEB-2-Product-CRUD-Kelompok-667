package com.example.productcrud.controller;

import com.example.productcrud.model.Category;
import com.example.productcrud.model.Product;
import com.example.productcrud.model.User;
import com.example.productcrud.repository.UserRepository;
import com.example.productcrud.service.CategoryService;
import com.example.productcrud.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class ProductController {

    private final ProductService productService;
    private final UserRepository userRepository;
    private final CategoryService categoryService;

    public ProductController(ProductService productService, UserRepository userRepository, CategoryService categoryService) {
        this.productService = productService;
        this.userRepository = userRepository;
        this.categoryService = categoryService;
    }

    @GetMapping("/products")
    public String listProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            Model model,
            Authentication authentication) {

        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User tidak ditemukan"));

        Page<Product> productPage = productService.getProducts(page, keyword, categoryId, user.getId());
        List<Category> categories = categoryService.findAllByUserId(user.getId());

        // Hitung info "Showing X to Y of Z entries"
        int totalItems = (int) productPage.getTotalElements();
        int startItem = totalItems == 0 ? 0 : (page * 10) + 1;
        int endItem = (int) Math.min((page + 1) * 10L, productPage.getTotalElements());

        model.addAttribute("products", productPage.getContent());
        model.addAttribute("categories", categories);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productPage.getTotalPages());
        model.addAttribute("totalItems", totalItems);
        model.addAttribute("startItem", startItem);
        model.addAttribute("endItem", endItem);
        model.addAttribute("keyword", keyword != null ? keyword : "");
        model.addAttribute("categoryId", categoryId);

        return "product/list";
    }

    @GetMapping("/products/new")
    public String showCreateForm(Model model, Authentication authentication) {
        model.addAttribute("product", new Product());
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User tidak ditemukan"));
        model.addAttribute("categories", categoryService.findAllByUserId(user.getId()));
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
                    model.addAttribute("categories", categoryService.findAllByUserId(user.getId()));
                    return "product/form";
                })
                .orElse("redirect:/products");
    }

    @PostMapping("/products/{id}/delete")
    public String deleteProduct(@PathVariable Long id) {
        productService.deleteById(id);
        return "redirect:/products";
    }
}