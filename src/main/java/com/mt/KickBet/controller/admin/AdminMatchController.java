package com.mt.KickBet.controller.admin;

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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/admin/matches")
public class AdminMatchController {

    private final MatchService matchService;

    public AdminMatchController(MatchService matchService) {
        this.matchService = matchService;
    }

    @GetMapping
    public String listMatches(@RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "25") int size,
                              @RequestParam(defaultValue = "startTime") String sortBy,
                              @RequestParam(defaultValue = "asc") String sortDir,
                              Model model) {

        if(page < 0) page = 0;
        if(size < 1 || size > 100) size = 25;

        List<String> sortFields = List.of("startTime");
        if(!sortFields.contains(sortBy)) sortBy = "startTime";

        Page<Match> matchesPage = matchService.getAllMatchesForAdmin(page, size, sortBy, sortDir);
        model.addAttribute("matches", matchesPage);

        model.addAttribute("currentSize", size);
        model.addAttribute("currentSortBy", sortBy);
        model.addAttribute("currentSortDir", sortDir);

        return "admin/match_list";
    }

    @GetMapping("/new")
    public String createMatch(Model model) {
        model.addAttribute("form", new CreateMatchRequest("", "", LocalDateTime.now(), 1.0, 1.0, 1.0));
        return "admin/match_createform";
    }

    @PostMapping
    public String createMatch(@Valid @ModelAttribute("form") CreateMatchRequest form,
                              BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return "admin/match_createform";
        }
        matchService.createMatch(form);
        return "redirect:/admin/matches";
    }

    @GetMapping("/{id}/edit")
    public String updateMatch(@PathVariable Long id,
                              Model model) {

        return matchService.getMatchById(id)
                .map(match -> {
                    model.addAttribute("form", new UpdateMatchRequest(
                            match.getHomeTeam(),
                            match.getAwayTeam(),
                            match.getStartTime(),
                            match.getOddsHome(),
                            match.getOddsDraw(),
                            match.getOddsAway()
                    ));
                    model.addAttribute("matchId", id);
                    return "admin/match_editform";
                }).orElse("redirect:/admin/matches");
    }

    @PostMapping("/{id}/edit")
    public String updateMatch(@PathVariable Long id,
                              @Valid @ModelAttribute("form") UpdateMatchRequest form,
                              BindingResult bindingResult,
                              Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("matchId", id);
            return "admin/match_editform";
        }
        matchService.updateMatch(id, form);
        return "redirect:/admin/matches";
    }

    @PostMapping("/{id}/result")
    public String setResult(@PathVariable Long id,
                            @RequestParam("result") String result,
                            RedirectAttributes redirectAttributes) {

        try {
            Result enumResult = Result.valueOf(result);
            matchService.setFinalResult(id, enumResult);
            redirectAttributes.addFlashAttribute("successMessage", "Wynik meczu został ustawiony pomyślnie!");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Nie możesz ustawić wyniku dla meczu, który jeszcze nie został rozpoczęty!.");
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


