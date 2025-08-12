package kr.plusb3b.games.gamehub.application.googleCloud;

import kr.plusb3b.games.gamehub.domain.game.entity.GamesFile;
import kr.plusb3b.games.gamehub.domain.game.exception.GameUploadException;
import kr.plusb3b.games.gamehub.upload.googleCloud.CloudUploadService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.google.cloud.storage.*;

// 압축해제를 위한 추가 import
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Service
@Slf4j
public class CloudUploadServiceImpl implements CloudUploadService {

    private final Storage storage;
    private final String bucketName;
    private final String tempGamePath;
    private final String activateGamePath;
    private final String deactivateGamePath;

    public CloudUploadServiceImpl(Storage storage,
                                  @Value("${spring.cloud.gcp.storage.bucket}") String bucketName,
                                  @Value("${app.temp.game.path}") String tempGamePath,
                                  @Value("${app.activate.game.path}") String activateGamePath,
                                  @Value("${app.deactivate.game.path}") String deactivateGamePath) {
        this.storage = storage;
        this.bucketName = bucketName;
        this.tempGamePath = tempGamePath;
        this.activateGamePath = activateGamePath;
        this.deactivateGamePath = deactivateGamePath;
    }

    @Override
    public GamesFile uploadFileToGCP(MultipartFile file) {
        try {
            // 1. 파일 유효성 검증
            if (file == null || file.isEmpty()) {
                throw new GameUploadException("업로드할 파일이 없습니다.");
            }

            // 2. 고유 파일명 생성
            String originalFileName = file.getOriginalFilename();
            String extension = "";
            if (originalFileName != null && originalFileName.contains(".")) {
                extension = originalFileName.substring(originalFileName.lastIndexOf("."));
            }

            String uniqueFileName = System.currentTimeMillis() + "_" +
                    UUID.randomUUID().toString().substring(0, 8) + extension;

            // 3. GCS 업로드 경로 설정 (이중 슬래시 방지)
            String cleanTempPath = tempGamePath.startsWith("/") ? tempGamePath.substring(1) : tempGamePath;
            String objectName = cleanTempPath + "/" + uniqueFileName;

            // 4. 업로드된 파일의 공개 URL 미리 생성
            String gameUrl = String.format("https://storage.googleapis.com/%s/%s",
                    bucketName, objectName);

            log.info("GCS 업로드 시작 - 파일: {}, 대상 URL: {}", uniqueFileName, gameUrl);

            // 5. GCS에 파일 업로드
            BlobId blobId = BlobId.of(bucketName, objectName);
            BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                    .setContentType(file.getContentType())
                    .build();

            // 실제 업로드 수행
            Blob uploadedBlob = storage.create(blobInfo, file.getBytes());

            if (uploadedBlob != null) {
                log.info("✅ GCS 업로드 성공 - 파일: {}, 크기: {} bytes",
                        uniqueFileName, file.getSize());
            } else {
                throw new GameUploadException("파일 업로드가 완료되지 않았습니다.");
            }

            // 6. GamesFile 객체 생성
            GamesFile gamesFile = new GamesFile();
            gamesFile.setGameHash(generateFileHash(file));
            gamesFile.setOriginalFilename(originalFileName);
            gamesFile.setGameUrl(gameUrl);
            gamesFile.setFileSize(file.getSize());
            gamesFile.setUploadedAt(LocalDateTime.now());
            gamesFile.setFileStatus(GamesFile.FileStatus.TEMP);

            return gamesFile;

        } catch (Exception e) {
            log.error("❌ GCS 업로드 실패: {}", e.getMessage(), e);
            throw new GameUploadException("파일 업로드 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }

    @Override
    public List<GamesFile> uploadFileToGCP(List<MultipartFile> files) {
        return files.stream()
                .map(this::uploadFileToGCP)
                .collect(Collectors.toList());
    }

    @Override
    public String moveFileToActivateFolder(String gameId, String currentUrl) {
        try {
            // 1. 현재 파일 다운로드
            String currentObjectName = extractObjectNameFromUrl(currentUrl);
            BlobId sourceBlobId = BlobId.of(bucketName, currentObjectName);

            // GCS에서 압축 파일 다운로드
            Blob sourceBlob = storage.get(sourceBlobId);
            if (sourceBlob == null) {
                throw new GameUploadException("원본 파일을 찾을 수 없습니다: " + currentUrl);
            }

            byte[] compressedData = sourceBlob.getContent();
            log.info("압축 파일 다운로드 완료: {} bytes", compressedData.length);

            // 2. 압축 해제
            String activeFolderPath = activateGamePath + "/" + gameId + "/";
            List<String> uploadedFiles = extractAndUploadFiles(compressedData, activeFolderPath);

            if (uploadedFiles.isEmpty()) {
                throw new GameUploadException("압축 해제된 파일이 없습니다.");
            }

            // 3. 원본 압축 파일 삭제
            storage.delete(sourceBlobId);

            // 4. 메인 HTML 파일 URL 반환 (index.html 우선)
            String mainFileUrl = findMainGameFile(uploadedFiles);

            log.info("파일 압축해제 및 이동 완료: {}개 파일 → {}", uploadedFiles.size(), mainFileUrl);
            return mainFileUrl;

        } catch (Exception e) {
            log.error("파일 압축해제 및 이동 실패: {}", e.getMessage(), e);
            throw new GameUploadException("파일 압축해제 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }

    @Override
    public String moveFileToDeactivateFolder(String gameId, String currentUrl) {
        try {
            String currentObjectName = extractObjectNameFromUrl(currentUrl);
            String fileName = currentObjectName.substring(currentObjectName.lastIndexOf("/") + 1);
            String newObjectName = deactivateGamePath + "/" + gameId + "/" + fileName;

            // 동일한 이동 로직
            BlobId sourceBlobId = BlobId.of(bucketName, currentObjectName);
            BlobId targetBlobId = BlobId.of(bucketName, newObjectName);

            storage.copy(Storage.CopyRequest.of(sourceBlobId, targetBlobId));
            storage.delete(sourceBlobId);

            String newUrl = String.format("https://storage.googleapis.com/%s/%s", bucketName, newObjectName);

            log.info("파일 비활성화 이동 완료: {} -> {}", currentUrl, newUrl);
            return newUrl;

        } catch (Exception e) {
            log.error("파일 비활성화 이동 실패: {}", e.getMessage(), e);
            throw new GameUploadException("파일 비활성화 이동 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }

    private List<String> extractAndUploadFiles(byte[] compressedData, String targetPath) throws IOException {
        List<String> uploadedFiles = new ArrayList<>();

        try (ByteArrayInputStream bis = new ByteArrayInputStream(compressedData);
             ZipInputStream zis = new ZipInputStream(bis)) {

            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {


                // 디렉토리는 건너뛰기
                if (entry.isDirectory()) {
                    continue;
                }

                String fileName = entry.getName();

                // 불필요한 파일 필터링 (__MACOSX, .DS_Store 등)
                if (fileName.contains("__MACOSX") || fileName.contains(".DS_Store") ||
                        fileName.startsWith(".") || fileName.contains("..")) {
                    log.warn("불필요한 파일 건너뛰기: {}", fileName);
                    continue;
                }

                // 파일 내용 읽기
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int len;
                while ((len = zis.read(buffer)) > 0) {
                    baos.write(buffer, 0, len);
                }
                byte[] fileData = baos.toByteArray();

                // GCS에 업로드할 경로 생성
                String objectName = targetPath + fileName;

                // MIME 타입 결정
                String mimeType = determineMimeType(fileName);

                // GCS에 업로드
                BlobId blobId = BlobId.of(bucketName, objectName);
                BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                        .setContentType(mimeType)
                        .build();

                storage.create(blobInfo, fileData);

                // 업로드된 파일 URL 생성
                String fileUrl = String.format("https://storage.googleapis.com/%s/%s", bucketName, objectName);
                uploadedFiles.add(fileUrl);

                log.info("파일 업로드 완료: {} ({} bytes)", fileName, fileData.length);
            }
        }

        return uploadedFiles;
    }

    private String determineMimeType(String fileName) {
        String lowerName = fileName.toLowerCase();

        if (lowerName.endsWith(".html") || lowerName.endsWith(".htm")) {
            return "text/html";
        } else if (lowerName.endsWith(".js")) {
            return "application/javascript";
        } else if (lowerName.endsWith(".css")) {
            return "text/css";
        } else if (lowerName.endsWith(".png")) {
            return "image/png";
        } else if (lowerName.endsWith(".jpg") || lowerName.endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (lowerName.endsWith(".gif")) {
            return "image/gif";
        } else if (lowerName.endsWith(".json")) {
            return "application/json";
        } else if (lowerName.endsWith(".wasm")) {
            return "application/wasm";
        } else {
            return "application/octet-stream";
        }
    }

    private String findMainGameFile(List<String> uploadedFiles) {
        // 1. index.html 우선 검색
        for (String url : uploadedFiles) {
            if (url.toLowerCase().contains("index.html")) {
                return url;
            }
        }

        // 2. main.html 검색
        for (String url : uploadedFiles) {
            if (url.toLowerCase().contains("main.html")) {
                return url;
            }
        }

        // 3. 게임명이 포함된 html 파일 검색
        for (String url : uploadedFiles) {
            if (url.toLowerCase().endsWith(".html")) {
                return url;
            }
        }

        // 4. 첫 번째 파일 반환
        return uploadedFiles.get(0);
    }

    /**
     * 파일의 SHA-256 해시값 생성
     * @param file 해시를 생성할 파일
     * @return 20자리 해시 문자열
     */
    private String generateFileHash(MultipartFile file) {
        try {
            byte[] fileBytes = file.getBytes();
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(fileBytes);

            StringBuilder hashString = new StringBuilder();
            for (byte b : hashBytes) {
                hashString.append(String.format("%02x", b));
            }

            String fullHash = hashString.toString();
            String shortHash = fullHash.substring(0, Math.min(20, fullHash.length()));

            log.debug("파일 해시 생성 완료: {} -> {}", file.getOriginalFilename(), shortHash);
            return shortHash;

        } catch (Exception e) {
            log.warn("파일 해시 생성 실패, 대체 해시 사용: {}", e.getMessage());
            String fallback = (file.getOriginalFilename() + System.currentTimeMillis()).hashCode() + "";
            return fallback.substring(0, Math.min(20, fallback.length()));
        }
    }

    private String extractObjectNameFromUrl(String url) {
        // https://storage.googleapis.com/bucket-name/object-name에서 object-name 추출
        String prefix = "https://storage.googleapis.com/" + bucketName + "/";
        return url.replace(prefix, "");
    }
}