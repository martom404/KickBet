package com.mt.KickBet.controller.user;

import com.mt.KickBet.model.entity.Bet;
import com.mt.KickBet.model.entity.User;
import com.mt.KickBet.security.SecurityTools;
import com.mt.KickBet.service.BetService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/my-bets")
public class MyBetsController {

    private final BetService betService;

    public MyBetsController(BetService betService) {
        this.betService = betService;
    }

    @GetMapping
    public String showMyBets(@RequestParam(defaultValue = "0") int page,
                             @RequestParam(defaultValue = "20") int size,
                             @RequestParam(defaultValue = "createdAt") String sortBy,
                             @RequestParam(defaultValue = "asc") String sortDir,
                             Model model) {
        if(page < 0) page = 0;
        if(size < 1) size = 20;

        List<String> sortField = List.of("createdAt", "pointsAwarded");
        if(!sortField.contains(sortBy)) sortBy = "createdAt";

        User currentUser = SecurityTools.getUser();
        if (currentUser != null) {
            Page<Bet> bets = betService.getUserBets(currentUser.getId(), page, size, sortBy, sortDir);
            model.addAttribute("bets", bets);
            model.addAttribute("currentSize", size);
            model.addAttribute("currentSortBy", sortBy);
            model.addAttribute("currentSortDir", sortDir);
        }
        return "user/my_bets";
    }
}

