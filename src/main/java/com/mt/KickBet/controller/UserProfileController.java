package com.mt.KickBet.controller;

import com.mt.KickBet.model.dao.User;
import com.mt.KickBet.service.UserService;
import com.mt.KickBet.security.SecurityTools;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/my-profile")
public class UserProfileController {
    private final UserService userService;

    public UserProfileController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String myProfile(Model model) {
        User currentUser = SecurityTools.getUser();
        if (currentUser != null) {
            userService.getUserByID(currentUser.getId())
                    .ifPresent(user -> model.addAttribute("user", user));
        }
        return "user/my_profile";
    }

}
