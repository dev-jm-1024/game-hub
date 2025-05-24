package kr.plusb3b.games.gamehub.api.controller.board.rest;


import jakarta.validation.Valid;
import kr.plusb3b.games.gamehub.api.dto.board.Board;
import kr.plusb3b.games.gamehub.api.dto.board.PostFiles;
import kr.plusb3b.games.gamehub.api.dto.board.Posts;
import kr.plusb3b.games.gamehub.api.dto.board.PostsRequestDto;
import kr.plusb3b.games.gamehub.api.dto.user.User;
import kr.plusb3b.games.gamehub.api.dto.user.UserAuth;
import kr.plusb3b.games.gamehub.repository.boardrepo.BoardRepository;
import kr.plusb3b.games.gamehub.repository.boardrepo.PostFilesRepository;
import kr.plusb3b.games.gamehub.repository.boardrepo.PostsRepository;
import kr.plusb3b.games.gamehub.repository.userrepo.UserAuthRepository;
import kr.plusb3b.games.gamehub.repository.userrepo.UserRepository;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.ResponseCookie;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController("RestBoardController")
@RequestMapping(path = "/board")
public class RestBoardController {


//    @Value("${app.api.version}")
//    private String apiVersion;

    private final PostsRepository postsRepo;
    private final PostFilesRepository postFilesRepo;
    private final UserRepository userRepo;
    private final BoardRepository boardRepo;

    public RestBoardController(PostsRepository postsRepo, PostFilesRepository postFilesRepo,
                               UserAuthRepository userAuthRepo, BoardRepository boardRepo,
                               UserRepository userRepo) {
        this.postsRepo = postsRepo;
        this.postFilesRepo = postFilesRepo;
        this.userRepo = userRepo;
        this.boardRepo = boardRepo;
    }

    //게시물 작성 후 DB 삽입
    //@PostMapping("/api/version/{boardId}/new")
    @PostMapping("/api/v1/new")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> insertPosts(@Valid @ModelAttribute PostsRequestDto postsRequestDto,
                                      @AuthenticationPrincipal UserDetails userDetails) {

        String boardId = postsRequestDto.getBoardId(); //게시판 아이디
        //String authUserId = userDetails.getUsername(); // 예: "jaeminlee123"
        String authUserId = "test";
        // 2. UserRepository 통해 바로 User 조회
        User user = userRepo.findByUserAuth_AuthUserId(authUserId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 게시판 엔티티 조회
        Board board = boardRepo.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시판이 존재하지 않습니다."));

        String title = postsRequestDto.getPostTitle();
        String content = postsRequestDto.getPostContent();
        int viewCount = 0;
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = null;
        int postAct = 1;

        // Posts 엔티티 조립
        Posts post = new Posts();
        post.setUser(user); //사용자의 고유 아이디인 mb_id
        post.setBoard(board); //게시판 아이디
        post.setPostTitle(title);
        post.setPostContent(content);
        post.setViewCount(viewCount);
        post.setCreatedAt(createdAt);
        post.setUpdatedAt(updatedAt);
        post.setPostAct(postAct);

//        for(Posts posts : postsRepo.findAll()) {
//            System.out.println("posts 객체: "+posts);
//        }
        try {
            Posts savedPosts = postsRepo.save(post);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedPosts);

        } catch (IllegalArgumentException e) {
            // 사용자를 찾을 수 없음, 게시판 없음 등 → 404 Not Found
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());

        } catch (Exception e) {
            // 기타 오류 (DB 오류 등) → 500 Internal Server Error
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("게시글 저장 중 오류가 발생했습니다.");
        }

    }

    //게시물에 첨부파일 있으면 삽입 -- 미완
    @PostMapping("/{boardId}/new")
    @ResponseStatus(HttpStatus.CREATED)
    public PostFiles insertPostFiles(@RequestBody PostFiles postFiles, @PathVariable Long bordId) {
        return postFilesRepo.save(postFiles);
    }

    //화면에 게시물 데이터 뿌려주기
    @GetMapping("/api/v1/{boardId}/{postId}/view")
    public Posts getPosts(@PathVariable String boardId, @PathVariable Long postId) {
        return postsRepo.findByBoard_BoardIdAndPostId(boardId, postId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "해당 게시글을 찾을 수 없습니다."
                ));
    }


}
