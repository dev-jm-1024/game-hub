package kr.plusb3b.games.gamehub.view.board;

import kr.plusb3b.games.gamehub.domain.game.dto.GameDetailDto;
import kr.plusb3b.games.gamehub.domain.user.entity.User;

public record PostGamesDetailVM(

        User user,
        boolean isAdmin,

        String mbNickName,
        String mbProfileUrl,
        String replaceProfileUrl,

        GameDetailDto gameDetailDto

) {
}
