package kr.plusb3b.games.gamehub.api.controller.index;

import kr.plusb3b.games.gamehub.domain.board.entity.Board;
import kr.plusb3b.games.gamehub.domain.board.repository.BoardRepository;
import kr.plusb3b.games.gamehub.domain.user.entity.User;
import kr.plusb3b.games.gamehub.security.AccessControlService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Value;
import jakarta.servlet.http.HttpServletRequest;


import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/game-hub")
public class GameHubController {

    private final BoardRepository boardRepo;
    private final AccessControlService access;

    @Value("${app.api.version}")
    private String apiVersion;

    @Value("${app.deactivate.path}")
    private String deactivatePath;


    public GameHubController( BoardRepository boardRepo, AccessControlService access) {
        this.boardRepo = boardRepo;
        this.access = access;
    }


    @GetMapping
    public String showMainPage(HttpServletRequest request, Model model) {


        // (선택) DB에서 userId로 유저 정보 조회하여 닉네임, 프로필 이미지 등 세팅 가능
        String testImageUrl = "https://search.pstatic.net/sunny/?src=https%3A%2F%2Fi.pinimg.com" +
                "%2Foriginals%2F9f%2F85%2Ffc%2F9f85fc037501419f146f81e5a2ab7a98.jpg" +
                "&type=sc960_832";

        model.addAttribute("isAdmin", true); // 추후 권한 분기 처리
        model.addAttribute("deactivatePath", deactivatePath); // 비활성화 경로

        User user = access.getAuthenticatedUser(request);
        boolean isLoggedIn = (user != null);

        System.out.println("isLoggedIn : " + isLoggedIn);
        model.addAttribute("isLoggedIn", isLoggedIn);

        //게시판 데이터 넘기기
        List<Board> boardList = boardRepo.findAll().stream()
                        .filter(Board::isActivateBoard)
                                .collect(Collectors.toList());
        model.addAttribute("boardList", boardList);

        if (isLoggedIn) {
            model.addAttribute("nickname", user.getMbNickName());
            model.addAttribute("profileImage", user.getMbProfileUrl() != null ? user.getMbProfileUrl() : testImageUrl);
        }

        return "main-contents/index";
    }


    //회원가입 페이지 이동
    @GetMapping("/join")
    public String showJoinPage(){
        return "join/join-form";
    }

    //로그인 페이지 이동
    @GetMapping("/login")
    public String showLoginPage(Model model){
        model.addAttribute("apiVersion", apiVersion);
        return "login/login-form";
    }

    //팀 회원가입 페이지 이동
    @GetMapping("/join-prod")
    public String showJoinProdPage(Model model){
        String prod="producer";
        model.addAttribute("prod", prod);
        return "join/join-form-producer";
    }

    //프로듀서 페이지 이동
    @GetMapping("/prod")
    public String redirectToProdPage(){
        return "main-contents/index-prod";
    }

    //임시로 관리자 페이지 접근
    @GetMapping("/admin")
    public String redirectToAdminPage(){
        return "admin/index";
    }

}

