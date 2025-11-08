package com.mt.KickBet.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class AuthController {

    @GetMapping("/register")
    public String showRegistrationForm() {
        return "auth/register";
    }

    @GetMapping("/login")
    public String showLoginForm(){
        return "auth/login";
    }
}
