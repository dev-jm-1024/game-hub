package kr.plusb3b.games.gamehub.domain.game.service;

import kr.plusb3b.games.gamehub.domain.game.entity.Games;
import kr.plusb3b.games.gamehub.domain.game.entity.GamesFile;

public interface GameMetadataUpdateService {

    /**
     * 게임 상태 업데이트
     */
    int updateGameToDB(Long gameId, Games.GameStatus status);

    /**
     * 게임 파일 상태 업데이트
     */
    int updateGameFileToDB(Long fileId, GamesFile.FileStatus status);

    /**
     * 게임 노출 상태 토글
     */
    int toggleGameVisibility(Long gameId);
}