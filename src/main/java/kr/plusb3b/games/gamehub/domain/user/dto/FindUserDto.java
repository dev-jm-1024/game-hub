package kr.plusb3b.games.gamehub.domain.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class FindUserDto {

    @NotBlank(message = "이름은 공백일 수 없습니다.")
    @Size(max = 20, message = "이름은 최대 20자까지 가능합니다.")
    public String name;

    @Email(message = "올바른 이메일 형식을 입력해주세요.")
    @Size(max = 70, message = "이메일은 최대 70자까지 가능합니다.")
    public String email;
}
