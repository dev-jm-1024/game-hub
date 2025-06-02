package kr.plusb3b.games.gamehub.api.controller.board;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import kr.plusb3b.games.gamehub.api.dto.board.*;
import kr.plusb3b.games.gamehub.api.dto.user.User;
import kr.plusb3b.games.gamehub.api.jwt.JwtProvider;
import kr.plusb3b.games.gamehub.repository.boardrepo.BoardRepository;
import kr.plusb3b.games.gamehub.repository.boardrepo.PostsRepository;
import kr.plusb3b.games.gamehub.repository.userrepo.UserRepository;
import kr.plusb3b.games.gamehub.security.AccessControlService;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/board")
public class BoardController {

    private final BoardRepository boardRepository;
    private final PostsRepository postsRepository;
    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;
    private final AccessControlService access;
    //private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(BoardController.class);4

//    @Value("${app.max.boardSize}")
//    String maxBoardSizeString;
//    int maxBoardSize = Integer.parseInt(maxBoardSizeString);


    public BoardController(BoardRepository boardRepository, PostsRepository postsRepository,
                           JwtProvider jwtProvider, UserRepository userRepository,
                           AccessControlService access) {
        this.boardRepository = boardRepository;
        this.postsRepository = postsRepository;
        this.jwtProvider = jwtProvider;
        this.userRepository = userRepository;
        this.access = access;
    }

    @GetMapping
    public String boardMainPage(Model model) {

        // 게시판 목록 전체 조회
        List<Board> boardList = boardRepository.findAll();
        model.addAttribute("boardList", boardList);

        // 전체 게시물 중에서 postAct == 1인 것만 필터링
        List<Posts> postsList = postsRepository.findAll();
        List<Posts> realPostsList = postsList.stream()
                .filter(post -> post.getPostAct() == 1)
                .collect(Collectors.toList());

        // 게시판별 게시물 존재 여부 Map
        Map<String, Boolean> hasBoardPosts = new HashMap<>();

        for (Board board : boardList) {
            String boardId = board.getBoardId();

            // 해당 게시판에 postAct == 1인 게시물이 하나라도 있는지 확인
            boolean exists = realPostsList.stream()
                    .anyMatch(post -> post.getBoard().getBoardId().equals(boardId));

            hasBoardPosts.put(boardId, exists);
        }

        // 최신순 5개 게시물 추출
        List<Posts> top5Posts = realPostsList.stream()
                .sorted(Comparator.comparing(Posts::getCreatedAt).reversed())
                .limit(5)
                .collect(Collectors.toList());

        model.addAttribute("hasBoardPosts", hasBoardPosts);
        model.addAttribute("top5Posts", top5Posts);

        return "board/main-board";
    }


    //게시판 경로 처리
    @GetMapping("/{boardId}/view")
    public String dispatchBoardPost(@PathVariable String boardId, Model model) {

        // 1. 게시판 정보 조회
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 게시판을 찾을 수 없습니다."));

        // 2. 게시글 목록 조회. 단, postAct가 1인 게시물만.
        List<Posts> tempPostList = postsRepository.findByBoard_BoardId(boardId);
        List<Posts> postList = new ArrayList<>();

        //boolean isPostsListEmpty = postsList.isEmpty();
        for(int i = 0; i < tempPostList.size(); i++) {
            if(tempPostList.get(i).getPostAct() != 0)
                postList.add(tempPostList.get(i));
        }

        boolean hasPosts = postList.isEmpty();
        if(!hasPosts) {
            model.addAttribute("postList", postList);
        }

        // 3. 모델에 데이터 추가
        model.addAttribute("board", board);  // Optional이 아닌 실제 객체로 넘김
        model.addAttribute("checkPostsList", hasPosts); // Thymeleaf에서 게시물 유무 표시용

        return "board/common/post-list";
    }


    //글 작성 페이지 경로 처리
    @GetMapping("/new")
    public String showPostPage(@RequestParam("boardId") String boardId, Model model) {
        model.addAttribute("boardId", boardId);

        return "board/common/post-form";
    }


    // GlobalExceptionHandler
    @ExceptionHandler(PostsNotFoundException.class)
    public String handlePostNotFound(PostsNotFoundException ex, Model model) {
        model.addAttribute("errorMessage", "해당 게시글이 존재하지 않습니다.");
        model.addAttribute("errorCode", "ERR-POST-404");
        return "error/post-not-found"; // 사용자 친화적인 에러 페이지
    }


    // /boards/{boardId}/{postsId}/view
    @GetMapping("/{boardId}/{postId}/view")
    public String showPostsViewPage(@PathVariable("boardId") String boardId,
                                    @PathVariable("postId") Long postId,Model model,
                                    HttpServletRequest request){

        //게시물이 보여서 링크타고 들어오면 당연히 게시물 데이터가 존재함.
        //근데 만약 없는 경우? 이건 내부적으로 예외처리를 해야한다.
        //게시물 postAct = 1 일 때만 게시물 보여준다.

        Posts posts = postsRepository.findByBoard_BoardIdAndPostId(boardId, postId)
                .orElseThrow(() -> new PostsNotFoundException(postId));


        //해당 글이 로그인한 본인이 쓴 글이면 true 아니면 false를 보낸다.
        //이를 통해 수정 및 삭제 버튼이 보여지게 된다.
        // 1. JWT 쿠키 추출
        try{
            Cookie[] cookies = request.getCookies();
            String jwt = null;

            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("jwt".equals(cookie.getName())) {
                        jwt = cookie.getValue();
                        break;
                    }
                }
            }

            // 2. JWT 존재 여부 확인
            if (jwt == null || !jwtProvider.validateToken(jwt)) {
                throw new AuthenticationCredentialsNotFoundException("JWT 토큰이 없거나 유효하지 않습니다.");
            }

            // 3. JWT에서 사용자 ID 추출
            String authUserId = jwtProvider.getUserId(jwt);

            // 4. 사용자 조회
            User user = userRepository.findByUserAuth_AuthUserId(authUserId)
                    .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

            // 5. 탈퇴한 회원인지 확인 (mb_act == 0)
            if (user.getMbAct() == 0) {
                throw new AuthenticationCredentialsNotFoundException("계정이 존재하지 않거나, 탈퇴한 회원입니다.");
            }

            Optional<Posts> optionalPosts = postsRepository.findPostsByUserAuth(authUserId, boardId);
            boolean isLogin = false;
            if(optionalPosts.isPresent()) {
                isLogin = true;
            }

            //View 에다가 데이터 전송
            model.addAttribute("postsData", posts);

            //로그인 여부 Model에 넣기
            model.addAttribute("isAuthor", isLogin);
        }catch (AuthenticationCredentialsNotFoundException e) {
            e.printStackTrace();
        }

        return "board/common/post-detail";
    }

    @GetMapping("/posts/edit")
    public String showPostsEditPage(@RequestParam("postId") Long postId,@RequestParam("boardId") String boardId ,Model model) {

        Optional<Posts> postsData = postsRepository.findByBoard_BoardIdAndPostId(boardId, postId);

        if (postsData.isPresent()) {
            model.addAttribute("postData", postsData.get());
        }

        return "/board/common/post-edit-form";
    }


}
