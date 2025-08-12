package kr.plusb3b.games.gamehub.domain.game.dto;

import kr.plusb3b.games.gamehub.domain.game.entity.GamesFile;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateGamesFileDto {

    //GamesFiles 에서 업데이트 할 것
    //gameUrl, fileStatus,

    public Long fileId;
    public String gameUrl;
    public GamesFile.FileStatus fileStatus;
}
