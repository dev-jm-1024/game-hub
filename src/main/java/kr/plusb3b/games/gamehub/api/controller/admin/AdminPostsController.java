package kr.plusb3b.games.gamehub.api.controller.admin;

import jakarta.servlet.http.*;
import kr.plusb3b.games.gamehub.domain.board.entity.Board;
import kr.plusb3b.games.gamehub.domain.board.entity.PostFiles;
import kr.plusb3b.games.gamehub.domain.board.entity.Posts;
import kr.plusb3b.games.gamehub.domain.board.service.BoardService;
import kr.plusb3b.games.gamehub.domain.board.service.PostFilesService;
import kr.plusb3b.games.gamehub.domain.board.service.PostsService;
import kr.plusb3b.games.gamehub.domain.board.service.viewmodel.AdminPostsDetailVmService;
import kr.plusb3b.games.gamehub.domain.board.service.viewmodel.AdminSummaryPostVmService;
import kr.plusb3b.games.gamehub.domain.board.service.viewmodel.MainBoardVmService;
import kr.plusb3b.games.gamehub.domain.board.service.viewmodel.PostListVmService;
import kr.plusb3b.games.gamehub.security.AccessControlService;
import kr.plusb3b.games.gamehub.view.admin.AdminPostsDetailVM;
import kr.plusb3b.games.gamehub.view.admin.AdminSummaryPostVM;
import kr.plusb3b.games.gamehub.view.board.MainBoardVM;
import kr.plusb3b.games.gamehub.view.board.PostListVM;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/board")
public class AdminPostsController {

    /***************** 리팩토링 후 의존성 주입 *****************/
    private final AccessControlService access;
    private final MainBoardVmService mainBoardVmService;
    private final AdminSummaryPostVmService adminSummaryPostVmService;
    private final AdminPostsDetailVmService adminPostsDetailVmService;

    public AdminPostsController(AccessControlService access, MainBoardVmService mainBoardVmService,
                                AdminSummaryPostVmService adminSummaryPostVmService,
                                AdminPostsDetailVmService adminPostsDetailVmService) {
        this.access = access;
        this.mainBoardVmService = mainBoardVmService;
        this.adminSummaryPostVmService = adminSummaryPostVmService;
        this.adminPostsDetailVmService = adminPostsDetailVmService;
    }

    //공지사항은 별도로 처리 - 읽기/쓰기 이슈
    @GetMapping("/posts-status")
    public String viewMainPostStatus(Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {

        access.validateAdminAccess(request, response);

        MainBoardVM vm = mainBoardVmService.getMainBoardVm();
        model.addAttribute("vm", vm);

        return "admin/post-status/index";
    }

    //GET: /admin/board/{boardId}/post-status
    @GetMapping("/{boardId}/post-status")
    public String viewPostStatusPageByBoard(Model model, @PathVariable("boardId") String boardId){

        List<AdminSummaryPostVM> vm = adminSummaryPostVmService.getAdminSummaryPostVm(boardId);
        model.addAttribute("vm", vm);

        return "admin/post-status/summary-post";

    }

    @GetMapping("/{boardId}/post-status/{postId}/detail")
    public String viewPostDetailPage(Model model, @PathVariable("boardId") String boardId,
                                     @PathVariable("postId") Long postId){


        AdminPostsDetailVM vm = adminPostsDetailVmService.getAdminPostsDetailVm(boardId, postId);
        model.addAttribute("vm", vm);

        return "admin/post-status/posts-detail";
    }



}
