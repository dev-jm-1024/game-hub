package kr.plusb3b.games.gamehub.api.controller.game.rest;

import kr.plusb3b.games.gamehub.domain.board.entity.Board;
import kr.plusb3b.games.gamehub.domain.board.repository.BoardRepository;
import kr.plusb3b.games.gamehub.domain.game.dto.GameUploadDto;
import kr.plusb3b.games.gamehub.domain.game.dto.GameUploadResponseDto;
import kr.plusb3b.games.gamehub.domain.game.entity.Games;
import kr.plusb3b.games.gamehub.domain.game.entity.GamesFile;
import kr.plusb3b.games.gamehub.domain.game.exception.GameUploadException;
import kr.plusb3b.games.gamehub.domain.game.service.GameMetadataService;
import kr.plusb3b.games.gamehub.domain.game.service.GameUploadValidator;
import kr.plusb3b.games.gamehub.domain.game.vo.GamesVO;
import kr.plusb3b.games.gamehub.domain.user.entity.User;
import kr.plusb3b.games.gamehub.security.AccessControlService;
import kr.plusb3b.games.gamehub.upload.googleCloud.CloudUploadService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import kr.plusb3b.games.gamehub.domain.game.dto.ErrorResponseDto;

@RestController
@RequestMapping("/game-hub/api/v1/games")
@Slf4j
public class RestGameUploadController {

    private final CloudUploadService gcpUploadService;
    private final GameMetadataService gameMetadataService;
    private final GameUploadValidator gameUploadValidator;
    private final AccessControlService access;
    private final BoardRepository boardRepo;

    public RestGameUploadController(CloudUploadService gcpUploadService,
                                    GameMetadataService gameMetadataService,
                                    GameUploadValidator gameUploadValidator,
                                    AccessControlService access,
                                    BoardRepository boardRepo) {
        this.gcpUploadService = gcpUploadService;
        this.gameMetadataService = gameMetadataService;
        this.gameUploadValidator = gameUploadValidator;
        this.access = access;
        this.boardRepo = boardRepo;
    }

    @PostMapping("/upload")
    @Transactional
    public ResponseEntity<?> uploadGame(@Valid @ModelAttribute GameUploadDto gameUploadDto,
                                        HttpServletRequest request) {

        log.info("게임 업로드 요청 시작 - 게임명: {}, 팀명: {}",
                gameUploadDto.getGameName(), gameUploadDto.getTeamName());

        try {
            // 1. 로그인 사용자 검증
            User user = access.getAuthenticatedUser(request);
            if (user == null) {
                log.warn("미인증 사용자의 업로드 시도");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ErrorResponseDto.of("로그인 후 이용할 수 있습니다.", "AUTH_REQUIRED"));
            }

            // 2. 파일 존재 여부 검증
            if (gameUploadDto.getGameFile() == null || gameUploadDto.getGameFile().isEmpty()) {
                log.warn("빈 파일 업로드 시도 - 사용자: {}", user.getUserAuth().getAuthUserId());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ErrorResponseDto.of("업로드할 파일을 선택해주세요.", "FILE_REQUIRED"));
            }

            // 3. HTML 파일 존재 여부 검증
            boolean hasIndexHtml = gameUploadValidator.isIndexHtml(gameUploadDto.getGameFile());
            if (!hasIndexHtml) {
                log.warn("index.html 파일 누락 - 사용자: {}, 파일: {}",
                        user.getUserAuth().getAuthUserId(), gameUploadDto.getGameFile().getOriginalFilename());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ErrorResponseDto.of("해당 파일에 index.html 파일이 존재하지 않습니다.", "INVALID_WEBGL_STRUCTURE"));
            }

            // 4. 게시판 조회
            Board board = boardRepo.findById("gameBoard")
                    .orElseThrow(() -> new IllegalArgumentException("게임 게시판을 찾을 수 없습니다."));

            // 5. GCP에 파일 업로드
            log.info("GCP 업로드 시작 - 파일: {}", gameUploadDto.getGameFile().getOriginalFilename());
            GamesFile uploadResult = gcpUploadService.uploadFileToGCP(gameUploadDto.getGameFile());

            // 6. 게임 메타데이터 저장
            log.info("게임 메타데이터 저장 시작 - 게임명: {}", gameUploadDto.getGameName());
            Games savedGame = gameMetadataService.saveGameToDB(gameUploadDto, new GamesVO(), user, board);

            // 7. 게임 파일 정보 저장
            log.info("게임 파일 정보 저장 시작 - 게임ID: {}", savedGame.getGameId());
            GamesFile savedGamesFile = gameMetadataService.saveGameFileToDB(savedGame, uploadResult);

            // 8. 성공 응답 생성
            GameUploadResponseDto responseDto = GameUploadResponseDto.builder()
                    .gameId(savedGame.getGameId())
                    .gameName(savedGame.getGameName())
                    .teamName(savedGame.getTeamName())
                    .status(savedGame.getStatus().name())
                    .fileUrl(savedGamesFile.getGameUrl())
                    .uploadedAt(savedGamesFile.getUploadedAt())
                    .message("성공적으로 업로드되었습니다. 관리자의 승인을 기다리세요.")
                    .success(true)
                    .build();

            log.info("게임 업로드 완료 - 게임ID: {}, 파일ID: {}",
                    savedGame.getGameId(), savedGamesFile.getFileId());

            return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);

        } catch (GameUploadException e) {
            log.error("게임 업로드 실패 - 사용자 오류: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ErrorResponseDto.of(e.getMessage(), "UPLOAD_ERROR"));

        } catch (IllegalArgumentException e) {
            log.error("게임 업로드 실패 - 잘못된 인수: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ErrorResponseDto.of("잘못된 요청입니다: " + e.getMessage(), "INVALID_REQUEST"));

        } catch (Exception e) {
            log.error("게임 업로드 중 예상치 못한 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ErrorResponseDto.of("서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요.", "INTERNAL_ERROR"));
        }
    }
}