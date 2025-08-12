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
        try {
            log.info("PENDING_REVIEW 게임 조회 시작");

            List<Games> allGames = gamesRepo.findAll();
            log.info("전체 게임 수: {}", allGames.size());

            List<Games> pendingGames = allGames.stream()
                    .filter(g -> g.getStatus() == Games.GameStatus.PENDING_REVIEW)
                    .collect(Collectors.toList());

            log.info("PENDING_REVIEW 상태 게임 수: {}", pendingGames.size());

            if (pendingGames.isEmpty()) {
                return Optional.empty();
            }

            GamesInfoDto dto = new GamesInfoDto(pendingGames, List.of());
            return Optional.of(dto);

        } catch (Exception e) {
            log.error("notApprovedGames 조회 실패", e);
            return Optional.empty();
        }
    }


    @Override
    public Optional<GamesInfoDto> approvedGames() {
        try {
            log.info("ACTIVE 게임 조회 시작");

            List<Games> allGames = gamesRepo.findAll();
            List<Games> activeGames = allGames.stream()
                    .filter(g -> g.getStatus() == Games.GameStatus.ACTIVE)
                    .collect(Collectors.toList());

            log.info("ACTIVE 상태 게임 수: {}", activeGames.size());

            if (activeGames.isEmpty()) {
                return Optional.empty();
            }

            GamesInfoDto dto = new GamesInfoDto(activeGames, List.of());
            return Optional.of(dto);

        } catch (Exception e) {
            log.error("approvedGames 조회 실패", e);
            return Optional.empty();
        }
    }


    @Override
    public Optional<GamesInfoDto> otherGames(Games.GameStatus status) {
        try {
            log.info("{} 상태 게임 조회 시작", status);

            List<Games> allGames = gamesRepo.findAll();
            List<Games> filteredGames = allGames.stream()
                    .filter(g -> g.getStatus() == status)
                    .collect(Collectors.toList());

            log.info("{} 상태 게임 수: {}", status, filteredGames.size());

            if (filteredGames.isEmpty()) {
                return Optional.empty();
            }

            GamesInfoDto dto = new GamesInfoDto(filteredGames, List.of());
            return Optional.of(dto);

        } catch (Exception e) {
            log.error("otherGames({}) 조회 실패", status, e);
            return Optional.empty();
        }
    }
}