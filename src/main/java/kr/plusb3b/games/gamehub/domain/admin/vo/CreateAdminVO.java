package kr.plusb3b.games.gamehub.domain.admin.vo;


import kr.plusb3b.games.gamehub.domain.user.entity.User;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
public final class CreateAdminVO {

    //User 부분에 해당되는 필드
    private final String mbNickname = "";
    private final String mbProfileUrl = null;
    private final String mbStatusMessage = "";
    private final LocalDateTime mbJoinDate = LocalDateTime.now();
    private final User.Role mbRole = User.Role.ROLE_ADMIN;
    private final int mbReportCnt = 0;

    //UserPrivate 에 해당되는 필드
    private final String priEmail = "";
    private final LocalDate priBirth = null;
    private final String priGender = "";



}
