package com.example.productcrud.controller;

import com.example.productcrud.model.Category;
import com.example.productcrud.model.User;
import com.example.productcrud.repository.UserRepository;
import com.example.productcrud.service.CategoryService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class CategoryController {
    private final CategoryService categoryService;
    private final UserRepository userRepository;

    public CategoryController(CategoryService categoryService, UserRepository userRepository){
        this.categoryService = categoryService;
        this.userRepository = userRepository;
    }

    @GetMapping("/categories")
    public String listCategories(Model model, Authentication authentication){
        String username = authentication.getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("user tidak ditemukan"));

        List<Category> categories = categoryService.findAllByUserId(user.getId());

        model.addAttribute("categories", categories);
        return "category/list";
    }

    @GetMapping("/categories/new")
    public String showCreateForm(Model model){
        Category category = new Category();
        model.addAttribute("category", category);
        return "category/form";
    }

    @PostMapping("/categories/save")
    public String saveCategory(@ModelAttribute Category category, Authentication authentication, RedirectAttributes redirectAttributes){
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User tidak ditemukan"));

        category.setUser(user);

        categoryService.save(category);
        redirectAttributes.addFlashAttribute("successMessage", "Kategori berhasil disimpan");
        return "redirect:/categories";
    }

    @GetMapping("/categories/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model){
        return categoryService.findById(id)
                .map(category -> {
                    model.addAttribute("category", category);
                    return "category/form";
                })
                .orElse("redirect:/categories");
    }
}
