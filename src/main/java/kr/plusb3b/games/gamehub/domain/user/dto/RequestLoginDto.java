package kr.plusb3b.games.gamehub.domain.user.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RequestLoginDto {

    @NotNull
    String authUserId;

    @NotNull
    String authUserPassword;
}
