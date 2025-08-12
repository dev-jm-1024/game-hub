package kr.plusb3b.games.gamehub.application.game;

import jakarta.persistence.EntityNotFoundException;
import kr.plusb3b.games.gamehub.domain.game.entity.Games;
import kr.plusb3b.games.gamehub.domain.game.entity.GamesFile;
import kr.plusb3b.games.gamehub.domain.game.repository.GamesFileRepository;
import kr.plusb3b.games.gamehub.domain.game.repository.GamesRepository;
import kr.plusb3b.games.gamehub.domain.game.service.GameMetadataUpdateService;
import kr.plusb3b.games.gamehub.upload.googleCloud.CloudUploadService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static kr.plusb3b.games.gamehub.domain.game.entity.Games.GameStatus.*;
import static kr.plusb3b.games.gamehub.domain.game.entity.Games.GameStatus.DEACTIVATED;
import static kr.plusb3b.games.gamehub.domain.game.entity.GamesFile.FileStatus.*;

@Service
public class GameMetadataUpdateServiceImpl implements GameMetadataUpdateService {

    private final GamesRepository gamesRepo;
    private final GamesFileRepository gamesFileRepo;
    private final CloudUploadService cloudUploadService;

    public GameMetadataUpdateServiceImpl(GamesRepository gamesRepo, GamesFileRepository gamesFileRepo,
                                         CloudUploadService cloudUploadService) {
        this.gamesRepo = gamesRepo;
        this.gamesFileRepo = gamesFileRepo;
        this.cloudUploadService = cloudUploadService;
    }

    @Override
    @Transactional
    public int updateGameToDB(Long gameId, Games.GameStatus status) {

        Games games = gamesRepo.findById(gameId)
                .orElseThrow(() -> new EntityNotFoundException("Game not found: " + gameId));

        return updateGameByStatus(games, status);
    }

    @Override
    @Transactional
    public int updateGameFileToDB(Long fileId, GamesFile.FileStatus status) {

        GamesFile gamesFile = gamesFileRepo.findById(fileId)
                .orElseThrow(() -> new EntityNotFoundException("Game file not found: " + fileId));

        return updateGameFileByStatus(gamesFile, status);
    }

    @Override
    @Transactional
    public int toggleGameVisibility(Long gameId) {
        Games games = gamesRepo.findById(gameId)
                .orElseThrow(() -> new EntityNotFoundException("Game not found: " + gameId));

        // 현재 노출 상태의 반대로 설정
        int newVisibility = games.getIsVisible() == 1 ? 0 : 1;

        return gamesRepo.updateVisibilityByGameId(newVisibility, gameId);
    }

    private int updateGameByStatus(Games games, Games.GameStatus status){

        Long gameId = games.getGameId();

        int result;
        switch (status) {

            //검토
            case UNDER_REVIEW:
                result = gamesRepo.updateApprovalVisibilityAndStatusByGameId(
                        games.getApprovedAt(),
                        games.getIsVisible(),
                        Games.GameStatus.UNDER_REVIEW,
                        gameId
                );
                break;

            //거절
            case REJECTED:
                result = gamesRepo.updateApprovalVisibilityAndStatusByGameId(
                        games.getApprovedAt(),
                        games.getIsVisible(),
                        Games.GameStatus.REJECTED,
                        gameId
                );
                break;

            //승인
            case ACTIVE:
                result = gamesRepo.updateApprovalVisibilityAndStatusByGameId(
                        LocalDateTime.now(),
                        1,
                        Games.GameStatus.ACTIVE,
                        gameId
                );
                break;

            //일시정지
            case SUSPENDED:
                result = gamesRepo.updateApprovalVisibilityAndStatusByGameId(
                        games.getApprovedAt(),
                        0,
                        SUSPENDED,
                        gameId
                );
                break;

            //서비스 종료
            case DEACTIVATED:
                result = gamesRepo.updateApprovalVisibilityAndStatusByGameId(
                        games.getApprovedAt(),
                        0,
                        DEACTIVATED,
                        gameId
                );
                break;

            default:
                throw new IllegalArgumentException("Unsupported game status: " + status);
        }

        return result;
    }

    private int updateGameFileByStatus(GamesFile gamesFile, GamesFile.FileStatus status) {

        Long fileId = gamesFile.getFileId();

        switch (status) {
            case ACTIVE:
                // CloudUploadService의 moveFileToActivateFolder 사용
                String gameId = String.valueOf(gamesFile.getGame().getGameId());
                String activatedUrl = cloudUploadService.moveFileToActivateFolder(gameId, gamesFile.getGameUrl());

                return gamesFileRepo.updateGameUrlAndFileStatusByFileId(
                        activatedUrl,
                        GamesFile.FileStatus.ACTIVE,
                        fileId);

            case DEACTIVATED:
                // CloudUploadService의 moveFileToDeactivateFolder 사용
                String deactivatedGameId = String.valueOf(gamesFile.getGame().getGameId());
                String deactivatedUrl = cloudUploadService.moveFileToDeactivateFolder(
                        deactivatedGameId,
                        gamesFile.getGameUrl());

                return gamesFileRepo.updateGameUrlAndFileStatusByFileId(
                        deactivatedUrl,
                        GamesFile.FileStatus.DEACTIVATED,
                        fileId);

            default:
                throw new IllegalArgumentException("Unsupported game file status: " + status);
        }
    }
}