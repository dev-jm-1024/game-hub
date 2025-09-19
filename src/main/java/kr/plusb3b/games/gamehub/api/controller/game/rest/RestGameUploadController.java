package kr.plusb3b.games.gamehub.api.controller.game.rest;

import jakarta.servlet.http.HttpServletResponse;
import kr.plusb3b.games.gamehub.common.util.FileValidator;
import kr.plusb3b.games.gamehub.domain.board.entity.Board;
import kr.plusb3b.games.gamehub.domain.board.repository.BoardRepository;
import kr.plusb3b.games.gamehub.domain.board.service.BoardService;
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
    private final BoardService boardService;


    public RestGameUploadController(CloudUploadService gcpUploadService,
                                    GameMetadataService gameMetadataService,
                                    GameUploadValidator gameUploadValidator,
                                    AccessControlService access,
                                    BoardService boardService) {
        this.gcpUploadService = gcpUploadService;
        this.gameMetadataService = gameMetadataService;
        this.gameUploadValidator = gameUploadValidator;
        this.access = access;
        this.boardService = boardService;
    }

    @PostMapping("/upload")
    @Transactional
    public ResponseEntity<?> uploadGame(@Valid @ModelAttribute GameUploadDto gameUploadDto,
                                        HttpServletRequest request,
                                        HttpServletResponse response) {

        log.info("게임 업로드 요청 시작 - 게임명: {}, 팀명: {}",
                gameUploadDto.getGameName(), gameUploadDto.getTeamName());

        try {
            // 1. 로그인 사용자 검증
            access.validateUserAccess(request, response);
            User user = access.getAuthenticatedUser(request);

            //피일 존재 여부 확인
            FileValidator.validateGameFile(gameUploadDto.getGameFile());


            // 3. HTML 파일 존재 여부 검증
            boolean hasIndexHtml = gameUploadValidator.isIndexHtml(gameUploadDto.getGameFile());
            if (!hasIndexHtml) {
                log.warn("index.html 파일 누락 - 사용자: {}, 파일: {}",
                        user.getUserAuth().getAuthUserId(), gameUploadDto.getGameFile().getOriginalFilename());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ErrorResponseDto.of("해당 파일에 index.html 파일이 존재하지 않습니다.", "INVALID_WEBGL_STRUCTURE"));
            }

            // 4. 게시판 조회
            boolean validateBoard = boardService.validateBoard("gameBoard");
            if(!validateBoard) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("게시판이 존재하지 않습니다");

            Board board = boardService.getBoardByBoardId("gameBoard");

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
            GameUploadResponseDto responseDto = gameMetadataService.responseGameUploadResult(savedGame, savedGamesFile);

            log.info("게임 업로드 완료 - 게임ID: {}, 파일ID: {}",
                    savedGame.getGameId(), savedGamesFile.getFileId());

            return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);

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