package kr.plusb3b.games.gamehub.api.controller.admin;

import kr.plusb3b.games.gamehub.domain.board.entity.PostFiles;
import kr.plusb3b.games.gamehub.domain.board.service.PostFilesService;
import kr.plusb3b.games.gamehub.security.AccessControlService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import jakarta.servlet.http.HttpServletRequest;
import kr.plusb3b.games.gamehub.domain.board.entity.Posts;
import kr.plusb3b.games.gamehub.domain.board.service.PostsService;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import jakarta.servlet.http.*;

@Controller
@RequestMapping("/admin/board")
public class AdminNoticeController {

    private final PostsService postsService;
    private final AccessControlService access;
    private final PostFilesService postFilesService;
    private final String NOTICE_ID = "notice";

    public AdminNoticeController(PostsService postsService, AccessControlService access,
                                 PostFilesService postFilesService) {
        this.postsService = postsService;
        this.access = access;
        this.postFilesService = postFilesService;
    }

    @GetMapping("/notice-status")
    public String viewNoticeStatusPage(Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {
        access.validateAdminAccess(request, response);

        // 전체 공지사항 목록
        List<Posts> noticeList = postsService.getPostsByBoardId(NOTICE_ID);

        // 중요 공지사항 목록 (활성화된 것만, 최신순)
        List<Posts> importantList = noticeList.stream()
                .filter(post -> post.isImportant() && post.isActivatePosts())
                .sorted(Comparator.comparing(Posts::getCreatedAt).reversed()) // 최신순으로 변경
                .collect(Collectors.toList());

        // 일반 공지사항 목록 (중요하지 않은 것들, 최신순)
        List<Posts> generalList = noticeList.stream()
                .filter(post -> !post.isImportant())
                .sorted(Comparator.comparing(Posts::getCreatedAt).reversed())
                .collect(Collectors.toList());

        model.addAttribute("noticeList", noticeList);
        model.addAttribute("importantList", importantList);
        model.addAttribute("generalList", generalList); // 일반 공지사항 따로 분리

        return "admin/notice-status/index";
    }

    @GetMapping("/notice/{postId}")
    public String viewNoticeDetailPage(Model model, @PathVariable("postId") Long postId, HttpServletRequest request, HttpServletResponse response) throws IOException {
        access.validateAdminAccess(request, response);

        Posts notice = postsService.detailPosts(NOTICE_ID, postId);
        List<PostFiles> postFilesList = postFilesService.getPostFiles(postId);

        model.addAttribute("postFilesList", postFilesList);
        model.addAttribute("notice", notice);

        return "admin/notice-status/notice-detail";
    }

    @GetMapping("/notice/create")
    public String viewCreateNoticePage(Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {
        access.validateAdminAccess(request, response);

        model.addAttribute("noticeId", NOTICE_ID);

        return "admin/notice-status/notice-form"; // 슬래시 제거
    }

    @GetMapping("/notice/{postId}/edit")
    public String viewEditNoticePage(Model model, HttpServletRequest request,
                                     HttpServletResponse response, @PathVariable Long postId) throws IOException {

        Posts posts = postsService.detailPosts(NOTICE_ID, postId);
        access.validateAdminAccess(request, response);

        model.addAttribute("notice", posts);

        List<PostFiles> postFiles = postFilesService.getPostFiles(postId);
        model.addAttribute("postFiles", postFiles);


        return "admin/notice-status/notice-edit-form";
    }
}