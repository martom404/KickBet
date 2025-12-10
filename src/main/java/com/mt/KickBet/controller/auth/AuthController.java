package com.mt.KickBet.controller.auth;

import com.mt.KickBet.exception.DuplicateUserException;
import com.mt.KickBet.exception.UserIsLockedException;
import com.mt.KickBet.model.dto.auth.RegisterForm;
import com.mt.KickBet.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

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
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    public AuthController(UserService userService, AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("registerForm", new RegisterForm("", "", "", ""));
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

        try {
            userService.validateLockedStatus(username);

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

        } catch (UserIsLockedException ex) {
            model.addAttribute("error", ex.getMessage());
            return "auth/login";
        } catch (AuthenticationException ex) {
            model.addAttribute("error", "Nieprawidłowa nazwa użytkownika lub hasło.");
            return "auth/login";
        }
    }


}
