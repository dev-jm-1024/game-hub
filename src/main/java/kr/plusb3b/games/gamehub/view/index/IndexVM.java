package kr.plusb3b.games.gamehub.view.index;

import kr.plusb3b.games.gamehub.domain.board.entity.Board;

import java.util.List;

//일단 보류. 메인 페이지에 뭐 보여줘야할 지 생각 좀
public record IndexVM(

        String replaceProfileUrl,

        boolean isAdmin,

        boolean isLoggedIn,
        String mbNickName,
        String mbProfileUrl,

        List<Board> boardList


) {
}
