package kr.plusb3b.games.gamehub.api.controller.admin.rest;

import jakarta.servlet.http.HttpServletRequest;
import kr.plusb3b.games.gamehub.domain.admin.service.AdminService;
import kr.plusb3b.games.gamehub.domain.board.service.BoardService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/api/v1")
public class RestAdminBoardController {

    private final BoardService boardService;
    private final AdminService adminService;

    public RestAdminBoardController(BoardService boardService, AdminService adminService) {

        this.boardService = boardService;
        this.adminService = adminService;
    }
    @PatchMapping("/boards/{boardId}/name")
    public ResponseEntity<?> updateBoardName(@PathVariable String boardId,
                                             @RequestBody String newName,
                                             HttpServletRequest request) {

        // 권한 체크
        HttpStatus status = adminService.checkAdminOrReturnStatus(request);
        if (status != HttpStatus.OK) {
            return ResponseEntity.status(status)
                    .body("관리자 권한이 필요합니다.");
        }

        try {
            boolean result = boardService.renameBoard(boardId, newName);

            if (result) {
                return ResponseEntity.ok("게시판 이름이 성공적으로 변경되었습니다.");
            } else {
                return ResponseEntity.badRequest()
                        .body("게시판 이름 변경에 실패했습니다.");
            }

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("서버 오류가 발생했습니다.");
        }
    }

    @PutMapping("/boards/{boardId}/activate")
    public ResponseEntity<?> activateBoard(@PathVariable String boardId, HttpServletRequest request) {

        // 권한 체크
        HttpStatus status = adminService.checkAdminOrReturnStatus(request);
        if (status != HttpStatus.OK) {
            return ResponseEntity.status(status)
                    .body("관리자 권한이 필요합니다.");
        }

        // 활성화 로직 - Request Body 필요 없음

        return ResponseEntity.status(HttpStatus.OK).body("ok");
    }

    @PutMapping("/boards/{boardId}/deactivate")
    public ResponseEntity<?> deactivateBoard(@PathVariable String boardId, HttpServletRequest request) {
        // 비활성화 로직 - Request Body 필요 없음

        return ResponseEntity.status(HttpStatus.OK).body("ok");
    }

}
