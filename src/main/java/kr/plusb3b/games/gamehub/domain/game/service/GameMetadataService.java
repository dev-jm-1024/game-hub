package kr.plusb3b.games.gamehub.domain.game.service;

import kr.plusb3b.games.gamehub.domain.board.entity.Board;
import kr.plusb3b.games.gamehub.domain.game.dto.GameUploadDto;
import kr.plusb3b.games.gamehub.domain.game.dto.GamesInfoDto;
import kr.plusb3b.games.gamehub.domain.game.entity.Games;
import kr.plusb3b.games.gamehub.domain.game.entity.GamesFile;
import kr.plusb3b.games.gamehub.domain.game.vo.GamesVO;
import kr.plusb3b.games.gamehub.domain.user.entity.User;

import java.util.Optional;

public interface GameMetadataService {

    //1. 게임 메타정보를 DB에 저장 (Games 엔티티)
    Games saveGameToDB(GameUploadDto dto, GamesVO gvo, User user, Board board);

    //2. 파일 정보를 DB에 저장 (GamesFile 엔티티)
    GamesFile saveGameFileToDB(Games game, GamesFile gamesFile);

    //3. 게임 정보 + 게임 파일 정보 --> GamesInfoDto
    //o 게임 메타 정보 DB에서 가져오기 --> Games
    //o 파일 정보 DB에서 가져오기 --> GamesFile

    //3.1 승인이 되지 않은 게임 항목들 가져오기
    Optional<GamesInfoDto> notApprovedGames();

    //3.2 승인이 된 게임 항목들 가져오기
    Optional<GamesInfoDto> approvedGames();

    //3.3 나머지 단계 항목들 가져오기
    Optional<GamesInfoDto> otherGames(Games.GameStatus status);

}
