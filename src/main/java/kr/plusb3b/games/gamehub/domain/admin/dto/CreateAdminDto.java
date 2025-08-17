package kr.plusb3b.games.gamehub.domain.admin.dto;

import lombok.Data;

@Data
public class CreateAdminDto {

    private String authUserId;
    private String authUserPassword;
}
