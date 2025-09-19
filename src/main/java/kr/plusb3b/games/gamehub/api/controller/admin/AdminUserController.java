package kr.plusb3b.games.gamehub.api.controller.admin;

import kr.plusb3b.games.gamehub.application.admin.AdminUserConfig;
import kr.plusb3b.games.gamehub.domain.admin.service.AdminUserService;
import kr.plusb3b.games.gamehub.domain.user.dto.UserDetailsDto;
import kr.plusb3b.games.gamehub.domain.user.entity.UserLoginInfo;
import kr.plusb3b.games.gamehub.domain.user.service.admin.AdminUserProvider;
import kr.plusb3b.games.gamehub.security.AccessControlService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin/user-status")
public class AdminUserController {

    private final AdminUserService adminUserService;
    private final AdminUserConfig adminUserConfig;
    private final AccessControlService access;
    private final AdminUserProvider adminUserProvider;

    public AdminUserController(AdminUserService adminUserService, AdminUserConfig adminUserConfig, AccessControlService access,
                               AdminUserProvider adminUserProvider) {
        this.adminUserService = adminUserService;
        this.adminUserConfig = adminUserConfig;
        this.access = access;
        this.adminUserProvider = adminUserProvider;
    }

    /**
     * 모든 요청에서 공통으로 사용하는 데이터
     */
//    @ModelAttribute
//    public void addCommonData(Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {
//
//        //access.validateAdminAccess(request, response);
//        User admin = adminUserService.validateAdminAccess(request);
//        adminUserService.addAdminInfo(model, admin, adminUserConfig);
//    }

    @GetMapping
    public String viewUserStatusPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String role,
            Model model) {

        // 파라미터 검증 및 보정
        int validatedPage = adminUserConfig.getValidatedPage(page);
        int validatedSize = adminUserConfig.getValidatedPageSize(size);

        // 검색어 검증 및 정제 (nickname을 search로 통합)
        String sanitizedSearch = null;
        if (search != null && adminUserConfig.isValidSearchTerm(search)) {
            sanitizedSearch = adminUserConfig.sanitizeSearchTerm(search);
        }

        // 사용자 목록 조회 (검색과 일반 조회 통합)
        Page<UserDetailsDto> usersPage = adminUserService.searchUsers(
                sanitizedSearch, status, role, validatedPage, validatedSize, model);

        // 페이징 정보 추가
        adminUserService.addPagingInfo(model, usersPage, validatedPage, validatedSize);

        // ✅ 검색 조건 유지를 위한 데이터 명시적 추가 (HTML 폼에서 사용)
        model.addAttribute("searchKeyword", sanitizedSearch);  // 원본 search가 아닌 정제된 값
        model.addAttribute("selectedStatus", status);
        model.addAttribute("selectedRole", role);

        // ✅ 사용자 목록 데이터 명시적 추가
        model.addAttribute("usersList", usersPage.getContent());

        // ✅ 페이징 관련 데이터 명시적 추가
        model.addAttribute("usersPage", usersPage);
        model.addAttribute("currentPage", validatedPage);
        model.addAttribute("totalPages", usersPage.getTotalPages());
        model.addAttribute("totalElements", usersPage.getTotalElements());
        model.addAttribute("pageSize", validatedSize);

        // 설정 정보 추가
        adminUserService.addConfigInfo(model, adminUserConfig);

        return "admin/user-status/index";
    }

    @GetMapping("/{mbId}/login-record")
    public String viewLoginRecordPage(Model model, @PathVariable("mbId") Long mbId){

        List<UserLoginInfo> vm =  adminUserProvider.getUserLoginInfo(mbId);
        model.addAttribute("vm", vm);
        return "admin/user-status/user-login-log";
    }

}