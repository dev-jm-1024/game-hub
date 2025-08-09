package kr.plusb3b.games.gamehub.upload.googleCloud;

import kr.plusb3b.games.gamehub.domain.game.entity.GamesFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CloudUploadService {

    // 단일 파일 업로드: temp-game
    GamesFile uploadFileToGCP(MultipartFile file);

    // 여러 파일 업로드: temp-game
    List<GamesFile> uploadFileToGCP(List<MultipartFile> files);

    // 게임 파일 승인 후 activate-game 폴더로 이동
    String moveFileToActivateFolder(String gameId, String currentUrl);

    // 게임 파일 비활성화 (deactivate-game 폴더로 이동)
    String moveFileToDeactivateFolder(String gameId, String currentUrl);
}