package kr.plusb3b.games.gamehub.view.admin;

import kr.plusb3b.games.gamehub.domain.board.entity.Board;
import kr.plusb3b.games.gamehub.domain.board.entity.PostsReactionCount;
import kr.plusb3b.games.gamehub.domain.board.vo.business.PostTitle;

public record AdminSummaryPostVM(
        Board board,
        Long postId,
        PostTitle title,
        PostsReactionCount postsReactionCount
) {
}
