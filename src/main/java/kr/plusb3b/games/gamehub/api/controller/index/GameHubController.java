package kr.plusb3b.games.gamehub.api.controller.index;

import jakarta.servlet.http.HttpServletRequest;
import kr.plusb3b.games.gamehub.api.dto.board.Board;
import kr.plusb3b.games.gamehub.api.jwt.JwtProvider;
import kr.plusb3b.games.gamehub.repository.boardrepo.BoardRepository;
import org.atmosphere.config.service.Get;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/game-hub")
public class GameHubController {

    private final BoardRepository boardRepo;
    @Value("${app.api.version}")
    private String apiVersion;

    @Value("${app.deactivate.path}")
    private String deactivatePath;


    public GameHubController( BoardRepository boardRepo) {
        this.boardRepo = boardRepo;
    }


    @GetMapping
    public String showMainPage(Authentication authentication, Model model) {

        System.out.println("[Controller] 인증객체: " + authentication);

        // ✅ 로그인 여부 판별 (SecurityContext에 인증 객체 있는지)
        boolean isMember = authentication != null && authentication.isAuthenticated();
        System.out.println("isMember: "+isMember);

        // ✅ userId 꺼내기 (현재는 문자열로 저장되어 있음)
        String userId = isMember ? (String) authentication.getPrincipal() : null;

        // (선택) DB에서 userId로 유저 정보 조회하여 닉네임, 프로필 이미지 등 세팅 가능
        String testImageUrl = "https://search.pstatic.net/sunny/?src=https%3A%2F%2Fi.pinimg.com" +
                "%2Foriginals%2F9f%2F85%2Ffc%2F9f85fc037501419f146f81e5a2ab7a98.jpg" +
                "&type=sc960_832";

        //게시판 정보 Model에 줘야함

        //1. boardId, boardName 조회
        List<Board> boardList = boardRepo.findAll();
        boolean checkBoardListEmpty = boardList.isEmpty();

        if(!checkBoardListEmpty) {
            model.addAttribute("boardList", boardList);
        }



        model.addAttribute("isAdmin", true); // 나중에 권한에 따라 분기 처리 가능
        model.addAttribute("profileImage", testImageUrl);
        model.addAttribute("isLoggedIn", isMember);
        model.addAttribute("nickname", userId); // 일단 userId를 닉네임으로 사용
        model.addAttribute("deactivatePath", deactivatePath);

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

}

