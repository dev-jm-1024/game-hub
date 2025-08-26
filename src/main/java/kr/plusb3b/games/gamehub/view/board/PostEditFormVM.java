package kr.plusb3b.games.gamehub.view.board;

import kr.plusb3b.games.gamehub.domain.board.entity.PostFiles;
import kr.plusb3b.games.gamehub.domain.board.entity.Posts;

import java.util.List;

public record PostEditFormVM(
        Posts posts,
        List<PostFiles> postFilesList,
        String boardId,
        boolean isAuthor,
        boolean hasPostFile
) {
}
