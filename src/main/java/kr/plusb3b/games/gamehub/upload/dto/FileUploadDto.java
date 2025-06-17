package kr.plusb3b.games.gamehub.upload.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FileUploadDto {

    private String fileType;
    private String fileURL;

    public FileUploadDto() {}

    public FileUploadDto(String fileType, String fileURL) {
        this.fileType = fileType;
        this.fileURL = fileURL;
    }
}
