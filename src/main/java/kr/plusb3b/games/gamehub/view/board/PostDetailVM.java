package kr.plusb3b.games.gamehub.view.board;

import kr.plusb3b.games.gamehub.domain.board.entity.Comments;
import kr.plusb3b.games.gamehub.domain.board.entity.CommentsReactionCount;
import kr.plusb3b.games.gamehub.domain.board.entity.PostFiles;
import kr.plusb3b.games.gamehub.domain.board.entity.Posts;
import kr.plusb3b.games.gamehub.domain.board.entity.PostsReactionCount;
import kr.plusb3b.games.gamehub.domain.user.entity.UserCommentsReaction;
import kr.plusb3b.games.gamehub.domain.user.entity.UserPostsReaction;

import java.util.List;
import java.util.Map;

public record PostDetailVM(
        // 기본 게시물 정보
        Posts posts,

        // 첨부파일 관련
        List<PostFiles> postFilesList,
        boolean hasPostFile,

        // 댓글 관련
        List<Comments> commentsList,

        // 사용자 권한 관련
        boolean isAuthor,

        // 게시물 반응 관련
        UserPostsReaction.ReactionType reactType,
        PostsReactionCount postsReactionCount,
        boolean isUserReported,

        // 댓글 반응 관련 - 각 댓글별 정보를 담는 Map들
        Map<Long, CommentsReactionCount> commentReactionMap,
        Map<Long, UserCommentsReaction.ReactionType> userCommentReactionMap,
        Map<Long, Boolean> userCommentReportMap
) {
}