package com.example.productcrud.controller;

import com.example.productcrud.model.User;
import com.example.productcrud.repository.UserRepository;
import com.example.productcrud.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService, UserRepository userRepository, PasswordEncoder passwordEncoder){
        this.userService = userService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/profile")
    public String viewProfile(Model model, Authentication authentication){
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("user tidak ditemukan"));

        model.addAttribute("currentUser", user);
        return "user/view";
    }

    @GetMapping("/profile/edit")
    public String showEditForm(Model model, Authentication authentication){
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("user tidak ditemukan"));

        model.addAttribute("currentUser", user);
        return "user/edit";
    }

    @PostMapping("/profile/update")
    public String updateProfile(@ModelAttribute User userFromForm, Authentication authentication, RedirectAttributes redirectAttributes){

        String username = authentication.getName();
        User existingUser = userRepository.findByUsername(username)
                        .orElseThrow(() -> new RuntimeException("User tidak ditemukan"));

        String newPassword = userFromForm.getPassword();

        if (newPassword != null && !newPassword.trim().isEmpty()) {
            userFromForm.setPassword(passwordEncoder.encode(newPassword));
        } else {
            userFromForm.setPassword(existingUser.getPassword());
        }

        userFromForm.setId(existingUser.getId());

        userService.save(userFromForm);

        redirectAttributes.addFlashAttribute("successMessage", "Profil berhasil diperbarui");
        return "redirect:/profile";
    }
}
