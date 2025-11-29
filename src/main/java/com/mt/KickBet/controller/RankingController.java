package com.mt.KickBet.controller;


import com.mt.KickBet.model.entity.User;
import com.mt.KickBet.security.SecurityTools;
import com.mt.KickBet.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/ranking")
public class RankingController {

    private final UserService userService;

    public RankingController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String showRanking(@RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "25") int size,
                              Model model) {
        Page<User> users = userService.getAllUsers(page, size);
        
        Map<Long, Integer> userPositions = new HashMap<>();
        for (User user : users.getContent()) {
            int position = userService.calculateRankPosition(user.getPoints());
            userPositions.put(user.getId(), position);
        }
        
        model.addAttribute("users", users);
        model.addAttribute("userPositions", userPositions);
        
        User currentUser = SecurityTools.getUser();
        if (currentUser != null) {
            model.addAttribute("currentUserId", currentUser.getId());
        }
        
        return "ranking";
    }
}

