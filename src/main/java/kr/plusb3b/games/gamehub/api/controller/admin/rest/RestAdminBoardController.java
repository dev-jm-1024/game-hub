package kr.plusb3b.games.gamehub.api.controller.admin.rest;

import jakarta.servlet.http.*;
import kr.plusb3b.games.gamehub.domain.board.dto.CreateBoardDto;
import kr.plusb3b.games.gamehub.domain.board.service.admin.AdminBoardService;
import kr.plusb3b.games.gamehub.security.AccessControlService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/admin/api/v1")
public class RestAdminBoardController {

    private final AdminBoardService adminBoardService;
    private final AccessControlService access;


    public RestAdminBoardController(AdminBoardService adminBoardService, AccessControlService access) {
        this.adminBoardService = adminBoardService;
        this.access = access;
    }

    //게시판 생성
    @PostMapping("/board/create")
    public ResponseEntity<?> createBoard(@ModelAttribute CreateBoardDto createBoardDto,
                                         HttpServletRequest request,
                                         HttpServletResponse response) throws IOException {

        access.validateAdminAccess(request, response);

        //게시판 생성 로직
        adminBoardService.createBoard(createBoardDto);

        return ResponseEntity.status(HttpStatus.OK).body("성공적으로 반영되었습니다");
    }

    //게시판 활성화
    @PostMapping("/board/{boardId}/activate")
    public ResponseEntity<?> activateBoard(@PathVariable String boardId,
                                           HttpServletRequest request,
                                           HttpServletResponse response) throws IOException {

        //로그인 여부 및 관리자 권한 검사
        access.validateAdminAccess(request, response);

        //게시판 활성화 로직
        adminBoardService.changeBoardStatus(boardId, 1);

        return ResponseEntity.status(HttpStatus.OK).body("성공적으로 반영되었습니다");
    }

    //게시판 활성화
    @PostMapping("/board/{boardId}/deactivate")
    public ResponseEntity<?> deactivateBoard(@PathVariable String boardId,
                                             HttpServletRequest request,
                                             HttpServletResponse response) throws IOException {

        //로그인 여부 및 관리자 권한 검사
        access.validateAdminAccess(request, response);

        //게시판 비활성화 로직
        adminBoardService.changeBoardStatus(boardId, 0);

        return ResponseEntity.status(HttpStatus.OK).body("성공적으로 반영되었습니다");
    }

    @PostMapping("/boards/check-name")
    public ResponseEntity<?> checkBoardName(@ModelAttribute CreateBoardDto dto,
                                                                 HttpServletRequest request,
                                                                 HttpServletResponse response) throws IOException {
        // 관리자 권한 확인
        access.validateAdminAccess(request, response);

        boolean available = adminBoardService.isDuplicateBoardName(dto.getBoardName().trim());

        if (available) return ResponseEntity.status(HttpStatus.OK).body("사용 가능한 이름입니다.");
        else return ResponseEntity.status(HttpStatus.CONFLICT).body("이미 존재하는 이름입니다.");
    }




    @PatchMapping("/boards/{boardId}/name")
    public ResponseEntity<String> updateBoardName(@PathVariable String boardId,
                                                  @RequestParam String newName) {
        // 여기서 IllegalArgumentException 발생 가능
        adminBoardService.renameBoard(boardId, newName);
        return ResponseEntity.ok("게시판 이름 변경 완료");
    }



}
