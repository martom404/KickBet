package com.mt.KickBet.controller;

import com.mt.KickBet.exception.DuplicateUserException;
import com.mt.KickBet.model.dto.auth.RegisterForm;
import com.mt.KickBet.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;

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

    @PostMapping("/login")
    public String processLogin(@RequestParam String username, @RequestParam String password, HttpServletRequest request, Model model) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            HttpSession session = request.getSession(true);
            session.setAttribute(
                    HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                    SecurityContextHolder.getContext()
            );
            return "redirect:/";

        } catch (AuthenticationException ex) {
            model.addAttribute("error", "Nieprawidłowa nazwa użytkownika lub hasło.");
            return "auth/login";
        }
    }


}
