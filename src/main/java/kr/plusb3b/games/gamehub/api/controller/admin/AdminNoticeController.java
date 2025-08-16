package kr.plusb3b.games.gamehub.api.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import jakarta.servlet.http.HttpServletRequest;
import kr.plusb3b.games.gamehub.domain.admin.service.AdminService;
import kr.plusb3b.games.gamehub.domain.board.entity.Posts;
import kr.plusb3b.games.gamehub.domain.board.service.PostsService;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/board/notice-status")
public class AdminNoticeController {

    private final PostsService postsService;
    private final AdminService adminService;

    private final String REDIRECT_PATH = "redirect:/game-hub";
    private final String NOTICE_PATH = "notice";

    public AdminNoticeController(PostsService postsService, AdminService adminService) {
        this.postsService = postsService;
        this.adminService = adminService;
    }

    @GetMapping
    public String viewNoticeStatusPage(Model model, HttpServletRequest request){

        String redirect = adminService.checkAdminOrRedirect(request, REDIRECT_PATH);
        if (redirect != null) {
            return redirect; // 리다이렉트
        }

        List<Posts> noticeList = postsService.getAllPosts().stream()
                .filter(
                        p-> p.getBoard().getBoardId().equals(NOTICE_PATH)
                )
                .collect(Collectors.toList());

        model.addAttribute("postList", noticeList);

        return "admin/notice-status/index";
    }

}
