package kr.plusb3b.games.gamehub.domain.user.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ChangePasswordDto {

    @NotNull(message = "기존 비밀번호를 입력하세요.")
    private String oldPassword;

    @NotNull(message = "현재 비밀번호를 입력하세요.")
    private String newPassword;

}
