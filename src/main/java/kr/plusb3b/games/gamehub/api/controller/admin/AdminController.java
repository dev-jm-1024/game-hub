package kr.plusb3b.games.gamehub.api.controller.admin;

import kr.plusb3b.games.gamehub.domain.game.dto.GamesInfoDto;
import kr.plusb3b.games.gamehub.domain.game.service.GameMetadataService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.Model;

import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final GameMetadataService gameMetadataService;

    public AdminController(GameMetadataService gameMetadataService) {
        this.gameMetadataService = gameMetadataService;
    }

    @GetMapping
    public String showAdminPage(){
        return "admin/index";
    }

    //게임 승인 상태 확인하기
    @GetMapping("/game-status")
    public String viewGameStatusPage(Model model){

        // 🔄 개선: NPE 방지
        Optional<GamesInfoDto> notApprovedGame = gameMetadataService.notApprovedGames();
        model.addAttribute("notApprovedGame", notApprovedGame.orElse(null));

        Optional<GamesInfoDto> approvedGame = gameMetadataService.approvedGames();
        model.addAttribute("approvedGame", approvedGame.orElse(null));

        return "admin/game-status/index";
    }
}
