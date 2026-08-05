package at.atypicalbacklog.management.config.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SpaForwardController {

    // Explicit SPA routes handled by React; forward them to the index.html at "/"
    @GetMapping({
        "/games",
        "/games/{uid}",
        "/game-instances/{uid}",
        "/game-instances/{uid}/edit",
        "/game-instances/{instanceUid}/images/{imageUid}/edit",
        "/library",
        "/backlog",
        "/play-activity/active",
        "/playthroughs/new",
        "/playthroughs/{uid}",
        "/reviews/year-tierlist",
        "/reviews",
        "/reviews/yearly",
        "/reviews/yearly/edit",
        "/reviews/edit",
        "/statistics",
        "/backlog-completion-overview",
        "/backlog-submissions",
        "/admin/import/staging",
        "/admin/import/staged-games/{uid}",
        "/admin/import/staged-playthrough-groups/{uid}",
        "/imprint",
        "/faq-glossary"
    })
    public String forwardSpaRoutes() {
        return "forward:/";
    }
}