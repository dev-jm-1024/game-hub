package kr.plusb3b.games.gamehub.application.user;

import kr.plusb3b.games.gamehub.domain.game.dto.GamesInfoDto;
import kr.plusb3b.games.gamehub.domain.game.entity.Games;
import kr.plusb3b.games.gamehub.domain.game.entity.GamesFile;
import kr.plusb3b.games.gamehub.domain.game.repository.GamesFileRepository;
import kr.plusb3b.games.gamehub.domain.game.repository.GamesRepository;
import kr.plusb3b.games.gamehub.domain.user.dto.UserGameSummaryDto;
import kr.plusb3b.games.gamehub.domain.user.service.UserProdGameService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserProdGameServiceImpl implements UserProdGameService {

    private final GamesRepository gamesRepository;
    private final GamesFileRepository gamesFileRepository;

    @Override
    public List<Games> getUserProdGame(Long mbId, Games.GameStatus status) {
        log.debug("사용자 ID: {}, 상태: {}로 게임 조회 시작", mbId, status);

        // 🔥 개선: DB 레벨에서 필터링 (기존의 메모리 필터링 제거)
        List<Games> games = gamesRepository.findAllByUser_MbIdAndStatusOrderByCreatedAtDesc(mbId, status);

        log.debug("조회된 게임 수: {}", games.size());
        return games;
    }

    @Override
    public List<GamesFile> getUserProdGameFile(Long gameId, GamesFile.FileStatus status) {
        log.debug("게임 ID: {}, 파일 상태: {}로 파일 조회 시작", gameId, status);

        // 🔥 개선: 파일 상태 필터링 적용 (기존에 누락되어 있던 부분)
        if (status != null) {
            return gamesFileRepository.findByGameGameIdAndFileStatusOrderByUploadedAtDesc(gameId, status);
        } else {
            return gamesFileRepository.findByGameGameIdOrderByUploadedAtDesc(gameId);
        }
    }

    @Override
    public GamesInfoDto getUserCombinedGame(List<Games> games, List<GamesFile> gamesFile) {
        // 🔥 개선: null 체크 로직 개선
        if (games == null) games = new ArrayList<>();
        if (gamesFile == null) gamesFile = new ArrayList<>();

        // 빈 리스트인 경우 빈 DTO 반환 (null 대신)
        return new GamesInfoDto(games, gamesFile);
    }

    @Override
    public Map<Games.GameStatus, List<UserGameSummaryDto>> getUserGamesByAllStatus(Long mbId) {
        log.debug("사용자 ID: {}의 모든 상태별 게임 조회 시작", mbId);

        // 🔥 성능 최적화: 게임과 파일 정보를 한번에 조회 (N+1 문제 해결)
        List<Games> gamesWithFiles = gamesRepository.findAllByUser_MbIdWithFilesOrderByCreatedAtDesc(mbId);

        // 🔥 개선: Stream API로 상태별 그룹화 + DTO 변환
        Map<Games.GameStatus, List<UserGameSummaryDto>> gamesByStatus = gamesWithFiles.stream()
                .collect(Collectors.groupingBy(
                        Games::getStatus,
                        Collectors.mapping(
                                game -> new UserGameSummaryDto(game, game.getGamesFile()),
                                Collectors.toList()
                        )
                ));

        log.debug("상태별 게임 분류 완료: {}", gamesByStatus.keySet());
        return gamesByStatus;
    }

    @Override
    public List<UserGameSummaryDto> getUserGameSummaries(Long mbId) {
        log.debug("사용자 ID: {}의 게임 요약 정보 조회 시작", mbId);

        // 🔥 성능 최적화: JOIN FETCH로 한번에 조회
        List<Games> gamesWithFiles = gamesRepository.findAllByUser_MbIdWithFilesOrderByCreatedAtDesc(mbId);

        // 🔥 개선: DTO 변환
        List<UserGameSummaryDto> summaries = gamesWithFiles.stream()
                .map(game -> new UserGameSummaryDto(game, game.getGamesFile()))
                .collect(Collectors.toList());

        log.debug("게임 요약 정보 {}개 조회 완료", summaries.size());
        return summaries;
    }

    @Override
    public boolean hasUserUploadedGames(Long mbId) {
        log.debug("사용자 ID: {}의 게임 업로드 여부 확인", mbId);

        // 🔥 개선: 예외 대신 boolean 반환으로 정상적인 흐름 처리
        boolean hasGames = gamesRepository.existsByUser_MbId(mbId);

        log.debug("사용자 게임 존재 여부: {}", hasGames);
        return hasGames;
    }

    /**
     * 🆕 사용자의 특정 상태 게임 개수 조회
     */
    @Override
    public long getUserGameCountByStatus(Long mbId, Games.GameStatus status) {
        return gamesRepository.findAllByUser_MbIdAndStatusOrderByCreatedAtDesc(mbId, status).size();
    }

    /**
     * 🆕 사용자의 활성 게임들만 조회 (플레이 가능한 게임들)
     */
    @Override
    public List<UserGameSummaryDto> getUserActiveGames(Long mbId) {
        List<Games> activeGames = gamesRepository.findAllByUser_MbIdAndStatusWithFilesOrderByCreatedAtDesc(
                mbId, Games.GameStatus.ACTIVE);

        return activeGames.stream()
                .filter(game -> game.isVisible()) // 노출된 게임만
                .filter(game -> game.getGamesFile() != null &&
                        game.getGamesFile().getFileStatus() == GamesFile.FileStatus.ACTIVE)
                .map(game -> new UserGameSummaryDto(game, game.getGamesFile()))
                .collect(Collectors.toList());
    }

    /**
     * 🆕 사용자의 승인 대기 게임들 조회
     */
    @Override
    public List<UserGameSummaryDto> getUserPendingGames(Long mbId) {
        List<Games> pendingGames = gamesRepository.findAllByUser_MbIdAndStatusWithFilesOrderByCreatedAtDesc(
                mbId, Games.GameStatus.PENDING_REVIEW);

        return pendingGames.stream()
                .map(game -> new UserGameSummaryDto(game, game.getGamesFile()))
                .collect(Collectors.toList());
    }

    /**
     * 🆕 사용자의 상태별 게임 통계 조회
     */
    @Override
    public Map<Games.GameStatus, Long> getUserGameStatistics(Long mbId) {
        List<Object[]> statistics = gamesRepository.countGamesByStatusForUser(mbId);

        return statistics.stream()
                .collect(Collectors.toMap(
                        row -> (Games.GameStatus) row[0],
                        row -> (Long) row[1]
                ));
    }
}