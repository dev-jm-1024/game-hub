package kr.plusb3b.games.gamehub.domain.game.dto;

import kr.plusb3b.games.gamehub.domain.game.entity.Games;
import kr.plusb3b.games.gamehub.domain.game.entity.GamesFile;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
public class GamesInfoDto {

    private List<Games> gamesList;
    private List<GamesFile> gamesFiles;

    public GamesInfoDto() {}

    public GamesInfoDto(List<Games> gamesList, List<GamesFile> gamesFiles) {
        this.gamesList = gamesList;
        this.gamesFiles = gamesFiles;
    }
}
