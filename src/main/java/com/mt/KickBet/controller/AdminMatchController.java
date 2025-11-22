package com.mt.KickBet.controller;

import com.mt.KickBet.model.dto.match.CreateMatchRequest;
import com.mt.KickBet.model.dto.match.UpdateMatchRequest;
import com.mt.KickBet.model.entity.Match;
import com.mt.KickBet.model.entity.Result;
import com.mt.KickBet.service.MatchService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/admin/matches")
public class AdminMatchController {

    private final MatchService matchService;

    public AdminMatchController(MatchService matchService) {
        this.matchService = matchService;
    }

    @GetMapping
    public String listMatches(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "25") int size,Model model) {
        Page<Match> matchesPage = matchService.getAllMatchesForAdmin(page, size);
        model.addAttribute("matches", matchesPage);
        return "admin/match_list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("form", new CreateMatchRequest("", "", LocalDateTime.now()));
        return "admin/match_createform";
    }

    @PostMapping
    public String createMatch(@Valid @ModelAttribute("form") CreateMatchRequest form, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "admin/match_createform";
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
                    return "admin/match_editform";
                })
                .orElse("redirect:/admin/matches");
    }

    @PostMapping("/{id}/edit")
    public String updateMatch(@PathVariable Long id, @Valid @ModelAttribute("form") UpdateMatchRequest form, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("matchId", id);
            return "admin/match_editform";
        }
        matchService.updateMatch(id, form);
        return "redirect:/admin/matches";
    }

    @PostMapping("/{id}/result")
    public String setResult(@PathVariable Long id, @RequestParam("result") String result) {
        try {
            Result enumResult = Result.valueOf(result);
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

    @PostMapping("/{id}/hideMatch")
    public String hideMatch(@PathVariable Long id) {
        matchService.makeHidden(id);
        return "redirect:/admin/matches";
    }
}


