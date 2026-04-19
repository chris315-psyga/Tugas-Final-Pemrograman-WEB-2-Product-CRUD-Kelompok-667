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

    @GetMapping("/products")
    public String listProducts(Model model) {
        model.addAttribute("products", productService.findAll());
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
    public String showCreateForm(Model model,Authentication authentication) {
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
    public String saveProduct(@ModelAttribute Product product, RedirectAttributes redirectAttributes) {
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
