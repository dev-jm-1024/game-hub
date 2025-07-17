package kr.plusb3b.games.gamehub.domain.board.dto;


import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class RequestCommentDto {

    @NotNull
    private String commentContent;
    private String boardId;
    private Long postId;

    public RequestCommentDto() {}

}
