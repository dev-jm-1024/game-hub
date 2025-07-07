package kr.plusb3b.games.gamehub.domain.board.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdatePostsRequestDto {

    private Long postId;
    private String boardId;

    @NotBlank
    private String postTitle;
    @NotBlank
    private String postContent;
}
