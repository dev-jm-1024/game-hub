package kr.plusb3b.games.gamehub.application.admin;

import jakarta.servlet.http.HttpServletRequest;
import kr.plusb3b.games.gamehub.domain.admin.service.AdminUserService;
import kr.plusb3b.games.gamehub.domain.user.dto.UserDetailsDto;
import kr.plusb3b.games.gamehub.domain.user.entity.User;
import kr.plusb3b.games.gamehub.domain.user.service.UserProvider;
import kr.plusb3b.games.gamehub.security.AccessControlService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

@Service
public class AdminUserServiceImpl implements AdminUserService {

    private final AccessControlService access;
    private final UserProvider userProvider;

    public AdminUserServiceImpl(AccessControlService access, UserProvider userProvider) {
        this.access = access;
        this.userProvider = userProvider;
    }

    @Override
    public User validateAdminAccess(HttpServletRequest request) {
        User user = access.getAuthenticatedUser(request);
        if (user == null || user.getMbRole() != User.Role.ROLE_ADMIN) {
            throw new SecurityException("관리자 권한이 필요합니다");
        }
        return user;
    }

    @Override
    public Page<UserDetailsDto> searchUsers(String search, String status, String role, int page, int size, Model model) {
        if (search != null && !search.trim().isEmpty()) {
            // 닉네임 검색
            model.addAttribute("searchKeyword", search);
            return userProvider.searchUsersByNickname(search.trim(), page, size);
        } else if (status != null && !status.isEmpty()) {
            // 상태별 조회
            int statusValue = Integer.parseInt(status);
            model.addAttribute("selectedStatus", status);
            return userProvider.getUsersByStatus(statusValue, page, size);
        } else if (role != null && !role.isEmpty()) {
            // 역할별 조회
            User.Role roleEnum = User.Role.valueOf(role);
            model.addAttribute("selectedRole", role);
            return userProvider.getUsersByRole(roleEnum, page, size);
        } else {
            // 전체 조회
            return userProvider.getAllUsersDetails(page, size);
        }
    }

    @Override
    public void addPagingInfo(Model model, Page<UserDetailsDto> usersPage, int page, int size) {
        model.addAttribute("usersPage", usersPage);
        model.addAttribute("usersList", usersPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", usersPage.getTotalPages());
        model.addAttribute("totalElements", usersPage.getTotalElements());
        model.addAttribute("pageSize", size);
    }

    @Override
    public void addAdminInfo(Model model, User admin, AdminUserConfig config) {
        UserDetailsDto adminDetailsDto = userProvider.getUserDetails(admin);
        model.addAttribute("adminDetailsDto", adminDetailsDto);

        // 기존 방식 유지 (List)
        model.addAttribute("apiPaths", config.getAdminUserApiPaths());

        // 추가: Map 방식도 제공
        model.addAttribute("userPaths", config.getUserPathsMap());
    }

    @Override
    public void addConfigInfo(Model model, AdminUserConfig config) {
        model.addAttribute("adminConfig", config.getAllConfigs());
    }
}