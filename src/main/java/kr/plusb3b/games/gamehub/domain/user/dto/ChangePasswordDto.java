package kr.plusb3b.games.gamehub.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ChangePasswordDto {

    private String oldPassword;
    private String newPassword;

}
