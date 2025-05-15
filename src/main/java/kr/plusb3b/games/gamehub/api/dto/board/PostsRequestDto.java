package kr.plusb3b.games.gamehub.api.dto.board;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
public class PostsRequestDto
{
    //유효성 검사
    @NotBlank(message="제목을 입력해야합니다.")
    private String post_title;

    //유효성 검사
    @NotBlank(message="내용을 입력하세요.")
    private String post_content;

    public PostsRequestDto(){}

    public PostsRequestDto(String post_title, String post_content) {
        this.post_title = post_title;
        this.post_content = post_content;
    }


}
