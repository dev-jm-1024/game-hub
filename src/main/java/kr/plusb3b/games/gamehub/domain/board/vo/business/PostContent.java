package kr.plusb3b.games.gamehub.domain.board.vo.business;

import jakarta.persistence.Embeddable;
import lombok.Getter;

@Embeddable
@Getter
public class PostContent {

    private String postContent;

    public PostContent() {

    }

    private PostContent(String postContent) {
        this.postContent = postContent;
    }

    public static PostContent of(String postContent) {

        if(postContent == null || postContent.isBlank())
            throw new IllegalArgumentException("작성내용이 비어있으면 안됩니다!");

        return new PostContent(postContent);
    }

    @Override
    public String toString() {

        return postContent;
    }
}
