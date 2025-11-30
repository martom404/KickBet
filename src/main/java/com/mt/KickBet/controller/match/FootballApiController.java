package com.mt.KickBet.controller.match;

import com.mt.KickBet.service.FootballDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/matches")
public class FootballApiController {

    private final FootballDataService footballDataService;

    @PostMapping("/sync")
    public String syncMatches() {
        footballDataService.syncMatches();
        return "redirect:/admin/matches";
    }
}
