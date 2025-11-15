package com.mt.KickBet.controller;


import com.mt.KickBet.model.dao.User;
import com.mt.KickBet.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Comparator;
import java.util.List;

@Controller
@RequestMapping("/ranking")
public class RankingController {

    private final UserService userService;

    public RankingController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String showRanking(Model model) {
        List<User> users = userService.getAllUsers();
        users.sort(Comparator.comparingInt(User::getPoints).reversed());
        model.addAttribute("users", users);
        return "ranking";
    }
}

