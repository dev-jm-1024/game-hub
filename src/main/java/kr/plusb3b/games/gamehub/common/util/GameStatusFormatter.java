package kr.plusb3b.games.gamehub.common.util;

import kr.plusb3b.games.gamehub.domain.game.entity.Games;
import kr.plusb3b.games.gamehub.domain.game.entity.GamesFile;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 게임 관련 데이터 포맷팅 유틸리티
 * DTO에서 분리된 순수 포맷팅 로직
 */
@Component
public class GameStatusFormatter {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    /**
     * 게임 상태를 한글로 변환
     */
    public String formatGameStatus(Games.GameStatus status) {
        if (status == null) return "알 수 없음";

        switch (status) {
            case PENDING_REVIEW: return "승인 대기";
            case UNDER_REVIEW: return "검토 중";
            case ACTIVE: return "서비스 중";
            case REJECTED: return "승인 거부";
            case SUSPENDED: return "일시 정지";
            case DEACTIVATED: return "서비스 종료";
            case UPLOAD_FAILED: return "업로드 실패";
            default: return "알 수 없음";
        }
    }

    /**
     * 파일 크기를 읽기 쉬운 형태로 변환
     */
    public String formatFileSize(Long bytes) {
        if (bytes == null) return "알 수 없음";

        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.1f KB", bytes / 1024.0);
        if (bytes < 1024 * 1024 * 1024) return String.format("%.1f MB", bytes / (1024.0 * 1024.0));
        return String.format("%.1f GB", bytes / (1024.0 * 1024.0 * 1024.0));
    }

    /**
     * 날짜를 문자열로 포맷팅
     */
    public String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) return "-";
        return dateTime.format(DATE_TIME_FORMATTER);
    }

    /**
     * 상태별 CSS 클래스명 반환 (UI용)
     */
    public String getStatusBadgeClass(Games.GameStatus status) {
        if (status == null) return "badge-light";

        switch (status) {
            case PENDING_REVIEW: return "badge-warning";
            case UNDER_REVIEW: return "badge-info";
            case ACTIVE: return "badge-success";
            case REJECTED: return "badge-danger";
            case SUSPENDED: return "badge-secondary";
            case DEACTIVATED: return "badge-dark";
            case UPLOAD_FAILED: return "badge-danger";
            default: return "badge-light";
        }
    }
}