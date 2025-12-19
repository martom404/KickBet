package com.mt.KickBet.controller.admin;

import com.mt.KickBet.service.ApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/matches")
public class ApiController {

    private final ApiService apiService;

    public ApiController(ApiService apiService) {
        this.apiService = apiService;
    }

    @PostMapping("/sync")
    public String syncMatches(@RequestParam int maxMatches, RedirectAttributes redirectAttributes) {
        int savedMatches = apiService.syncMatches(maxMatches);
        redirectAttributes.addFlashAttribute("message", "Pobrano " + savedMatches + " nowych meczów z API.");
        return "redirect:/admin/matches";
    }
}
