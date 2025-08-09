package kr.plusb3b.games.gamehub.domain.game.service;

import org.springframework.web.multipart.MultipartFile;

public interface GameUploadValidator {

    boolean isIndexHtml(MultipartFile zipFile);
    boolean isValidGameZip(MultipartFile file);
    // 추후: 용량 체크, 확장자 검사 등도 여기로!
}

