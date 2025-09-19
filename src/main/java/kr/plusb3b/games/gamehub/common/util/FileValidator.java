package kr.plusb3b.games.gamehub.common.util;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class FileValidator {
    public static void validateImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("업로드할 파일이 없습니다.");
        }
        if (!file.getContentType().startsWith("image/")) {
            throw new IllegalArgumentException("이미지 파일만 업로드 가능합니다.");
        }
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new IllegalArgumentException("파일 크기는 5MB 이하만 허용됩니다.");
        }
    }

    public static void validateGameFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("업로드할 파일이 없습니다.");
        }
        if (!file.getContentType().startsWith("zip/")) {
            throw new IllegalArgumentException("압축 파일만 업로드 가능합니다.");
        }
//        if (file.getSize() > 5 * 1024 * 1024) {
//            throw new IllegalArgumentException("파일 크기는 5MB 이하만 허용됩니다.");
//        }
    }
}
