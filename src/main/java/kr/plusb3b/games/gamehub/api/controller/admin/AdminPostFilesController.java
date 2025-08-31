package kr.plusb3b.games.gamehub.api.controller.admin;

import kr.plusb3b.games.gamehub.domain.board.entity.Board;
import kr.plusb3b.games.gamehub.domain.board.entity.PostFiles;
import kr.plusb3b.games.gamehub.domain.board.service.viewmodel.AdminPostFilesVmService;
import kr.plusb3b.games.gamehub.view.admin.AdminPostFilesVM;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin/board")
public class AdminPostFilesController {

    private final AdminPostFilesVmService mediaService;

    public AdminPostFilesController(AdminPostFilesVmService mediaService) {
        this.mediaService = mediaService;
    }

    @GetMapping("/post-file-status")
    public String viewPostFilesMainPage(Model model){

        Map<Board, List<PostFiles>> vm = mediaService.getPostFilesTop5();
        model.addAttribute("vm", vm);

        return "admin/post-file-status/index";
    }

    @GetMapping("/{boardId}/post-file-status")
    public String viewPostFilesPageByBoard(Model model, @PathVariable("boardId") String boardId){

        List<AdminPostFilesVM> vm = mediaService.getPostFiles(boardId);
        model.addAttribute("vm", vm);

        return "admin/post-file-status/posts-file-detail";
    }
}
