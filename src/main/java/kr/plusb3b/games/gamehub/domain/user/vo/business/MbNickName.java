package kr.plusb3b.games.gamehub.domain.user.vo.business;

import jakarta.persistence.Embeddable;
import lombok.Getter;

@Embeddable
@Getter
public class MbNickName {

    private String mbNickName;

    public MbNickName() {
    }

    private MbNickName(String mbNickName) {
        this.mbNickName = mbNickName;
    }

    public static MbNickName of(String mbNickName){
        if(mbNickName == null || mbNickName.isBlank())
            throw new IllegalArgumentException("닉네임이 공백이어선 안됩니다");
        else if(mbNickName.length() > 20)
            throw new IllegalArgumentException("닉네임이 20글자를 넘어가선 안됩니다");

        return new MbNickName(mbNickName);
    }

    @Override
    public String toString() {
        return mbNickName;
    }
}
