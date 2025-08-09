package kr.plusb3b.games.gamehub.upload;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Service
public class FileUpload {

    private final Cloudinary cloudinary;

    /**
     * 생성자: .env에서 환경 변수 로딩하여 Cloudinary 인스턴스 초기화
     * CLOUDINARY_URL 예: cloudinary://API_KEY:API_SECRET@CLOUD_NAME
     */
    public FileUpload() {
        Dotenv dotenv = Dotenv.load();
        this.cloudinary = new Cloudinary(dotenv.get("CLOUDINARY_URL"));
    }

    /**
     * 다중 파일 업로드 수행
     * - board-upload/ 폴더에 저장되도록 public_id 직접 설정
     * - 파일명 중복 방지를 위해 UUID를 suffix로 추가
     *
     * @param fileData MultipartFile 리스트
     * @return Map<파일 URL, MIME 타입>
     */
    public Map<String, String> getFileUrlAndType(List<MultipartFile> fileData) {
        Map<String, String> resultMap = new HashMap<>();

        // null 또는 빈 리스트인 경우 바로 리턴
        if (fileData == null || fileData.isEmpty()) {
            return resultMap;
        }

        for (MultipartFile file : fileData) {
            try {
                // 빈 파일 건너뛰기
                if (file.isEmpty()) {
                    continue;
                }

                // 원본 파일명 확인 및 기본 이름 설정
                String originalFilename = file.getOriginalFilename();
                String extension = "";

                if (originalFilename == null || originalFilename.isBlank()) {
                    originalFilename = "uploaded_file";
                } else {
                    int dotIndex = originalFilename.lastIndexOf(".");
                    if (dotIndex > -1) {
                        extension = originalFilename.substring(dotIndex); // .png, .jpg 등
                        originalFilename = originalFilename.substring(0, dotIndex); // 확장자 제외
                    }
                }

                // 파일명 중복 방지를 위한 UUID 추가
                String uniqueFileName = originalFilename + "_" + UUID.randomUUID() + extension;

                // public_id = board-upload/파일명_UUID.확장자
                String publicId = "board-upload/" + uniqueFileName;

                // 업로드 옵션 구성
                Map<String, Object> uploadOptions = ObjectUtils.asMap(
                        "upload_preset", "capstone_upload", // Unsigned preset 이름
                        "public_id", publicId,              // 폴더 포함 저장 경로
                        "resource_type", "auto"             // 자동 타입 감지 (image, video, raw)
                );

                // Cloudinary에 업로드 실행
                Map uploadResult = cloudinary.uploader().upload(file.getBytes(), uploadOptions);

                // 결과에서 URL과 파일 MIME 타입 추출
                String fileUrl = (String) uploadResult.get("secure_url");
                String fileType = file.getContentType();

                // 결과 저장
                resultMap.put(fileUrl, fileType);

            } catch (IOException e) {
                // 업로드 중 오류 발생 시 로깅
                System.err.println("[Cloudinary Upload Error] 파일 업로드 실패: " + file.getOriginalFilename());
                e.printStackTrace();
            }
        }

        return resultMap;
    }

    /**
     * 프로필 이미지 전용 업로드 메서드
     * - profile-image-folder preset 사용 (자동으로 profile-images/ 폴더에 저장됨)
     * - public_id는 preset에서 자동 생성 (파일명 기반 + unique suffix)
     *
     * @param profileFile 프로필 이미지 파일
     * @return 업로드된 프로필 이미지 URL
     */
    public String uploadProfileImage(MultipartFile profileFile) {
        if (profileFile == null || profileFile.isEmpty()) {
            throw new IllegalArgumentException("업로드할 프로필 이미지가 없습니다.");
        }

        try {
            // 이미지 파일 검증
            String contentType = profileFile.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                throw new IllegalArgumentException("프로필 사진은 이미지 파일만 업로드 가능합니다.");
            }

            // 파일 크기 제한 (5MB)
            long maxSize = 5 * 1024 * 1024;
            if (profileFile.getSize() > maxSize) {
                throw new IllegalArgumentException("프로필 사진 크기는 5MB를 초과할 수 없습니다.");
            }

            // 프로필 이미지 전용 preset 사용
            // preset에서 이미 profile-images/ 폴더와 public_id 자동생성이 설정되어 있음
            Map<String, Object> uploadOptions = ObjectUtils.asMap(
                    "upload_preset", "profile-image-folder", // 새로 만든 프로필 전용 preset
                    "resource_type", "image"
            );

            Map uploadResult = cloudinary.uploader().upload(profileFile.getBytes(), uploadOptions);
            String fileUrl = (String) uploadResult.get("secure_url");

            System.out.println("[Profile Image Upload Success] " + profileFile.getOriginalFilename() + " -> " + fileUrl);
            return fileUrl;

        } catch (IOException e) {
            System.err.println("[Profile Image Upload Error] 프로필 이미지 업로드 실패");
            e.printStackTrace();
            throw new RuntimeException("프로필 이미지 업로드 중 오류가 발생했습니다.", e);
        }
    }

    /**
     * 프로필 이미지 업로드 + URL transformation 적용
     * - 업로드 후 URL에 이미지 최적화 파라미터 추가
     *
     * @param profileFile 프로필 이미지 파일
     * @return 최적화된 프로필 이미지 URL (300x300, 품질 자동 조정)
     */
    public String uploadProfileImageWithOptimization(MultipartFile profileFile) {
        // 기본 업로드 먼저 수행
        String originalUrl = uploadProfileImage(profileFile);

        // URL에 transformation 파라미터 추가하여 이미지 최적화
        if (originalUrl.contains("/upload/")) {
            String optimizedUrl = originalUrl.replace(
                    "/upload/",
                    "/upload/w_300,h_300,c_fill,q_auto,f_auto/"
            );
            System.out.println("[Profile Image Optimization] " + originalUrl + " -> " + optimizedUrl);
            return optimizedUrl;
        }

        return originalUrl;
    }
}