package kr.plusb3b.games.gamehub.api.controller.board;

import kr.plusb3b.games.gamehub.domain.board.service.viewmodel.MainBoardVmService;
import kr.plusb3b.games.gamehub.domain.board.service.viewmodel.PostGamesListVmService;
import kr.plusb3b.games.gamehub.domain.board.service.viewmodel.PostListVmService;
import kr.plusb3b.games.gamehub.view.board.MainBoardVM;
import kr.plusb3b.games.gamehub.view.board.PostGamesListVM;
import kr.plusb3b.games.gamehub.view.board.PostListVM;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/board")
public class BoardController {

    /*리팩토링 진행하면서 추가한 것들*/
    private final MainBoardVmService mainBoardVmService;
    private final PostListVmService postListVmService;
    private final PostGamesListVmService postGamesListVmService;

    public BoardController(MainBoardVmService mainBoardVmService,
                           PostListVmService postListVmService, PostGamesListVmService postGamesListVmService) {
        this.mainBoardVmService = mainBoardVmService;
        this.postListVmService = postListVmService;
        this.postGamesListVmService = postGamesListVmService;
    }

    @GetMapping
    public String boardMainPage(Model model) {

        MainBoardVM vm = mainBoardVmService.getMainBoardVm();
        model.addAttribute("vm", vm);

        return "board/main-board";
    }


    //게시판 경로 처리
    @GetMapping("/{boardId}/view")
    public String dispatchBoardPost(@PathVariable("boardId") String boardId, Model model) {

        PostListVM vm = postListVmService.getPostListVm(boardId);
        model.addAttribute("vm", vm);

        return "board/common/post-list";
    }

    @GetMapping("/game-board/{boardId}/view")
    public String gameBoardMainPage(@PathVariable("boardId") String boardId ,Model model) {

        PostGamesListVM vm = postGamesListVmService.getPostGameListVm(boardId);
        model.addAttribute("vm", vm);

        return "board/common/post-games-list";
    }

}