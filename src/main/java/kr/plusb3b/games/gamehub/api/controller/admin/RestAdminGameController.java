package kr.plusb3b.games.gamehub.api.controller.admin;

import kr.plusb3b.games.gamehub.domain.game.entity.Games;
import kr.plusb3b.games.gamehub.domain.game.entity.GamesFile;
import kr.plusb3b.games.gamehub.domain.game.repository.GamesRepository;
import kr.plusb3b.games.gamehub.domain.game.service.GameMetadataUpdateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/games")
@Slf4j
public class RestAdminGameController {

    private final GameMetadataUpdateService gameMetadataUpdateService;
    private final GamesRepository gamesRepository;

    public RestAdminGameController(GameMetadataUpdateService gameMetadataUpdateService,
                                   GamesRepository gamesRepository) {
        this.gameMetadataUpdateService = gameMetadataUpdateService;
        this.gamesRepository = gamesRepository;
    }

    /**
     * 승인 대기 → 검토 중
     */
    @PatchMapping("/{gameId}/review")
    public ResponseEntity<?> startReview(@PathVariable Long gameId) {
        try {
            log.info("게임 검토 시작 요청 - 게임ID: {}", gameId);

            // 게임 상태를 UNDER_REVIEW로 변경
            int gameResult = gameMetadataUpdateService.updateGameToDB(gameId, Games.GameStatus.UNDER_REVIEW);

            // 파일 상태는 변경하지 않음 (TEMP 상태 유지)

            Map<String, Object> response = new HashMap<>();
            response.put("success", gameResult > 0);
            response.put("message", gameResult > 0 ? "검토 단계로 이동했습니다." : "상태 변경에 실패했습니다.");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("게임 검토 시작 실패 - 게임ID: {}, 오류: {}", gameId, e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "서버 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * 검토 중 → 활성 (승인)
     */
    @PatchMapping("/{gameId}/approve")
    public ResponseEntity<?> approveGame(@PathVariable Long gameId) {
        try {
            log.info("게임 승인 요청 - 게임ID: {}", gameId);

            // 게임 상태를 ACTIVE로 변경
            int gameResult = gameMetadataUpdateService.updateGameToDB(gameId, Games.GameStatus.ACTIVE);

            // 게임 파일도 ACTIVE로 변경 (파일 이동 포함)
            Games game = gamesRepository.findById(gameId).orElse(null);
            int fileResult = 0;
            if (game != null && game.getGamesFile() != null) {
                fileResult = gameMetadataUpdateService.updateGameFileToDB(
                        game.getGamesFile().getFileId(),
                        GamesFile.FileStatus.ACTIVE
                );
            }

            Map<String, Object> response = new HashMap<>();
            response.put("success", gameResult > 0 && fileResult > 0);
            response.put("message", (gameResult > 0 && fileResult > 0) ? "게임이 승인되어 활성화되었습니다." : "승인 처리에 실패했습니다.");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("게임 승인 실패 - 게임ID: {}, 오류: {}", gameId, e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "서버 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * 검토 중 → 승인 거절
     */
    @PatchMapping("/{gameId}/reject")
    public ResponseEntity<?> rejectGame(@PathVariable Long gameId, @RequestBody(required = false) Map<String, String> requestBody) {
        try {
            String reason = requestBody != null ? requestBody.get("reason") : "사유 없음";
            log.info("게임 거절 요청 - 게임ID: {}, 사유: {}", gameId, reason);

            // 게임 상태를 REJECTED로 변경
            int gameResult = gameMetadataUpdateService.updateGameToDB(gameId, Games.GameStatus.REJECTED);

            // 파일 상태는 TEMP로 유지

            Map<String, Object> response = new HashMap<>();
            response.put("success", gameResult > 0);
            response.put("message", gameResult > 0 ? "게임이 거절되었습니다." : "거절 처리에 실패했습니다.");
            response.put("reason", reason);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("게임 거절 실패 - 게임ID: {}, 오류: {}", gameId, e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "서버 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * 활성 → 일시 중단
     */
    @PatchMapping("/{gameId}/suspend")
    public ResponseEntity<?> suspendGame(@PathVariable Long gameId) {
        try {
            log.info("게임 일시 중단 요청 - 게임ID: {}", gameId);

            // 게임 상태를 SUSPENDED로 변경
            int gameResult = gameMetadataUpdateService.updateGameToDB(gameId, Games.GameStatus.SUSPENDED);

            // 파일은 그대로 두고 게임 상태로 접근 제어

            Map<String, Object> response = new HashMap<>();
            response.put("success", gameResult > 0);
            response.put("message", gameResult > 0 ? "게임이 일시 중단되었습니다." : "일시 중단 처리에 실패했습니다.");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("게임 일시 중단 실패 - 게임ID: {}, 오류: {}", gameId, e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "서버 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * 활성 → 서비스 종료
     */
    @PatchMapping("/{gameId}/deactivate")
    public ResponseEntity<?> deactivateGame(@PathVariable Long gameId) {
        try {
            log.info("게임 서비스 종료 요청 - 게임ID: {}", gameId);

            // 게임 상태를 DEACTIVATED로 변경
            int gameResult = gameMetadataUpdateService.updateGameToDB(gameId, Games.GameStatus.DEACTIVATED);

            // 게임 파일도 DEACTIVATED로 변경 (파일 이동 포함)
            Games game = gamesRepository.findById(gameId).orElse(null);
            int fileResult = 0;
            if (game != null && game.getGamesFile() != null) {
                fileResult = gameMetadataUpdateService.updateGameFileToDB(
                        game.getGamesFile().getFileId(),
                        GamesFile.FileStatus.DEACTIVATED
                );
            }

            Map<String, Object> response = new HashMap<>();
            response.put("success", gameResult > 0);
            response.put("message", gameResult > 0 ? "게임 서비스가 종료되었습니다." : "서비스 종료 처리에 실패했습니다.");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("게임 서비스 종료 실패 - 게임ID: {}, 오류: {}", gameId, e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "서버 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * 일시 중단 → 활성 (재활성화)
     */
    @PatchMapping("/{gameId}/reactivate")
    public ResponseEntity<?> reactivateGame(@PathVariable Long gameId) {
        try {
            log.info("게임 재활성화 요청 - 게임ID: {}", gameId);

            // 게임 상태를 ACTIVE로 변경
            int gameResult = gameMetadataUpdateService.updateGameToDB(gameId, Games.GameStatus.ACTIVE);

            // 파일이 이미 ACTIVE 상태라면 그대로 두기

            Map<String, Object> response = new HashMap<>();
            response.put("success", gameResult > 0);
            response.put("message", gameResult > 0 ? "게임이 재활성화되었습니다." : "재활성화 처리에 실패했습니다.");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("게임 재활성화 실패 - 게임ID: {}, 오류: {}", gameId, e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "서버 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * 노출/숨김 토글
     */
    @PatchMapping("/{gameId}/toggle-visibility")
    public ResponseEntity<?> toggleVisibility(@PathVariable Long gameId) {
        try {
            log.info("게임 노출 상태 토글 요청 - 게임ID: {}", gameId);

            int result = gameMetadataUpdateService.toggleGameVisibility(gameId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", result > 0);
            response.put("message", result > 0 ? "노출 상태가 변경되었습니다." : "상태 변경에 실패했습니다.");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("노출 상태 토글 실패 - 게임ID: {}, 오류: {}", gameId, e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "서버 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * 게임 플레이 URL 조회
     */
    @GetMapping("/{gameId}/play-url")
    public ResponseEntity<?> getPlayUrl(@PathVariable Long gameId) {
        try {
            log.info("게임 플레이 URL 조회 요청 - 게임ID: {}", gameId);

            Games game = gamesRepository.findById(gameId).orElse(null);

            Map<String, Object> response = new HashMap<>();
            if (game != null && game.getGamesFile() != null && game.getGamesFile().getFileStatus() == GamesFile.FileStatus.ACTIVE) {
                response.put("success", true);
                response.put("playUrl", game.getGamesFile().getGameUrl());
                response.put("message", "플레이 URL을 찾았습니다.");
            } else {
                response.put("success", false);
                response.put("message", "활성화된 게임 파일을 찾을 수 없습니다.");
            }

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("플레이 URL 조회 실패 - 게임ID: {}, 오류: {}", gameId, e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "서버 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
}