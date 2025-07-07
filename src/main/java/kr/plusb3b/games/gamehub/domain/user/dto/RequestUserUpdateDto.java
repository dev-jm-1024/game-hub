package kr.plusb3b.games.gamehub.domain.user.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Objects;

@Getter
@Setter
public class RequestUserUpdateDto {

    private String authUserId; //로그인 아이디
//    private String authPassword; //로그인 비밀번호
    private String priEmail; //이메일
    private LocalDate priBirth; //사용자 생일
    private String mbNickName; //사용자 닉네임
    private String mbStatusMessage; //사용자 상태메세지
    private String priGender; //사용자 성별
    private String mbProfileUrl; //사용자 프로필 사진 URL

    public RequestUserUpdateDto() {}

    public RequestUserUpdateDto(String authUserId, String priEmail, LocalDate priBirth, String mbNickName,
                                String mbStatusMessage, String priGender, String mbProfileUrl) {
        this.authUserId = authUserId;
        this.priEmail = priEmail;
        this.priBirth = priBirth;
        this.mbNickName = mbNickName;
        this.mbStatusMessage = mbStatusMessage;
        this.priGender = priGender;
        this.mbProfileUrl = mbProfileUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        RequestUserUpdateDto that = (RequestUserUpdateDto) o;
        return Objects.equals(authUserId, that.authUserId) && Objects.equals(priEmail, that.priEmail) && Objects.equals(priBirth, that.priBirth) && Objects.equals(mbNickName, that.mbNickName) && Objects.equals(mbStatusMessage, that.mbStatusMessage) && Objects.equals(priGender, that.priGender) && Objects.equals(mbProfileUrl, that.mbProfileUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(authUserId, priEmail, priBirth, mbNickName, mbStatusMessage, priGender, mbProfileUrl);
    }
}
