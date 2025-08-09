package kr.plusb3b.games.gamehub.domain.admin.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import kr.plusb3b.games.gamehub.domain.user.entity.User;

public class AdminAuth {

    @Id
    private String authAdminId;

    private String authPassword;
    private User.Role role = User.Role.ROLE_ADMIN;
}
