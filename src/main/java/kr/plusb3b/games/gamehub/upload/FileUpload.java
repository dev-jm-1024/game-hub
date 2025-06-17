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

        for (MultipartFile file : fileData) {
            try {
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
                System.err.println("[Cloudinary Upload Error] 파일 업로드 실패");
                e.printStackTrace();
            }
        }

        return resultMap;
    }
}
