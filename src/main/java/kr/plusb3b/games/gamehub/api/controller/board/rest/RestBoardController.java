package kr.plusb3b.games.gamehub.api.controller.board.rest;


import jakarta.validation.Valid;
import kr.plusb3b.games.gamehub.api.dto.board.Board;
import kr.plusb3b.games.gamehub.api.dto.board.Posts;
import kr.plusb3b.games.gamehub.api.dto.board.CreatePostsRequestDto;
import kr.plusb3b.games.gamehub.api.dto.board.UpdatePostsRequestDto;
import kr.plusb3b.games.gamehub.api.dto.user.User;
import kr.plusb3b.games.gamehub.api.jwt.JwtProvider;
import kr.plusb3b.games.gamehub.repository.boardrepo.BoardRepository;
import kr.plusb3b.games.gamehub.repository.boardrepo.PostsRepository;
import kr.plusb3b.games.gamehub.repository.userrepo.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import kr.plusb3b.games.gamehub.security.AccessControlService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;

@RestController("RestBoardController")
//@RequestMapping(path = "/board")
@RequestMapping(path="/api/v1/board") // 경로 수정 ver
public class RestBoardController {


//    @Value("${app.api.version}")
//    private String apiVersion;

    private final PostsRepository postsRepo;
    private final UserRepository userRepo;
    private final BoardRepository boardRepo;
    private final JwtProvider jwtProvider;
    private final AccessControlService access;

    public RestBoardController(PostsRepository postsRepo, BoardRepository boardRepo,
                               UserRepository userRepo, JwtProvider jwtProvider,
                               AccessControlService access) {
        this.postsRepo = postsRepo;
        this.userRepo = userRepo;
        this.boardRepo = boardRepo;
        this.jwtProvider = jwtProvider;
        this.access = access;
    }

    @PostMapping("/{boardId}/posts") // 수정 ver
    //@PostMapping("/api/v1/new")
    @ResponseStatus(HttpStatus.CREATED)//(@PathVariable("boardId") String boardId , @RequestBody CreatePostsRequestDto createPostsRequestDto, HttpServletRequest request)
    public ResponseEntity<?> insertPosts(@PathVariable("boardId") String boardId , @RequestBody CreatePostsRequestDto createPostsRequestDto,
                                         HttpServletRequest request) {
        try {
            //1 ~ 5
            User user = access.getAuthenticatedUser(request);
            if(user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

            // 6. 게시판 엔티티 조회
            //String boardId = createPostsRequestDto.getBoardId(); //이 부분 지워져야함.
            Board board = boardRepo.findById(boardId)
                    .orElseThrow(() -> new IllegalArgumentException("해당 게시판이 존재하지 않습니다."));

            // 7. Posts 엔티티 조립 및 저장
            Posts post = new Posts();
            post.setUser(user);
            post.setBoard(board);
            post.setPostTitle(createPostsRequestDto.getPostTitle());
            post.setPostContent(createPostsRequestDto.getPostContent());
            post.setViewCount(0);
            post.setCreatedAt(LocalDate.now());
            post.setUpdatedAt(null);
            post.setPostAct(1);

            Posts savedPost = postsRepo.save(post);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedPost);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("게시글 저장 중 오류가 발생했습니다.");
        }
    }

    @PatchMapping("/{boardId}/posts/{postId}")
    //@PatchMapping("/api/v1/edit")//(@PathVariable("boardId") String boardId, @PathVariable("postId") Long postId, ....)
    public ResponseEntity<?> updatePosts(@PathVariable("boardId") String boardId, @PathVariable("postId") Long postId,
            @Valid @RequestBody UpdatePostsRequestDto updatePostsRequestDto, HttpServletRequest request) {

        try {
            //1 ~ 5
            User user = access.getAuthenticatedUser(request);
            if(user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

            boolean checkBoardAndPosts = access.validateBoardAndPost(boardId, postId);

            if(!checkBoardAndPosts) throw new RuntimeException("알 수 없는 오류");

            //8. UpdatePostsRequestDto 조립
            //updatedAt 시간
            LocalDate updatedAt = LocalDate.now();
            int updateResult = postsRepo.updatePostsByPostIdAndBoardId(
                    updatePostsRequestDto.getPostTitle(),
                    updatePostsRequestDto.getPostContent(),
                    updatedAt,
                    updatePostsRequestDto.getPostId(),
                    updatePostsRequestDto.getBoardId()
            );

            if (updateResult > 0) {
                return ResponseEntity.status(HttpStatus.OK).body("정상적으로 업데이트 되었습니다!");
            }
            return ResponseEntity.status(HttpStatus.CONFLICT).body("업데이트에 실패하였습니다");
        }catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("게시글 업데이트 중 오류가 발생했습니다.");
        }
    }

    @PatchMapping("/{boardId}/posts/{postId}/deactivate")
   // @PatchMapping("api/v1/delete/{boardId}/{postId}")
    public ResponseEntity<?> deactivatePosts(@PathVariable("boardId") String boardId,@PathVariable("postId") Long postId, HttpServletRequest request){


        try {
            //1 ~ 5
            User user = access.getAuthenticatedUser(request);
            if(user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            boolean checkBoardAndPosts = access.validateBoardAndPost(boardId, postId);

            if(!checkBoardAndPosts) throw new RuntimeException("알 수 없는 오류");

            //8. 해당 게시물을 비활성화 시키기
            int deactivatePosts = postsRepo.deletePostsByPostId(postId);

            if(deactivatePosts > 0){
                return ResponseEntity.status(HttpStatus.OK).body("게시물이 성공적으로 삭제 되었습니다");
            }

        }catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("게시글 삭제 중 오류가 발생했습니다.");
        }

        return ResponseEntity.status(HttpStatus.CONFLICT).body("게시글 삭제 중 알 수 없는 오류가 발생했습니다.");

    }



}