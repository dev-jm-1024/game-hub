package kr.plusb3b.games.gamehub.domain.admin.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;

public interface AdminService {

    String checkAdminOrRedirect(HttpServletRequest request, String redirectPath);

    HttpStatus checkAdminOrReturnStatus(HttpServletRequest request);
}
