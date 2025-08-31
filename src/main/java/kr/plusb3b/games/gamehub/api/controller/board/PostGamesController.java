package kr.plusb3b.games.gamehub.api.controller.board;

import kr.plusb3b.games.gamehub.domain.board.service.viewmodel.PostGamesDetailVmService;
import kr.plusb3b.games.gamehub.domain.game.dto.GameDetailDto;
import kr.plusb3b.games.gamehub.domain.game.service.GameMetadataService;
import kr.plusb3b.games.gamehub.domain.user.entity.User;
import kr.plusb3b.games.gamehub.security.AccessControlService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.plusb3b.games.gamehub.view.board.PostGamesDetailVM;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

@Controller
@RequestMapping("/board")
@CrossOrigin(origins = "*") // CORS 허용
public class PostGamesController {


    /** 리팩토링 후 의존성 주입 **/
    private final PostGamesDetailVmService postGamesDetailVmService;


    public PostGamesController(PostGamesDetailVmService postGamesDetailVmService) {
        this.postGamesDetailVmService = postGamesDetailVmService;
    }

    @GetMapping("/{boardId}/game/{gameId}/view")
    public String showGameViewPage(@PathVariable("boardId") String boardId, @PathVariable("gameId") Long gameId,
                                   HttpServletRequest request, Model model){

        /********************** 리팩토링 후  *****************************/

        PostGamesDetailVM vm = postGamesDetailVmService.getPostGamesDetailVm(request, boardId, gameId);
        model.addAttribute("vm", vm);

        return "board/common/post-games-detail";
    }

    // 🆕 게임 프록시 메서드 추가
    @GetMapping("/game-proxy/**")
    @CrossOrigin(origins = "*")
    public void proxyGameFile(HttpServletRequest request, HttpServletResponse response) throws IOException {

        // 1. 요청된 경로에서 게임 파일 경로 추출
        String requestURI = request.getRequestURI();
        String gamePath = requestURI.replace("/board/game-proxy/", "");

        // 2. Google Storage의 실제 URL 구성
        String googleStorageUrl = "https://storage.googleapis.com/game-webgl-bucket-capstone/" + gamePath;

        System.out.println("프록시 요청: " + requestURI);
        System.out.println("실제 URL: " + googleStorageUrl);

        try {
            // 3. Google Storage에서 파일 다운로드
            URL url = new URL(googleStorageUrl);
            URLConnection connection = url.openConnection();

            // 4. Content-Type 설정 (파일 확장자에 따라)
            String contentType = getContentType(gamePath);
            response.setContentType(contentType);

            // 5. CORS 헤더 추가
            response.setHeader("Access-Control-Allow-Origin", "*");
            response.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
            response.setHeader("Access-Control-Allow-Headers", "*");

            // 6. 파일 내용을 그대로 전달
            try (InputStream inputStream = connection.getInputStream()) {
                inputStream.transferTo(response.getOutputStream());
            }

        } catch (Exception e) {
            System.err.println("프록시 오류: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("파일을 찾을 수 없습니다: " + gamePath);
        }
    }

    // Content-Type 결정 헬퍼 메서드
    private String getContentType(String filePath) {
        String lowerPath = filePath.toLowerCase();

        if (lowerPath.endsWith(".html")) return "text/html; charset=utf-8";
        if (lowerPath.endsWith(".js")) return "application/javascript; charset=utf-8";
        if (lowerPath.endsWith(".css")) return "text/css; charset=utf-8";
        if (lowerPath.endsWith(".json")) return "application/json; charset=utf-8";
        if (lowerPath.endsWith(".png")) return "image/png";
        if (lowerPath.endsWith(".jpg") || lowerPath.endsWith(".jpeg")) return "image/jpeg";
        if (lowerPath.endsWith(".wasm")) return "application/wasm";
        if (lowerPath.endsWith(".data")) return "application/octet-stream";
        if (lowerPath.endsWith(".symbols.json")) return "application/octet-stream";

        return "application/octet-stream";
    }
}