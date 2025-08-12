package kr.plusb3b.games.gamehub.api.controller.admin;

import kr.plusb3b.games.gamehub.domain.game.dto.GamesInfoDto;
import kr.plusb3b.games.gamehub.domain.game.entity.Games;
import kr.plusb3b.games.gamehub.domain.game.repository.GamesRepository;
import kr.plusb3b.games.gamehub.domain.game.service.GameMetadataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.Model;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
@Slf4j
public class AdminController {

    private final GameMetadataService gameMetadataService;
    private final GamesRepository gamesRepository;

    public AdminController(GameMetadataService gameMetadataService, GamesRepository gamesRepository) {
        this.gameMetadataService = gameMetadataService;
        this.gamesRepository = gamesRepository;
    }

    @GetMapping
    public String showAdminPage(){
        return "admin/index";
    }

    @GetMapping("/game-status")
    public String viewGameStatusPage(Model model){

        // log.info("AdminController /game-status 호출됨");

        try {
            // 전체 게임 조회
            List<Games> allGames = gamesRepository.findAll();
            // log.info("전체 게임 수: {}", allGames.size());

            if (allGames.isEmpty()) {
                // log.warn("DB에 게임이 없습니다");
                model.addAttribute("notApprovedGame", null);
                model.addAttribute("approvedGame", null);
                model.addAttribute("underReviewGame", null);
                model.addAttribute("rejectedGame", null);
                model.addAttribute("suspendedGame", null);
                model.addAttribute("deactivatedGame", null);
                return "admin/game-status/index";
            }

            // 상태별 게임 분류
            List<Games> pendingReviewGames = allGames.stream()
                    .filter(g -> g.getStatus() == Games.GameStatus.PENDING_REVIEW)
                    .collect(Collectors.toList());

            List<Games> underReviewGames = allGames.stream()
                    .filter(g -> g.getStatus() == Games.GameStatus.UNDER_REVIEW)
                    .collect(Collectors.toList());

            List<Games> activeGames = allGames.stream()
                    .filter(g -> g.getStatus() == Games.GameStatus.ACTIVE)
                    .collect(Collectors.toList());

            List<Games> rejectedGames = allGames.stream()
                    .filter(g -> g.getStatus() == Games.GameStatus.REJECTED)
                    .collect(Collectors.toList());

            List<Games> suspendedGames = allGames.stream()
                    .filter(g -> g.getStatus() == Games.GameStatus.SUSPENDED)
                    .collect(Collectors.toList());

            List<Games> deactivatedGames = allGames.stream()
                    .filter(g -> g.getStatus() == Games.GameStatus.DEACTIVATED)
                    .collect(Collectors.toList());

            // DTO 생성
            GamesInfoDto notApprovedGame = pendingReviewGames.isEmpty() ? null :
                    new GamesInfoDto(pendingReviewGames, List.of());

            GamesInfoDto underReviewGame = underReviewGames.isEmpty() ? null :
                    new GamesInfoDto(underReviewGames, List.of());

            GamesInfoDto approvedGame = activeGames.isEmpty() ? null :
                    new GamesInfoDto(activeGames, List.of());

            GamesInfoDto rejectedGame = rejectedGames.isEmpty() ? null :
                    new GamesInfoDto(rejectedGames, List.of());

            GamesInfoDto suspendedGame = suspendedGames.isEmpty() ? null :
                    new GamesInfoDto(suspendedGames, List.of());

            GamesInfoDto deactivatedGame = deactivatedGames.isEmpty() ? null :
                    new GamesInfoDto(deactivatedGames, List.of());

            // Model에 데이터 추가
            model.addAttribute("notApprovedGame", notApprovedGame);
            model.addAttribute("approvedGame", approvedGame);
            model.addAttribute("underReviewGame", underReviewGame);
            model.addAttribute("rejectedGame", rejectedGame);
            model.addAttribute("suspendedGame", suspendedGame);
            model.addAttribute("deactivatedGame", deactivatedGame);

            // log.info("Model 데이터 추가 완료");

        } catch (Exception e) {
            // log.error("게임 상태 페이지 로딩 실패", e);

            // 예외 발생 시 모든 속성을 null로 설정
            model.addAttribute("notApprovedGame", null);
            model.addAttribute("approvedGame", null);
            model.addAttribute("underReviewGame", null);
            model.addAttribute("rejectedGame", null);
            model.addAttribute("suspendedGame", null);
            model.addAttribute("deactivatedGame", null);
        }

        return "admin/game-status/index";
    }
}