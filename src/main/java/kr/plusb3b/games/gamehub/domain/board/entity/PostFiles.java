package kr.plusb3b.games.gamehub.domain.board.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@Table(name="post_files")
public class PostFiles {

    //파일 고유 아이디
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long fileId;

    //파일이 속한 게시글 아이디 : 외래키
    @ManyToOne
    @JoinColumn(name="post_id")
    private Posts post;

    //파일경로 또는 이름
    private String fileUrl;

    //파일 유형
    private String fileType;

    //업로드 날짜
    private LocalDate uploadDate;

    //여분컬럼1~10
    /*
    private String file_extra1;
    private String file_extra2;
    private String file_extra3;
    private String file_extra4;
    private String file_extra5;
    private String file_extra6;
    private String file_extra7;
    private String file_extra8;
    private String file_extra9;
    private String file_extra10;
    */
    public PostFiles() {}

}
