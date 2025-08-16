package kr.plusb3b.games.gamehub.api.controller.admin;

import jakarta.servlet.http.HttpServletRequest;
import kr.plusb3b.games.gamehub.domain.admin.service.AdminService;
import kr.plusb3b.games.gamehub.domain.board.entity.Posts;
import kr.plusb3b.games.gamehub.domain.board.service.PostsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/board/posts-status")
public class AdminPostsController {

    private final PostsService postsService;
    private final AdminService adminService;

    private final String REDIRECT_PATH = "redirect:/game-hub";
    private final String NOTICE_PATH = "notice";

    public AdminPostsController(PostsService postsService, AdminService adminService) {
        this.postsService = postsService;
        this.adminService = adminService;
    }

    //공지사항은 별도로 처리 - 읽기/쓰기 이슈
    @GetMapping
    public String showPostsStatusPage(Model model, HttpServletRequest request){

        String redirect = adminService.checkAdminOrRedirect(request, REDIRECT_PATH);
        if (redirect != null) {
            return redirect; // 리다이렉트
        }

        //전체 게시물 데이터
        List<Posts> postList = postsService.getAllPosts().stream()
                .filter(
                        p -> !p.getBoard().getBoardId().equals(NOTICE_PATH)
                )
                .collect(Collectors.toList());

        model.addAttribute("postList", postList);


        return "admin/post-status/index";
    }


}
