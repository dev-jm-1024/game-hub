package kr.plusb3b.games.gamehub.domain.board.dto;

import kr.plusb3b.games.gamehub.domain.board.entity.Posts;

import java.time.LocalDate;

public record NoticeUpdateResponse(
        Long postId,
        String title,
        String content,
        boolean important,
        LocalDate updatedAt
) {
    public static NoticeUpdateResponse of(Posts post) {
        return new NoticeUpdateResponse(
                post.getPostId(),
                post.getPostTitle().toString(),
                post.getPostContent().toString(),
                post.isImportant(),
                post.getUpdatedAt()
        );
    }
}

