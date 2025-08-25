package kr.plusb3b.games.gamehub.application.admin;

import jakarta.servlet.http.HttpServletRequest;
import kr.plusb3b.games.gamehub.domain.admin.service.AdminService;
import kr.plusb3b.games.gamehub.domain.user.entity.User;
import kr.plusb3b.games.gamehub.domain.user.service.UserProvider;
import kr.plusb3b.games.gamehub.security.AccessControlService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class AdminServiceImpl implements AdminService {

    private final AccessControlService access;
    private final UserProvider userProvider;

    public AdminServiceImpl(AccessControlService access, UserProvider userProvider) {
        this.access = access;
        this.userProvider = userProvider;
    }

    @Override
    public String checkAdminOrRedirect(HttpServletRequest request, String redirectPath) {
        User user = access.getAuthenticatedUser(request);

        if (user == null) {
            // 로그인 안 한 경우 → 로그인 페이지로 리다이렉트
            return "redirect:/game-hub/login";
        } else if (user.getMbRole() != User.Role.ROLE_ADMIN) {
            // 로그인은 했지만 권한 없음 → 지정된 redirectPath로 이동
            return redirectPath;
        }

        // 관리자라면 null 반환 (리다이렉트 필요 없음)
        return null;
    }





    @Override
    public HttpStatus checkAdminOrReturnStatus(HttpServletRequest request) {
        User user = access.getAuthenticatedUser(request);
        if(user == null || user.getMbRole() != User.Role.ROLE_ADMIN) {
            return HttpStatus.FORBIDDEN;
        }

        return HttpStatus.OK;
    }

}
