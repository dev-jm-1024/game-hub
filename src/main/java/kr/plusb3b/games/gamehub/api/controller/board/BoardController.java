package kr.plusb3b.games.gamehub.api.controller.board;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import kr.plusb3b.games.gamehub.api.dto.board.*;
import kr.plusb3b.games.gamehub.api.dto.user.User;
import kr.plusb3b.games.gamehub.api.dto.user.UserAuth;
import kr.plusb3b.games.gamehub.api.jwt.JwtProvider;
import kr.plusb3b.games.gamehub.repository.boardrepo.BoardRepository;
import kr.plusb3b.games.gamehub.repository.boardrepo.PostsRepository;
import kr.plusb3b.games.gamehub.repository.userrepo.UserAuthRepository;
import kr.plusb3b.games.gamehub.repository.userrepo.UserRepository;
import kr.plusb3b.games.gamehub.security.AccessControlService;
import org.atmosphere.config.service.Post;
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
    private final UserAuthRepository userAuthRepo;
    //private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(BoardController.class);4

//    @Value("${app.max.boardSize}")
//    String maxBoardSizeString;
//    int maxBoardSize = Integer.parseInt(maxBoardSizeString);


    public BoardController(BoardRepository boardRepository, PostsRepository postsRepository,
                           JwtProvider jwtProvider, UserRepository userRepository,
                           AccessControlService access, UserAuthRepository userAuthRepo) {
        this.boardRepository = boardRepository;
        this.postsRepository = postsRepository;
        this.jwtProvider = jwtProvider;
        this.userRepository = userRepository;
        this.access = access;
        this.userAuthRepo = userAuthRepo;
    }

    @GetMapping
    public String boardMainPage(Model model) {

        // 게시판 목록 가져오기
        List<Board> boardList = boardRepository.findAll();
        model.addAttribute("boardList", boardList);

        // 게시판별로 게시물 분류 및 필터링/정렬
        Map<String, List<Posts>> postsByBoard = new HashMap<>();

        for (Board board : boardList) {
            String boardId = board.getBoardId();

            // 해당 게시판의 전체 게시물 가져오기
            List<Posts> posts = postsRepository.findByBoard_BoardId(boardId);

            // 활성화된 게시물 중 최신순 5개만 필터링
            List<Posts> top5Posts = filterAndSortTop5ByCreatedAt(posts);

            // Map에 추가
            postsByBoard.put(boardId, top5Posts);
        }

        // View에 전달
        model.addAttribute("postsByBoard", postsByBoard);

        return "board/main-board"; // 템플릿 경로
    }



    //게시판 경로 처리
    @GetMapping("/{boardId}/view")
    public String dispatchBoardPost(@PathVariable("boardId") String boardId, Model model) {

        Board board = boardRepository.findById(boardId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        model.addAttribute("board", board);

        //boardId 디버깅 출력
        System.out.println(boardId);

        //postAct가 1인 것만 넣기
        List<Posts> allPostData = postsRepository.findByBoard_BoardId(boardId);
        List<Posts> realPostData = new ArrayList<>();
        for(int i = 0; i < allPostData.size(); i++) {
            if(allPostData.get(i).getPostAct() == 1){
                realPostData.add(allPostData.get(i));
            }
        }

        //postAct 1인 데이터 Model로 넘기기

        model.addAttribute("realPostData", realPostData);


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

        System.out.println("boardId 존재함? " + boardId);
        System.out.println("postId 존재함? " + postId);

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

            Optional<UserAuth> userAuthOpt = userAuthRepo.findByAuthUserId(authUserId);
            if(userAuthOpt.isPresent()) {
                User user = userAuthOpt.get().getUser();

                /********************로그인한 회원이 게시물 쓴건지 확인************************/
                boolean isAuthUser = false;
                if(posts.getUser().equals(user)){
                    model.addAttribute("isAuthUser", true);
                }else{
                    model.addAttribute("isAuthUser", false);
                }


                // 5. 탈퇴 여부 확인
                if (user.getMbAct() == 0) {
                    throw new IllegalStateException("탈퇴한 회원입니다.");
                }
            }else{
                throw new IllegalArgumentException("사용자를 찾을 수 없습니다");
            }


            //View 에다가 데이터 전송
            model.addAttribute("postsData", posts);


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


    private List<Posts> filterAndSortTop5ByCreatedAt(List<Posts> posts) {
        return posts.stream()
                .filter(post -> post.getPostAct() == 1) // 활성 게시글만
                .sorted(Comparator.comparing(Posts::getCreatedAt).reversed()) // 최신순 정렬
                .limit(5) // 상위 5개
                .collect(Collectors.toList());
    }



}