package com.example.productcrud.controller;

import com.example.productcrud.model.Product;
import com.example.productcrud.model.User;
import com.example.productcrud.repository.UserRepository;
import com.example.productcrud.service.ProductService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class DashboardController {

    private final ProductService productService;
    private final UserRepository userRepository;

    public DashboardController(ProductService productService, UserRepository userRepository) {
        this.productService = productService;
        this.userRepository = userRepository;
    }

    @GetMapping("/")
    public String dashboard(Model model, Authentication authentication) {
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User tidak ditemukan"));

        Long userId = user.getId();


        Long totalProducts = productService.countByUserId(userId);


        Long totalInventoryValue = productService.sumInventoryValueByUserId(userId);


        Long activeProducts = productService.countActiveByUserId(userId);
        Long inactiveProducts = productService.countInactiveByUserId(userId);


        List<Object[]> productsPerCategory = productService.countProductsPerCategoryByUserId(userId);


        List<Product> lowStockProducts = productService.findLowStockByUserId(userId);

        model.addAttribute("totalProducts", totalProducts);
        model.addAttribute("totalInventoryValue", totalInventoryValue);
        model.addAttribute("activeProducts", activeProducts);
        model.addAttribute("inactiveProducts", inactiveProducts);
        model.addAttribute("productsPerCategory", productsPerCategory);
        model.addAttribute("lowStockProducts", lowStockProducts);
        model.addAttribute("hasProducts", totalProducts > 0);

        return "dashboard";
    }
}