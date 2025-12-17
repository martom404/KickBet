package com.mt.KickBet.controller.admin;

import com.mt.KickBet.exception.DuplicateUserException;
import com.mt.KickBet.model.dto.user.UpdateUserRequest;
import com.mt.KickBet.model.entity.User;
import com.mt.KickBet.service.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/users")
public class AdminUserController {

    private final UserService userService;

    public AdminUserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String showUsers(@RequestParam(defaultValue = "0") int page,
                            @RequestParam(defaultValue = "20") int size,
                            @RequestParam(defaultValue = "createdAt") String sortBy,
                            @RequestParam(defaultValue = "desc") String sortDir,
                            Model model) {

        if(page < 0) page = 0;
        if(size < 1 || size > 100) size = 20;

        List<String> sortFields = List.of("createdAt", "username", "points");
        if(!sortFields.contains(sortBy)) sortBy = "createdAt";

        Page<User> users = userService.getAllUsersForAdmin(page, size, sortBy, sortDir);
        model.addAttribute("users", users);
        model.addAttribute("currentSize", size);
        model.addAttribute("currentSortBy", sortBy);
        model.addAttribute("currentSortDir", sortDir);

        return "admin/user_list";
    }

    @GetMapping("/{id}/edit")
    public String editUser(@PathVariable Long id,
                           Model model) {

        Optional<User> userOpt = userService.getUserByID(id);

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            model.addAttribute("form", new UpdateUserRequest(
                    user.getUsername(),
                    user.getEmail(),
                    user.getPoints()
            ));
            model.addAttribute("userId", id);
            return "admin/user_editform";
        } else {
            return "redirect:/admin/users";
        }
    }

    @PostMapping("/{id}/edit")
    public String editUser(@PathVariable Long id,
                           @Valid @ModelAttribute("form") UpdateUserRequest dto,
                           BindingResult bindingResult,
                           Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("form", dto);
            model.addAttribute("userId", id);
            return "admin/user_editform";
        }

        try {
            userService.updateUser(id, dto);
            return "redirect:/admin/users";
        } catch (DuplicateUserException ex) {
            bindingResult.reject("update.error", ex.getMessage());
            model.addAttribute("form", dto);
            model.addAttribute("userId", id);
            return "admin/user_editform";
        }
    }

    @PostMapping("/{id}/block")
    public String blockUser(@PathVariable long id) {
        userService.blockUser(id);
        return "redirect:/admin/users";
    }


}
