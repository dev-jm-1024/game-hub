package kr.plusb3b.games.gamehub.application.game;

import kr.plusb3b.games.gamehub.domain.game.service.GameUploadValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Service
@Slf4j
public class GameUploadValidatorImpl implements GameUploadValidator {

    @Override
    public boolean isIndexHtml(MultipartFile file) {
        try (ZipInputStream zis = new ZipInputStream(file.getInputStream())) {
            ZipEntry entry;

            // zip 파일 내부를 순회하면서 index.html 찾기
            while ((entry = zis.getNextEntry()) != null) {
                String fileName = entry.getName();

                // index.html 파일 찾으면 true 반환
                if ("index.html".equals(fileName) || fileName.endsWith("/index.html")) {
                    log.info("index.html 파일 확인됨: {}", fileName);
                    return true;
                }
                zis.closeEntry();
            }

            log.warn("index.html 파일이 발견되지 않았습니다.");
            return false;

        } catch (IOException e) {
            log.error("zip 파일 구조 검증 실패: {}", e.getMessage());
            return false; // 손상된 zip 파일이거나 읽기 실패
        }
    }

    @Override
    public boolean isValidGameZip(MultipartFile file) {
        return false;
    }
}
