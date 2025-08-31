package kr.plusb3b.games.gamehub.view.admin;

import kr.plusb3b.games.gamehub.domain.board.entity.Board;
import kr.plusb3b.games.gamehub.domain.board.entity.PostsReactionCount;

public record AdminSummaryPostVM(
        Board board,
        Long postId,
        String title,
        PostsReactionCount postsReactionCount
) {
}
