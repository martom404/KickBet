package com.mt.KickBet.controller;

import com.mt.KickBet.model.dto.bet.CreateBetRequest;
import com.mt.KickBet.model.entity.Bet;
import com.mt.KickBet.model.entity.Match;
import com.mt.KickBet.model.entity.Result;
import com.mt.KickBet.model.entity.User;
import com.mt.KickBet.security.SecurityTools;
import com.mt.KickBet.service.BetService;
import com.mt.KickBet.service.MatchService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/matches")
public class MatchController {

    private final MatchService matchService;
    private final BetService betService;

    public MatchController(MatchService matchService, BetService betService) {
        this.matchService = matchService;
        this.betService = betService;
    }

    @GetMapping
    public String showMatches(@RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "25") int size,
                              Model model) {
        Page<Match> matches = matchService.getAllMatches(page, size);
        model.addAttribute("matches", matches);

        User currentUser = SecurityTools.getUser();
        if (currentUser != null) {
            List<Bet> userBets = betService.getUserBets(currentUser.getId());
            Map<Long, Bet> betsByMatchId = userBets.stream()
                    .collect(Collectors.toMap(bet -> bet.getMatch().getId(), bet -> bet));
            
            model.addAttribute("userBets", userBets);
            model.addAttribute("betsByMatchId", betsByMatchId);
        }

        return "matches";
    }

    @PostMapping("/{matchId}/bet")
    public String placeBet(@PathVariable Long matchId,
                           @RequestParam Result pick,
                           RedirectAttributes redirectAttributes) {
        User currentUser = SecurityTools.getUser();
        if (currentUser == null) {
            redirectAttributes.addFlashAttribute("error", "Musisz być zalogowany do obstawiania!");
            return "redirect:/matches";
        }

        try {
            CreateBetRequest request = new CreateBetRequest(pick);
            betService.createBet(currentUser.getId(), matchId, request);
            redirectAttributes.addFlashAttribute("success", "Zakład został złożony pomyślnie!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/matches";
    }
}






