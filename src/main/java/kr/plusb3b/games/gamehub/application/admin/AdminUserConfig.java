package kr.plusb3b.games.gamehub.application.admin;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@Getter
public class AdminUserConfig {

    // 경로 설정
    @Value("${admin.api.path}")
    private String adminApiPath;

    @Value("${admin.user.activate}")
    private String userActivatePath;

    @Value("${admin.user.deactivate}")
    private String userDeactivatePath;

    @Value("${admin.user.login-history}")
    private String userLoginHistoryPath;

    @Value("${admin.user.activity}")
    private String userActivityPath;

    // 페이징 설정
    @Value("${admin.user.page.default-size:10}")
    private int defaultPageSize;

    @Value("${admin.user.page.max-size:100}")
    private int maxPageSize;

    @Value("${admin.user.page.default-page:0}")
    private int defaultPage;

    // 검색 설정
    @Value("${admin.user.search.min-length:2}")
    private int searchMinLength;

    @Value("${admin.user.search.max-length:50}")
    private int searchMaxLength;

    // 기존 List 방식 (호환성 유지)
    public List<String> getAdminUserApiPaths() {
        return Arrays.asList(
                adminApiPath + userActivatePath,
                adminApiPath + userDeactivatePath,
                adminApiPath + userLoginHistoryPath,
                adminApiPath + userActivityPath
        );
    }

    // Map 방식 (더 명확함)
    public Map<String, String> getUserPathsMap() {
        Map<String, String> paths = new HashMap<>();
        paths.put("activate", adminApiPath + userActivatePath);
        paths.put("deactivate", adminApiPath + userDeactivatePath);
        paths.put("loginHistory", adminApiPath + userLoginHistoryPath);
        paths.put("activity", adminApiPath + userActivityPath);
        return paths;
    }

    // 페이징 설정 메서드들
    public int getValidatedPageSize(int requestedSize) {
        if (requestedSize <= 0) return defaultPageSize;
        return Math.min(requestedSize, maxPageSize);
    }

    public int getValidatedPage(int requestedPage) {
        return Math.max(requestedPage, defaultPage);
    }

    public Map<String, Object> getPagingConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put("defaultSize", defaultPageSize);
        config.put("maxSize", maxPageSize);
        config.put("defaultPage", defaultPage);
        return config;
    }

    // 검색 설정 메서드들
    public boolean isValidSearchTerm(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return false;
        }
        String trimmed = searchTerm.trim();
        return trimmed.length() >= searchMinLength && trimmed.length() <= searchMaxLength;
    }

    public String sanitizeSearchTerm(String searchTerm) {
        if (searchTerm == null) return "";
        return searchTerm.trim().replaceAll("[<>\"'&]", ""); // XSS 방지
    }

    public Map<String, Object> getSearchConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put("minLength", searchMinLength);
        config.put("maxLength", searchMaxLength);
        return config;
    }

    // 전체 설정을 한 번에 가져오기
    public Map<String, Object> getAllConfigs() {
        Map<String, Object> allConfigs = new HashMap<>();
        allConfigs.put("paths", getUserPathsMap());
        allConfigs.put("paging", getPagingConfig());
        allConfigs.put("search", getSearchConfig());
        return allConfigs;
    }

}
