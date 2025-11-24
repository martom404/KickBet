package com.mt.KickBet.controller;


import com.mt.KickBet.model.entity.User;
import com.mt.KickBet.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/ranking")
public class RankingController {

    private final UserService userService;

    public RankingController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String showRanking(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "25") int size, Model model) {
        Page<User> users = userService.getAllUsers(page, size);
        model.addAttribute("users", users);
        return "ranking";
    }
}