package kr.plusb3b.games.gamehub.domain.board.dto;

import kr.plusb3b.games.gamehub.domain.board.vo.business.PostTitle;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SummaryPostDto {

    private String boardId;
    private Long postId;
    private String author;
    private PostTitle postTitle;
    private LocalDate createdAt;
    //int viewCount

}
