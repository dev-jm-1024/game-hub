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
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/board-status")
public class AdminBoardController {

    private final BoardService boardService;
    private final AdminService adminService;
    private final AccessControlService access;


    private final String REDIRECT_PATH = "redirect:/game-hub";
    private final String NOTICE = "notice";

    // API 경로
    private String ADMIN_PATH = "/admin/api/v1";
    private String ACTIVATE_PATH = ADMIN_PATH + "/board/{boardId}/activate";
    private String DEACTIVATE_PATH = ADMIN_PATH + "/board/{boardId}/deactivate";
    private String RENAME_PATH = ADMIN_PATH + "/board/{boardId}/rename";
    private String MAKE_PATH = ADMIN_PATH + "/board/make";


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
                                b -> !b.getBoardId().equals(NOTICE)
                        ).collect(Collectors.toList());

        model.addAttribute("boardList", boardList);

        //API 경로 Model에 넣어주기
        model.addAttribute("boardApiPaths", boardApiPaths());

        return "admin/board-status/index";
    }

    @GetMapping("/board/create")
    public String viewCreateBoardPage(Model model){

        model.addAttribute("boardApiPaths", boardApiPaths());
        return "admin/board-status/make-board";
    }


    private Map<String ,String> boardApiPaths(){
        return Map.ofEntries(
                Map.entry("activate", ACTIVATE_PATH),
                Map.entry("deactivate", DEACTIVATE_PATH),
                Map.entry("rename", RENAME_PATH),
                Map.entry("makeBoard", MAKE_PATH)
        );
    }



}
