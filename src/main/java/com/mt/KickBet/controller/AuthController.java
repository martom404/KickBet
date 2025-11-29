package com.mt.KickBet.controller;

import com.mt.KickBet.exception.DuplicateUserException;
import com.mt.KickBet.model.dao.UserRepository;
import com.mt.KickBet.model.dto.auth.RegisterForm;
import com.mt.KickBet.model.entity.User;
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

import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        if (!model.containsAttribute("registerForm")) {
            model.addAttribute("registerForm", new RegisterForm("", "", "", ""));
        }
        return "auth/register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("registerForm") RegisterForm form,
                               BindingResult bindingResult,
                               Model model) {

        if (!form.password().equals(form.confirmPassword())) {
            bindingResult.rejectValue("confirmPassword", "password.mismatch", "Hasła muszą być identyczne.");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("registerForm", form);
            return "auth/register";
        }

        try {
            userService.registerUser(form);
        } catch (DuplicateUserException ex) {
            bindingResult.reject("registration.error", ex.getMessage());
            model.addAttribute("registerForm", form);
            return "auth/register";
        }

        model.addAttribute("registerForm", new RegisterForm("", "", "", ""));
        model.addAttribute("successMessage", "Konto zostało utworzone. Zaloguj się, aby kontynuować.");
        return "auth/register";
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "auth/login";
    }

    @PostMapping("/login")
    public String processLogin(@RequestParam String username,
                               @RequestParam String password,
                               HttpServletRequest request,
                               Model model) {

        Optional<User> userOptional = userRepository.findByUsername(username);

        if (userOptional.isPresent() && userOptional.get().isLocked()) {
            model.addAttribute("error", "Twoje konto zostało zablokowane.");
            return "auth/login";
        }

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
