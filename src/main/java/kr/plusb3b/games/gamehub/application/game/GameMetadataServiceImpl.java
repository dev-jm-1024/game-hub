package kr.plusb3b.games.gamehub.application.game;

import kr.plusb3b.games.gamehub.domain.board.entity.Board;
import kr.plusb3b.games.gamehub.domain.game.dto.GameUploadDto;
import kr.plusb3b.games.gamehub.domain.game.dto.GamesInfoDto;
import kr.plusb3b.games.gamehub.domain.game.entity.Games;
import kr.plusb3b.games.gamehub.domain.game.entity.GamesFile;
import kr.plusb3b.games.gamehub.domain.game.exception.GameUploadException;
import kr.plusb3b.games.gamehub.domain.game.repository.GamesRepository;
import kr.plusb3b.games.gamehub.domain.game.repository.GamesFileRepository;
import kr.plusb3b.games.gamehub.domain.game.service.GameMetadataService;
import kr.plusb3b.games.gamehub.domain.game.vo.GamesVO;
import kr.plusb3b.games.gamehub.domain.user.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class GameMetadataServiceImpl implements GameMetadataService {

    private final GamesRepository gamesRepo;
    private final GamesFileRepository gamesFileRepo;

    // 생성자 주입

    public GameMetadataServiceImpl(GamesRepository gamesRepo, GamesFileRepository gamesFileRepo) {
        this.gamesRepo = gamesRepo;
        this.gamesFileRepo = gamesFileRepo;
    }

    @Override
    public Games saveGameToDB(GameUploadDto dto, GamesVO gvo, User user, Board board) {
        try {
            // Games 엔티티 생성 (파일 관련 정보는 제외)
            Games saveGame = new Games(
                    board, user,
                    dto.getGameName(), dto.getGameDescription(), dto.getTeamName(),
                    dto.getSpecs(), LocalDateTime.now(), dto.getGameVersion(),
                    dto.getGenre(), dto.getPlatform(),
                    gvo.getGameStatus(), gvo.getApprovedAt(), gvo.getIsVisible()
            );

            Games result = gamesRepo.save(saveGame);
            log.info("게임 정보 DB 저장 성공 - 게임ID: {}, 게임명: {}", result.getGameId(), result.getGameName());
            return result;

        } catch (DataIntegrityViolationException e) {
            log.error("DB 제약 조건 위반 - 게임 저장 실패: {}", e.getMessage());
            throw new GameUploadException("중복된 게임이거나 잘못된 데이터입니다.", e);

        } catch (DataAccessException e) {
            log.error("DB 접근 오류 - 게임 저장 실패: {}", e.getMessage());
            throw new GameUploadException("데이터베이스 저장 중 오류가 발생했습니다.", e);

        } catch (Exception e) {
            log.error("예상치 못한 오류 - 게임 저장 실패: {}", e.getMessage());
            throw new GameUploadException("게임 정보 저장 중 오류가 발생했습니다.", e);
        }
    }

    @Override
    public GamesFile saveGameFileToDB(Games game, GamesFile gamesFile) {
        try {
            // Games와 GamesFile 관계 설정
            gamesFile.setGame(game);

            GamesFile result = gamesFileRepo.save(gamesFile);
            log.info("게임 파일 정보 DB 저장 성공 - 파일ID: {}, 해시: {}", result.getFileId(), result.getGameHash());
            return result;

        } catch (DataIntegrityViolationException e) {
            log.error("DB 제약 조건 위반 - 게임 파일 저장 실패: {}", e.getMessage());
            throw new GameUploadException("중복된 파일이거나 잘못된 데이터입니다.", e);

        } catch (DataAccessException e) {
            log.error("DB 접근 오류 - 게임 파일 저장 실패: {}", e.getMessage());
            throw new GameUploadException("파일 정보 저장 중 데이터베이스 오류가 발생했습니다.", e);

        } catch (Exception e) {
            log.error("예상치 못한 오류 - 게임 파일 저장 실패: {}", e.getMessage());
            throw new GameUploadException("파일 정보 저장 중 오류가 발생했습니다.", e);
        }
    }


    @Override
    public Optional<GamesInfoDto> notApprovedGames() {
        // 🔄 개선: JOIN FETCH로 한 번에 조회 (N+1 문제 해결)
        List<Games> gamesWithFiles = gamesRepo.findPendingReviewGamesWithFiles();

        if (gamesWithFiles.isEmpty()) {
            return Optional.empty();
        }

        // Games에서 GamesFile 추출
        List<Games> gamesList = gamesWithFiles;
        List<GamesFile> gamesFileList = gamesWithFiles.stream()
                .map(Games::getGamesFile)
                .filter(Objects::nonNull) // null 체크
                .collect(Collectors.toList());

        GamesInfoDto dto = new GamesInfoDto(gamesList, gamesFileList);
        return Optional.of(dto);
    }

    @Override
    public Optional<GamesInfoDto> approvedGames() {
        // 🔄 개선: JOIN FETCH로 한 번에 조회
        List<Games> gamesWithFiles = gamesRepo.findActiveGamesWithFiles();

        if (gamesWithFiles.isEmpty()) {
            return Optional.empty();
        }

        List<Games> gamesList = gamesWithFiles;
        List<GamesFile> gamesFileList = gamesWithFiles.stream()
                .map(Games::getGamesFile)
                .filter(Objects::nonNull) // null 체크
                .collect(Collectors.toList());

        GamesInfoDto dto = new GamesInfoDto(gamesList, gamesFileList);
        return Optional.of(dto);
    }
}