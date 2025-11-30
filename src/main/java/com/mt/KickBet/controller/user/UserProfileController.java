package com.mt.KickBet.controller.user;

import com.mt.KickBet.model.dto.auth.ChangePasswordForm;
import com.mt.KickBet.model.entity.User;
import com.mt.KickBet.security.SecurityTools;
import com.mt.KickBet.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
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
                    .ifPresent(user -> {
                        model.addAttribute("user", user);
                        int rankPosition = userService.calculateRankPosition(user.getPoints());
                        model.addAttribute("rankPosition", rankPosition);
                    });
        }
        return "user/my_profile";
    }

    @GetMapping("/change-password")
    public String changePassword(Model model) {
        if (!model.containsAttribute("changePasswordForm")) {
            model.addAttribute("changePasswordForm", new ChangePasswordForm("", "", ""));
        }
        return "user/change_password";
    }

    @PostMapping("/change-password")
    public String changePassword(@Valid @ModelAttribute("changePasswordForm") ChangePasswordForm form,
                                 BindingResult bindingResult,
                                 Model model) {

        if (!form.newPassword().equals(form.confirmPassword())) {
            bindingResult.rejectValue("confirmPassword", "password.mismatch", "Hasła nie są identyczne!");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("changePasswordForm", form);
            return "user/change_password";
        }

        User currentUser = SecurityTools.getUser();
        if (currentUser == null) {
            return "redirect:/login";
        }

        try {
            userService.changePassword(
                    currentUser.getId(),
                    form.currentPassword(),
                    form.newPassword()
            );
            model.addAttribute("changePasswordForm", new ChangePasswordForm("", "", ""));
            model.addAttribute("successMessage", "Hasło zostało zmienione!");
            return "user/change_password";
        } catch (IllegalArgumentException e) {
            bindingResult.reject("passwordError", e.getMessage());
            model.addAttribute("changePasswordForm", form);
            return "user/change_password";
        }
    }

}
