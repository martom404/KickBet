package com.mt.KickBet.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HelloControler {

    @GetMapping("/")
    public String hello(Model model) {
        model.addAttribute("message", "Witaj w KickBet!");
        return "index";
    }
}
