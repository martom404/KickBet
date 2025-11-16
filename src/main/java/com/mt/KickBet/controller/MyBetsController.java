package com.mt.KickBet.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/my-bets")
public class MyBetsController {

    @GetMapping
    public String showMyBets(Model model) {
        return "user/my_bets";
    }
}

