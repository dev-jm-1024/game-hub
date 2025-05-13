package kr.plusb3b.games.gamehub.api.controller.board;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/boards")
public class BoardController {

    @GetMapping
    public String showBoardPage(){
        return "board/main-board";
    }

    //공지사항 경로 처리
    @GetMapping("/notice")
    public String showNoticePage(){
        return "board/notice";
    }

    //자유 게시판 경로 처리
    @GetMapping("/free")
    public String showFreeBoardPage(){
        return "board/free-board";
    }

    @GetMapping("/guide")
    public String showGuidePage(){
        return "board/guide";
    }

    @GetMapping("/qna")
    public String showQnaPage(){
        return "board/qna";
    }
}
