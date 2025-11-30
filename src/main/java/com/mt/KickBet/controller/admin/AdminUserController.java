package com.mt.KickBet.controller.admin;


import com.mt.KickBet.model.dto.user.UpdateUserRequest;
import jakarta.validation.Valid;
import org.springframework.ui.Model;
import com.mt.KickBet.model.entity.User;
import com.mt.KickBet.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/users")
public class AdminUserController {
    private final UserService userService;

    public AdminUserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String listUsers(@RequestParam(defaultValue = "0") int page,
                            @RequestParam(defaultValue = "20") int size,
                            Model model) {
        Page<User> users = userService.getAllUsers(page,size);
        model.addAttribute("users", users);
        return "admin/user_list";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id,
                           Model model){
        return userService.getUserByID(id).map(user -> {
            model.addAttribute("form", new UpdateUserRequest(
                    user.getUsername(),
                    user.getEmail(),
                    user.getPoints()
            ));
            model.addAttribute("userId", id);
            return "admin/user_editform";
        }).orElse("redirect:/admin/users");
    }

    @PostMapping("/{id}/edit")
    public String updateUser(@PathVariable Long id,
                             @Valid @ModelAttribute("form") UpdateUserRequest dto,
                             BindingResult bindingResult,
                             Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("form", dto);
            model.addAttribute("userId", id);
            return "admin/user_editform";
        }

        userService.updateUser(id, dto);
        return "redirect:/admin/users";
    }

    @PostMapping("/{id}/block")
    public String blockUser(@PathVariable long id) {
        userService.blockUser(id);
        return "redirect:/admin/users";
    }


}
