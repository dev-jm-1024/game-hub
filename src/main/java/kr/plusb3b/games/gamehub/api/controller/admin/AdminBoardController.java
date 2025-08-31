package kr.plusb3b.games.gamehub.api.controller.admin;

import jakarta.servlet.http.*;
import kr.plusb3b.games.gamehub.application.admin.AdminBoardConfig;
import kr.plusb3b.games.gamehub.domain.admin.service.AdminService;
import kr.plusb3b.games.gamehub.domain.board.entity.Board;
import kr.plusb3b.games.gamehub.domain.board.service.BoardService;
import kr.plusb3b.games.gamehub.security.AccessControlService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/board-status")
public class AdminBoardController {

    private final BoardService boardService;
    private final AdminService adminService;
    private final AccessControlService access;
    private final AdminBoardConfig adminBoardConfig;

    private final String REDIRECT_PATH = "redirect:/game-hub";
    private final String NOTICE = "notice";


    public AdminBoardController(BoardService boardService, AdminService adminService,
                                AccessControlService access, AdminBoardConfig adminBoardConfig) {
        this.boardService = boardService;
        this.adminService = adminService;
        this.access = access;
        this.adminBoardConfig = adminBoardConfig;
    }



    //게시판 데이터 가져오기 - 공지사항 제외
    //공지사항은 별도로 처리 - 읽기/쓰기 이슈
    @GetMapping
    public String viewBoardStatusPage(Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {

        access.validateAdminAccess(request, response);

        //게시판 데이터 넘기기
        List<Board> boardList = boardService.getAllBoards().stream()
                        .filter(
                                b -> !b.getBoardId().equals(NOTICE)
                        ).collect(Collectors.toList());

        model.addAttribute("boardList", boardList);

        //게시판 API 경로 넣어주기
        Map<String, String> boardApiPaths = adminBoardConfig.getAdminBoardPaths();
        model.addAttribute("boardApiPaths", boardApiPaths);

        return "admin/board-status/index";
    }

    @GetMapping("/edit")
    public String viewBoardEditPage(Model model, HttpServletRequest request , HttpServletResponse response) throws IOException{

        access.validateAdminAccess(request, response);

        //게시판 데이터 넘기기
        List<Board> boardList = boardService.getAllBoards().stream()
                .filter(
                        b -> !b.getBoardId().equals(NOTICE)
                ).collect(Collectors.toList());

        model.addAttribute("boardList", boardList);

        //게시판 API 경로 넣어주기
        Map<String, String> boardApiPaths = adminBoardConfig.getAdminBoardPaths();
        model.addAttribute("boardApiPaths", boardApiPaths);

        return "admin/board-status/edit-board";
    }

    @GetMapping("/board/create")
    public String viewCreateBoardPage(Model model, HttpServletRequest request , HttpServletResponse response) throws IOException{

        access.validateAdminAccess(request, response);

        //게시판 API 경로 넣어주기
        Map<String, String> boardApiPaths = adminBoardConfig.getAdminBoardPaths();

        model.addAttribute("boardApiPaths", boardApiPaths);
        return "admin/board-status/make-board";
    }




}
