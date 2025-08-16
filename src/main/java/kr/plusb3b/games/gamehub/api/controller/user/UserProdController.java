package kr.plusb3b.games.gamehub.api.controller.user;

import kr.plusb3b.games.gamehub.common.util.GameStatusFormatter;
import kr.plusb3b.games.gamehub.domain.game.entity.Games;
import kr.plusb3b.games.gamehub.domain.user.dto.UserGameSummaryDto;
import kr.plusb3b.games.gamehub.domain.user.entity.User;
import kr.plusb3b.games.gamehub.domain.user.service.UserProdGameService;
import kr.plusb3b.games.gamehub.domain.user.service.UserProvider;
import kr.plusb3b.games.gamehub.security.AccessControlService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/game-hub/prod")
@RequiredArgsConstructor
public class UserProdController {

    private final AccessControlService accessControlService;
    private final UserProdGameService userProdGameService;
    private final UserProvider userProvider;
    private final GameStatusFormatter gameStatusFormatter;

    private static final String PROD_HOME_PATH = "prod/upload/index";
    private static final String UPLOAD_STATUS_PATH = "prod/upload-status/index";

    /**
     * 업로드 페이지로 이동
     */
    @GetMapping("/upload")
    public String viewUploadPage() {
        log.debug("게임 업로드 페이지 요청");
        return PROD_HOME_PATH;
    }

    /**
     * 업로드 상태 확인 페이지로 이동
     * HTML 주석 요구사항에 맞춰 요약 정보와 세부 정보 모두 제공
     */
    @GetMapping("/upload-check")
    public String viewUploadCheckPage(Model model, HttpServletRequest request) {
        log.debug("업로드 상태 확인 페이지 요청");

        // 1. 인증된 사용자 확인
        User user = accessControlService.getAuthenticatedUser(request);
        if (user == null) {
            log.warn("인증되지 않은 사용자의 업로드 상태 확인 시도");
            return "redirect:/login";
        }

        // 2. 사용자 권한 확인 (PRODUCER만 접근 가능)
        User.Role role = userProvider.hasRole(user);
        if (role == null || role != User.Role.ROLE_PRODUCER) {
            log.warn("권한이 없는 사용자의 업로드 상태 확인 시도: mbId={}, role={}", user.getMbId(), role);
            return "redirect:/access-denied";
        }

        // 3. 사용자가 업로드한 게임이 있는지 확인
        Long mbId = user.getMbId();
        boolean hasGames = userProdGameService.hasUserUploadedGames(mbId);

        if (!hasGames) {
            log.info("사용자가 업로드한 게임이 없음: mbId={}", mbId);
            model.addAttribute("hasGames", false);
            model.addAttribute("message", "아직 업로드한 게임이 없습니다.");
            return UPLOAD_STATUS_PATH;
        }

        // 4. 상태별 게임 데이터 조회 및 Model에 추가
        setupGameStatusData(model, mbId);

        // 5. 사용자 정보와 통계 추가
        setupUserInfo(model, user, mbId);

        log.info("업로드 상태 확인 페이지 데이터 준비 완료: mbId={}", mbId);
        return UPLOAD_STATUS_PATH;
    }

    /**
     * 상태별 게임 데이터를 Model에 설정
     */
    private void setupGameStatusData(Model model, Long mbId) {
        // 모든 상태별 게임 조회
        Map<Games.GameStatus, List<UserGameSummaryDto>> gamesByStatus =
                userProdGameService.getUserGamesByAllStatus(mbId);

        // HTML 주석 요구사항에 맞춰 각 상태별로 Model에 추가

        // 승인 대기 (PENDING_REVIEW)
        List<UserGameSummaryDto> pendingGames = gamesByStatus.getOrDefault(
                Games.GameStatus.PENDING_REVIEW, List.of());
        model.addAttribute("pendingGames", pendingGames);
        model.addAttribute("pendingCount", pendingGames.size());

        // 검토 중 (UNDER_REVIEW)
        List<UserGameSummaryDto> underReviewGames = gamesByStatus.getOrDefault(
                Games.GameStatus.UNDER_REVIEW, List.of());
        model.addAttribute("underReviewGames", underReviewGames);
        model.addAttribute("underReviewCount", underReviewGames.size());

        // 활성 (ACTIVE) - 서비스 중인 게임
        List<UserGameSummaryDto> activeGames = gamesByStatus.getOrDefault(
                Games.GameStatus.ACTIVE, List.of());
        model.addAttribute("activeGames", activeGames);
        model.addAttribute("activeCount", activeGames.size());

        // 거부 (REJECTED)
        List<UserGameSummaryDto> rejectedGames = gamesByStatus.getOrDefault(
                Games.GameStatus.REJECTED, List.of());
        model.addAttribute("rejectedGames", rejectedGames);
        model.addAttribute("rejectedCount", rejectedGames.size());

        // 일시 정지 (SUSPENDED)
        List<UserGameSummaryDto> suspendedGames = gamesByStatus.getOrDefault(
                Games.GameStatus.SUSPENDED, List.of());
        model.addAttribute("suspendedGames", suspendedGames);
        model.addAttribute("suspendedCount", suspendedGames.size());

        // 서비스 종료 (DEACTIVATED)
        List<UserGameSummaryDto> deactivatedGames = gamesByStatus.getOrDefault(
                Games.GameStatus.DEACTIVATED, List.of());
        model.addAttribute("deactivatedGames", deactivatedGames);
        model.addAttribute("deactivatedCount", deactivatedGames.size());

        // 업로드 실패 (UPLOAD_FAILED)
        List<UserGameSummaryDto> failedGames = gamesByStatus.getOrDefault(
                Games.GameStatus.UPLOAD_FAILED, List.of());
        model.addAttribute("failedGames", failedGames);
        model.addAttribute("failedCount", failedGames.size());

        // 전체 게임 수
        int totalGames = gamesByStatus.values().stream()
                .mapToInt(List::size)
                .sum();
        model.addAttribute("totalCount", totalGames);

        log.debug("상태별 게임 데이터 설정 완료: 총 {}개 게임", totalGames);
    }

    /**
     * 사용자 정보와 통계를 Model에 설정
     */
    private void setupUserInfo(Model model, User user, Long mbId) {
        // 사용자 기본 정보
        model.addAttribute("user", user);
        model.addAttribute("hasGames", true);

        // 상태별 통계
        Map<Games.GameStatus, Long> statistics = userProdGameService.getUserGameStatistics(mbId);
        model.addAttribute("statistics", statistics);

        // GameStatusFormatter를 Model에 추가 (Thymeleaf에서 사용)
        model.addAttribute("formatter", gameStatusFormatter);

        log.debug("사용자 정보 및 통계 설정 완료: mbId={}", mbId);
    }

    @GetMapping("/active-games")
    public String viewActiveGames(Model model, HttpServletRequest request) {
        User user = accessControlService.getAuthenticatedUser(request);
        if (user == null) return "redirect:/login";

        List<UserGameSummaryDto> activeGames = userProdGameService.getUserActiveGames(user.getMbId());
        model.addAttribute("activeGames", activeGames);
        model.addAttribute("formatter", gameStatusFormatter);

        return "prod/active-games/index";
    }

    /**
     * 승인 대기 게임만 조회
     */
    @GetMapping("/pending-games")
    public String viewPendingGames(Model model, HttpServletRequest request) {
        User user = accessControlService.getAuthenticatedUser(request);
        if (user == null) return "redirect:/login";

        List<UserGameSummaryDto> pendingGames = userProdGameService.getUserPendingGames(user.getMbId());
        model.addAttribute("pendingGames", pendingGames);
        model.addAttribute("formatter", gameStatusFormatter);

        return "prod/pending-games/index";
    }
}