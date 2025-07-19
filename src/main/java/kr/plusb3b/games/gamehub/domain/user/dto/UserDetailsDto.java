package kr.plusb3b.games.gamehub.domain.user.dto;

import kr.plusb3b.games.gamehub.domain.user.entity.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserDetailsDto {

    private User user;
    private UserAuth userAuth;
    private UserPrivate userPrivate;

}
