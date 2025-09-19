package kr.plusb3b.games.gamehub.domain.game.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GameUploadDto {

    @NotBlank(message = "게임 이름은 필수입니다")
    @Size(max = 200, message = "게임 이름은 200자를 초과할 수 없습니다")
    private String gameName;

    @NotBlank(message = "게임 설명은 필수입니다")
    @Size(max = 1000, message = "게임 설명은 1000자를 초과할 수 없습니다")
    private String gameDescription;

    @NotBlank(message = "팀 이름은 필수입니다")
    @Size(max = 100, message = "팀 이름은 100자를 초과할 수 없습니다")
    private String teamName;

    @Size(max = 50, message = "게임 버전은 50자를 초과할 수 없습니다")
    private String gameVersion;

    @Size(max = 500, message = "권장사양은 500자를 초과할 수 없습니다")
    private String specs;

    @NotBlank(message = "장르 선택은 필수입니다")
    private String genre;

    @NotBlank(message = "플랫폼 선택은 필수입니다")
    private String platform;

    // 파일 관련 필드들 (MultipartFile은 Controller 에서 별도 처리)
    private MultipartFile gameFile;
}