package com.mt.KickBet.controller;

import com.mt.KickBet.model.dao.Result;
import com.mt.KickBet.model.dto.match.CreateMatchRequest;
import com.mt.KickBet.model.dto.match.UpdateMatchRequest;

import com.mt.KickBet.service.MatchService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.BindingResult;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/admin/matches")
public class AdminMatchMvcController {

    private final MatchService matchService;

    public AdminMatchMvcController(MatchService matchService) {
        this.matchService = matchService;
    }

    @GetMapping
    public String listMatches(Model model) {
        model.addAttribute("matches", matchService.getAllMatches());
        return "admin/match_list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("form", new CreateMatchRequest("", "", LocalDateTime.now().plusHours(1)));
        return "admin/match_form";
    }

    @PostMapping
    public String createMatch(@Valid @ModelAttribute("form") CreateMatchRequest form, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "admin/match_form";
        }
        matchService.createMatch(form);
        return "redirect:/admin/matches";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        return matchService.getMatchById(id)
                .map(match -> {
                    UpdateMatchRequest form = new UpdateMatchRequest(
                            match.getHomeTeam(),
                            match.getAwayTeam(),
                            match.getStartTime()
                    );
                    model.addAttribute("form", form);
                    model.addAttribute("matchId", id);
                    return "admin/match_edit";
                })
                .orElse("redirect:/admin/matches");
    }

    @PostMapping("/{id}/edit")
    public String updateMatch(@PathVariable Long id, @Valid @ModelAttribute("form") UpdateMatchRequest form, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("matchId", id);
            return "admin/match_edit";
        }
        matchService.updateMatch(id, form);
        return "redirect:/admin/matches";
    }

    @PostMapping("/{id}/result")
    public String setResult(@PathVariable Long id, @RequestParam("result") String result) {
        try {
            Result enumResult = Result.valueOf(result.toUpperCase());
            matchService.setFinalResult(id, enumResult);
        } catch (IllegalArgumentException e) {
        }
        return "redirect:/admin/matches";
    }

    @PostMapping("/{id}/delete")
    public String deleteMatch(@PathVariable Long id) {
        matchService.deleteMatch(id);
        return "redirect:/admin/matches";
    }
}


