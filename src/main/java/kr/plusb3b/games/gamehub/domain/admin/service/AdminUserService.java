package kr.plusb3b.games.gamehub.domain.admin.service;

import jakarta.servlet.http.HttpServletRequest;
import kr.plusb3b.games.gamehub.application.admin.AdminUserConfig;
import kr.plusb3b.games.gamehub.domain.user.dto.UserDetailsDto;
import kr.plusb3b.games.gamehub.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.ui.Model;

public interface AdminUserService {

    /**
     * 관리자 권한 검증
     * @param request HTTP 요청
     * @return 검증된 관리자 User 객체
     * @throws SecurityException 관리자 권한이 없을 경우
     */
    User validateAdminAccess(HttpServletRequest request);

    /**
     * 검색 조건에 따른 사용자 목록 조회
     * @param search 검색어 (닉네임)
     * @param status 상태 필터
     * @param role 역할 필터
     * @param page 페이지 번호
     * @param size 페이지 크기
     * @param model Model 객체 (검색 조건 저장용)
     * @return 사용자 목록 페이지
     */
    Page<UserDetailsDto> searchUsers(String search, String status, String role, int page, int size, Model model);

    /**
     * 페이징 정보를 Model에 추가
     * @param model Model 객체
     * @param usersPage 사용자 페이지 객체
     * @param page 현재 페이지 번호
     * @param size 페이지 크기
     */
    void addPagingInfo(Model model, Page<UserDetailsDto> usersPage, int page, int size);

    /**
     * 관리자 정보와 경로 정보를 Model에 추가
     * @param model Model 객체
     * @param admin 관리자 User 객체
     * @param config AdminUserConfig 설정 객체
     */
    void addAdminInfo(Model model, User admin, AdminUserConfig config);

    /**
     * 전체 설정 정보를 Model에 추가
     * @param model Model 객체
     * @param config AdminUserConfig 설정 객체
     */
    void addConfigInfo(Model model, AdminUserConfig config);
}