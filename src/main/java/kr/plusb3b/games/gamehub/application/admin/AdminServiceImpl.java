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

        if(user == null) return redirectPath;
        else if(user.getMbRole() != User.Role.ROLE_ADMIN) return redirectPath;

        return redirectPath; // 관리자가 아니면 리다이렉트 경로 반환
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
