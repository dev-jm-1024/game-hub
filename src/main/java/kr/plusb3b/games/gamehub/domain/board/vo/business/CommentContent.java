package kr.plusb3b.games.gamehub.domain.board.vo.business;

import jakarta.persistence.Embeddable;
import lombok.Getter;

@Embeddable
@Getter
public class CommentContent {

    private String commentContent;

    public CommentContent() {}

    private CommentContent(String commentContent) {
        this.commentContent = commentContent;
    }

    public static CommentContent of(String commentContent) {
        if(commentContent == null || commentContent.isEmpty()){
            throw new IllegalArgumentException("댓글 내용이 공백이어서는 안됩니다!");
        } else if (commentContent.length() > 100000) {
            throw new IllegalArgumentException("댓글 내용이 10만자 이상이면 안됩니다");

        }

        return new CommentContent(commentContent);
    }

    @Override
    public String toString() {
        return commentContent;
    }

}
