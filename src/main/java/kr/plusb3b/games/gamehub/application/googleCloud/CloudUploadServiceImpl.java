package kr.plusb3b.games.gamehub.application.googleCloud;

import kr.plusb3b.games.gamehub.domain.game.entity.GamesFile;
import kr.plusb3b.games.gamehub.domain.game.exception.GameUploadException;
import kr.plusb3b.games.gamehub.upload.googleCloud.CloudUploadService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.google.cloud.storage.*;

import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;
import java.util.List;
import java.util.stream.Collectors;

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

            // 4. 업로드된 파일의 공개 URL 미리 생성 ✅
            String gameUrl = String.format("https://storage.googleapis.com/%s/%s",
                    bucketName, objectName);

            log.info("GCS 업로드 시작 - 파일: {}, 대상 URL: {}", uniqueFileName, gameUrl);

            // 5. GCS에 파일 업로드
            BlobId blobId = BlobId.of(bucketName, objectName);
            BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                    .setContentType(file.getContentType())
                    // ACL 설정은 버킷 레벨에서 처리하므로 제거
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
        // 파일 이동 로직 구현
        // temp-game/파일명 → activate-game/gameId/압축해제된파일들
        return null; // 구현 필요
    }

    @Override
    public String moveFileToDeactivateFolder(String gameId, String currentUrl) {
        // 파일 이동 로직 구현
        return null; // 구현 필요
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
}