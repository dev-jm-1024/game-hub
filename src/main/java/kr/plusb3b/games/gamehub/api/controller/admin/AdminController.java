package kr.plusb3b.games.gamehub.api.controller.admin;


import jakarta.servlet.http.HttpServletRequest;
import kr.plusb3b.games.gamehub.domain.user.entity.User;
import kr.plusb3b.games.gamehub.security.AccessControlService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.Model;


@Controller
@RequestMapping("/admin")
@Slf4j
public class AdminController {

    private final AccessControlService access;

    public AdminController(AccessControlService access) {
        this.access = access;
    }

    @GetMapping
    public String showAdminPage(Model model, HttpServletRequest request){

        User user = access.getAuthenticatedUser(request);

        model.addAttribute("admin", user);

        return "admin/index";
    }



//
//    //사용자 관리 : /user-status
//    @GetMapping("/user-status")
//    public String viewUserStatusPage(){
//        return "admin/user-status/index";
//    }
//
//
//    //게시판 관리 - 공지사항 관리, 게시물 관리
//    //: /board-status, /board/notice-status, /board/posts-status
//
//    @GetMapping("/board-status")
//    public String  viewBoardStatusPage(Model model){
//        return "admin/board-status/index";
//    }
//
//    @GetMapping("/board/notice-status")
//    public String viewNoticeStatusPage(Model model){
//        return "admin/notice-status/index";
//    }
//
//    @GetMapping("/board/posts-status")
//    public String viewPostsStatusPage(Model model){
//        return "admin/post-status/index";
//    }

}