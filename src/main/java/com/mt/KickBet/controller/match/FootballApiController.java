package com.mt.KickBet.controller.match;

import com.mt.KickBet.service.ApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/matches")
public class FootballApiController {

    private final ApiService apiService;

    @PostMapping("/sync")
    public String syncMatches() {
        apiService.syncMatches();
        return "redirect:/admin/matches";
    }
}
