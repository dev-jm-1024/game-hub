package kr.plusb3b.games.gamehub.domain.board.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
public class PostRequestDto {

    //유효성 검사
    @NotBlank(message="제목을 입력해야합니다.")
    private String postTitle;

    //유효성 검사
    @NotBlank(message="내용을 입력하세요.")
    private String postContent;

    private List<MultipartFile> files;

    public PostRequestDto(){}

    public PostRequestDto(String postTitle, String postContent, List<MultipartFile> files) {
        this.postTitle = postTitle;
        this.postContent = postContent;
        this.files = files;
    }
}
