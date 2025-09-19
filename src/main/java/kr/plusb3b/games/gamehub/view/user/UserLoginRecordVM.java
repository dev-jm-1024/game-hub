package kr.plusb3b.games.gamehub.view.user;

import java.time.LocalDateTime;

public record UserLoginRecordVM(
        Long mbId,
        LocalDateTime loginTime,
        String ipAddress
) {
}
