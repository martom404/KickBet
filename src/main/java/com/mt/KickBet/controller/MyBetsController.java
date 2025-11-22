package com.mt.KickBet.controller;

import com.mt.KickBet.model.entity.User;
import com.mt.KickBet.security.SecurityTools;
import com.mt.KickBet.service.BetService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/my-bets")
public class MyBetsController {

    private final BetService betService;

    public MyBetsController(BetService betService) {
        this.betService = betService;
    }

    @GetMapping
    public String showMyBets(Model model) {
        User currentUser = SecurityTools.getUser();
        if (currentUser != null) {
            model.addAttribute("bets", betService.getUserBets(currentUser.getId()));
        }
        return "user/my_bets";
    }
}



