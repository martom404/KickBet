package com.mt.KickBet.controller;

import com.mt.KickBet.model.entity.Match;
import com.mt.KickBet.service.MatchService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
@RequestMapping("/matches")
public class MatchController {

    private final MatchService matchService;

    public MatchController(MatchService matchService) {
        this.matchService = matchService;
    }

    @GetMapping
    public String showMatches(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "25") int size, Model model) {
        Page<Match> matches = matchService.getAllMatches(page, size);
        model.addAttribute("matches", matches);
        return "matches";
    }
}






