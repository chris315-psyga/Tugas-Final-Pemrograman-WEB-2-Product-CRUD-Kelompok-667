package com.example.productcrud.controller;

import com.example.productcrud.dto.ChangePasswordRequest;
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

    // ==================== CHANGE PASSWORD ====================

    @GetMapping("/profile/change-password")
    public String showChangePasswordForm(Model model) {
        model.addAttribute("changePasswordRequest", new ChangePasswordRequest());
        return "user/change-password";
    }

    @PostMapping("/profile/change-password")
    public String processChangePassword(@ModelAttribute ChangePasswordRequest request,
                                        Authentication authentication,
                                        RedirectAttributes redirectAttributes) {

        String username = authentication.getName();
        User existingUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User tidak ditemukan"));

        // Validasi: field tidak boleh kosong
        if (request.getOldPassword() == null || request.getOldPassword().trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Password lama harus diisi");
            return "redirect:/profile/change-password";
        }

        if (request.getNewPassword() == null || request.getNewPassword().trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Password baru harus diisi");
            return "redirect:/profile/change-password";
        }

        if (request.getConfirmPassword() == null || request.getConfirmPassword().trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Konfirmasi password baru harus diisi");
            return "redirect:/profile/change-password";
        }

        // Validasi: password lama sesuai
        if (!passwordEncoder.matches(request.getOldPassword(), existingUser.getPassword())) {
            redirectAttributes.addFlashAttribute("error", "Password lama salah");
            return "redirect:/profile/change-password";
        }

        // Validasi: password baru sama dengan konfirmasi
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            redirectAttributes.addFlashAttribute("error", "Password baru dan konfirmasi password tidak sama");
            return "redirect:/profile/change-password";
        }

        // Validasi: password baru tidak boleh sama dengan password lama
        if (passwordEncoder.matches(request.getNewPassword(), existingUser.getPassword())) {
            redirectAttributes.addFlashAttribute("error", "Password baru tidak boleh sama dengan password lama");
            return "redirect:/profile/change-password";
        }

        // Update password dengan encoding BCrypt
        existingUser.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userService.save(existingUser);

        redirectAttributes.addFlashAttribute("successMessage", "Password berhasil diubah!");
        return "redirect:/profile";
    }
}