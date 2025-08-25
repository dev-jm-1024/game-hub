package kr.plusb3b.games.gamehub.api.controller.admin.rest;

import jakarta.servlet.http.HttpServletRequest;
import kr.plusb3b.games.gamehub.domain.admin.service.AdminService;
import kr.plusb3b.games.gamehub.domain.board.dto.CreateBoardDto;
import kr.plusb3b.games.gamehub.domain.board.service.BoardService;
import kr.plusb3b.games.gamehub.domain.user.entity.User;
import kr.plusb3b.games.gamehub.security.AccessControlService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/admin/api/v1")
public class RestAdminBoardController {

    private final BoardService boardService;
    private final AdminService adminService;
    private final AccessControlService access;

    public RestAdminBoardController(BoardService boardService, AdminService adminService,
                                    AccessControlService access) {

        this.boardService = boardService;
        this.adminService = adminService;
        this.access = access;
    }

    //게시판 생성
    @PostMapping("/board/create")
    public ResponseEntity<?> createBoard(@ModelAttribute CreateBoardDto createBoardDto, HttpServletRequest request){

        //로그인을 했는지?
        User user = access.getAuthenticatedUser(request);

        if(user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다");
        else if(user.getMbRole() != User.Role.ROLE_ADMIN) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("관리자 권한이 존재하지 않습니다");

        int result = boardService.createBoard(createBoardDto);
        if(result == 0) return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("DB Error");

        System.out.println("boardName: " + createBoardDto.getBoardName());

        return ResponseEntity.status(HttpStatus.OK).body("성공적으로 반영되었습니다");
    }

    //게시판 활성화
    @PostMapping("/board/{boardId}/activate")
    public ResponseEntity<?> activateBoard(@PathVariable String boardId, HttpServletRequest request){

        if(boardId == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("게시판 아이디가 누락되었습니다");
        System.out.println("boardId: " + boardId);

        User user = access.getAuthenticatedUser(request);

        if(user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다");
        else if(user.getMbRole() != User.Role.ROLE_ADMIN) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("관리자 권한이 존재하지 않습니다");

        boolean result = boardService.changeBoardStatus(boardId, 1);

        if(!result) return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("DB Error");


        return ResponseEntity.status(HttpStatus.OK).body("성공적으로 반영되었습니다");
    }

    //게시판 활성화
    @PostMapping("/board/{boardId}/deactivate")
    public ResponseEntity<?> deactivateBoard(@PathVariable String boardId, HttpServletRequest request){

        if(boardId == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("게시판 아이디가 누락되었습니다");
        System.out.println("boardId: " + boardId);

        User user = access.getAuthenticatedUser(request);

        if(user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다");
        else if(user.getMbRole() != User.Role.ROLE_ADMIN) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("관리자 권한이 존재하지 않습니다");

        boolean result = boardService.changeBoardStatus(boardId, 0);

        if(!result) return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("DB Error");


        return ResponseEntity.status(HttpStatus.OK).body("성공적으로 반영되었습니다");
    }

    @PostMapping("/board/create/check-name")
    public ResponseEntity<?> checkBoardName(@ModelAttribute CreateBoardDto dto, HttpServletRequest request){

        // 관리자 권한 확인
        User user = access.getAuthenticatedUser(request);
        if(user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다");
        else if (user.getMbRole() != User.Role.ROLE_ADMIN) return ResponseEntity.status(HttpStatus.FORBIDDEN).body("권한이 올바르지 않습니다");

        String boardName = dto.getBoardName();

        System.out.println("=== 중복확인 요청 ===");
        System.out.println("받은 boardName: [" + boardName + "]");

        if (boardName == null || boardName.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("이름은 공백일 수 없습니다.");
        }

        // isDuplicateBoardName은 중복이 없으면 true를 반환
        boolean isAvailable = boardService.isDuplicateBoardName(boardName.trim());

        if (isAvailable) {
            return ResponseEntity.ok("사용 가능한 이름입니다.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                    .body("이미 존재하는 이름입니다.");
        }
    }



    @PostMapping("/board/{boardId}/name")
    public ResponseEntity<?> updateBoardName(@PathVariable String boardId,
                                             @RequestParam String newName,
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


}
