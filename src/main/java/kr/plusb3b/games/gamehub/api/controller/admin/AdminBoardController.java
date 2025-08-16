package kr.plusb3b.games.gamehub.api.controller.admin;

import jakarta.servlet.http.HttpServletRequest;
import kr.plusb3b.games.gamehub.domain.admin.service.AdminService;
import kr.plusb3b.games.gamehub.domain.board.entity.Board;
import kr.plusb3b.games.gamehub.domain.board.service.BoardService;
import kr.plusb3b.games.gamehub.domain.user.entity.User;
import kr.plusb3b.games.gamehub.security.AccessControlService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/board-status")
public class AdminBoardController {

    private final BoardService boardService;
    private final AdminService adminService;
    private final AccessControlService access;


    private final String REDIRECT_PATH = "redirect:/game-hub";
    private final String NOTICE_PATH = "notice";

    public AdminBoardController(BoardService boardService, AdminService adminService,
                                AccessControlService access) {
        this.boardService = boardService;
        this.adminService = adminService;
        this.access = access;
    }

    //게시판 데이터 가져오기 - 공지사항 제외
    //공지사항은 별도로 처리 - 읽기/쓰기 이슈
    @GetMapping
    public String viewBoardStatusPage(Model model, HttpServletRequest request){

        User user = access.getAuthenticatedUser(request);

        if(user == null || user.getMbRole() != User.Role.ROLE_ADMIN) {
            return REDIRECT_PATH;
        }

        //게시판 데이터 넘기기
        List<Board> boardList = boardService.getAllBoards().stream()
                        .filter(
                                b -> !b.getBoardId().equals(NOTICE_PATH)
                        ).collect(Collectors.toList());

        model.addAttribute("boardList", boardList);

        return "admin/board-status/index";
    }



}
