package kr.plusb3b.games.gamehub.domain.user.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
public class LoginCheckDto {

    private String authUserId;
    private String authUserPassword;

    public LoginCheckDto() {}

    public LoginCheckDto(String authUserId, String authUserPassword) {
        this.authUserId = authUserId;
        this.authUserPassword = authUserPassword;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LoginCheckDto that = (LoginCheckDto) o;
        return Objects.equals(authUserId, that.authUserId) && Objects.equals(authUserPassword, that.authUserPassword);
    }

    @Override
    public int hashCode() {
        return Objects.hash(authUserId, authUserPassword);
    }
}
