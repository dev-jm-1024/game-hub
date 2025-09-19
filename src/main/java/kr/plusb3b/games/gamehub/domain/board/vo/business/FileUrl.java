package kr.plusb3b.games.gamehub.domain.board.vo.business;

import jakarta.persistence.Embeddable;
import lombok.Getter;

@Embeddable
@Getter
public class FileUrl {

    private String fileUrl;

    public FileUrl(){}

    private FileUrl(String fileUrl){
        this.fileUrl = fileUrl;
    }

    public static FileUrl of(String fileUrl){

        if(fileUrl == null || fileUrl.isBlank())
            throw new IllegalArgumentException("파일 URL이 공백입니다");

        return new FileUrl(fileUrl);
    }
}

