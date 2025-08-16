package kr.plusb3b.games.gamehub.domain.user.service;

import kr.plusb3b.games.gamehub.domain.game.dto.GamesInfoDto;
import kr.plusb3b.games.gamehub.domain.game.entity.Games;
import kr.plusb3b.games.gamehub.domain.game.entity.GamesFile;
import kr.plusb3b.games.gamehub.domain.user.dto.UserGameSummaryDto;

import java.util.List;
import java.util.Map;

/**
 * 사용자(게임 개발자)의 게임 관리 서비스
 */
public interface UserProdGameService {

    /**
     * 사용자의 특정 상태 게임들 조회
     */
    List<Games> getUserProdGame(Long mbId, Games.GameStatus status);

    /**
     * 사용자의 특정 게임의 파일들 조회
     */
    List<GamesFile> getUserProdGameFile(Long gameId, GamesFile.FileStatus status);

    /**
     * 게임과 파일 정보를 결합한 DTO 생성
     */
    GamesInfoDto getUserCombinedGame(List<Games> games, List<GamesFile> gamesFile);

    /**
     * 🆕 사용자의 모든 게임을 상태별로 그룹화하여 조회
     */
    Map<Games.GameStatus, List<UserGameSummaryDto>> getUserGamesByAllStatus(Long mbId);

    /**
     * 🆕 사용자의 게임 요약 정보 목록 조회
     */
    List<UserGameSummaryDto> getUserGameSummaries(Long mbId);

    /**
     * 🆕 사용자가 업로드한 게임이 있는지 확인
     */
    boolean hasUserUploadedGames(Long mbId);

    /**
     * 🆕 사용자의 활성 게임들만 조회 (플레이 가능한 게임들)
     */
    List<UserGameSummaryDto> getUserActiveGames(Long mbId);

    /**
     * 🆕 사용자의 승인 대기 게임들 조회
     */
    List<UserGameSummaryDto> getUserPendingGames(Long mbId);

    /**
     * 🆕 사용자의 상태별 게임 통계 조회
     */
    Map<Games.GameStatus, Long> getUserGameStatistics(Long mbId);

    /**
     * 🆕 사용자의 특정 상태 게임 개수 조회
     */
    long getUserGameCountByStatus(Long mbId, Games.GameStatus status);
}