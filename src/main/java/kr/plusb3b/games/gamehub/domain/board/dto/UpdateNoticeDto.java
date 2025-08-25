package kr.plusb3b.games.gamehub.domain.board.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;
import java.util.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor // 기본 생성자 추가 필요
public class UpdateNoticeDto {

    private Long postId;
    private String postTitle;
    private String postContent;
    private Integer importantAct; // int → Integer (null 허용)

    // 기존 파일들 (제거되지 않은 파일들만 전송됨)
    private List<String> oldFileUrl;

    // 새로 추가할 파일들
    private List<MultipartFile> files;

}
