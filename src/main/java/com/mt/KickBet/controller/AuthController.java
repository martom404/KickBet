package com.mt.KickBet.controller;

import com.mt.KickBet.exception.DuplicateUserException;
import com.mt.KickBet.model.dto.auth.RegisterForm;
import com.mt.KickBet.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        if (!model.containsAttribute("form")) {
            model.addAttribute("form", new RegisterForm("", "", "", ""));
        }
        return "auth/register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("form") RegisterForm form, BindingResult bindingResult, Model model) {
        if (!form.password().equals(form.confirmPassword())) {
            bindingResult.rejectValue("confirmPassword", "password.mismatch", "Hasła muszą być identyczne.");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("form", form);
            return "auth/register";
        }

        try {
            userService.registerUser(form);
        } catch (DuplicateUserException ex) {
            bindingResult.reject("registration.error", ex.getMessage());
            model.addAttribute("form", form);
            return "auth/register";
        }

        model.addAttribute("form", new RegisterForm("", "", "", ""));
        model.addAttribute("successMessage", "Konto zostało utworzone. Zaloguj się, aby kontynuować.");
        return "auth/register";
    }

    @GetMapping("/login")
    public String showLoginForm(){
        return "auth/login";
    }
}
