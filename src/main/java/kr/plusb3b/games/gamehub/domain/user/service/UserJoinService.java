package kr.plusb3b.games.gamehub.domain.user.service;

import kr.plusb3b.games.gamehub.domain.user.entity.User;
import kr.plusb3b.games.gamehub.domain.user.dto.UserSignupDto;
import kr.plusb3b.games.gamehub.domain.user.vo.UserSignupVO;

public interface UserJoinService {

    //객체 조립 후 저장
    //사용자 입력 데이터 + 추가 데이터
    void signupUser(UserSignupDto userSignupDto, UserSignupVO usv);

    //비로그인 상태인지 검사
    boolean isLogin(User user);

    //아이디 중복 검사
    boolean checkDistinctLoginId(String loginId);

    //이메일 중복 검사
    boolean checkDistinctEmail(String email);
}
