package kr.plusb3b.games.gamehub.api.dto.board;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreatePostsRequestDto
{

    private String boardId;

    //유효성 검사
    @NotBlank(message="제목을 입력해야합니다.")
    private String postTitle;

    //유효성 검사
    @NotBlank(message="내용을 입력하세요.")
    private String postContent;

    public CreatePostsRequestDto(){}

    public CreatePostsRequestDto(String post_title, String post_content) {
        this.postTitle = post_title;
        this.postContent = post_content;
    }


}
