package com.mt.KickBet.controller;

import com.mt.KickBet.service.MatchService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/matches")
public class MatchController {

    private final MatchService matchService;

    public MatchController(MatchService matchService) {
        this.matchService = matchService;
    }

    @GetMapping
    public String showMatches(Model model) {
        model.addAttribute("matches", matchService.getAllMatches());
        return "matches";
    }
}






