package kr.plusb3b.games.gamehub.domain.board.dto;

import lombok.Getter;

import java.time.LocalDate;

@Getter
public class SummaryPostDto {

    private String boardId;
    private Long postId;
    private String author;
    private String title;
    private LocalDate createdAt;
    //int viewCount

    public SummaryPostDto() {}

    public SummaryPostDto(String boardId, Long postId, String author,
                          String title, LocalDate createdAt) {
        this.boardId = boardId;
        this.postId = postId;
        this.author = author;
        this.title = title;
        this.createdAt = createdAt;
    }

    /*

       public SummaryPostDto(String author, String title, LocalDate createdAt, int viewCount) {
        this.author = author;
        this.title = title;
        this.createdAt = createdAt;
        this.viewCount = viewCount;
    }

     */
}
