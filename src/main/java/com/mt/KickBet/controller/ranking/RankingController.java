package com.mt.KickBet.controller.ranking;


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
                              @RequestParam(defaultValue = "12") int size,
                              Model model) {

        if(page < 0) page = 0;
        if(size < 1 || size > 12) size = 12;
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

