package kr.plusb3b.games.gamehub.api.controller.board.rest;


import jakarta.validation.Valid;
import kr.plusb3b.games.gamehub.domain.user.entity.User;
import kr.plusb3b.games.gamehub.application.board.BoardServiceImpl;
import kr.plusb3b.games.gamehub.application.board.PostFilesServiceImpl;
import kr.plusb3b.games.gamehub.application.board.PostsServiceImpl;
import kr.plusb3b.games.gamehub.domain.board.vo.CreatePostsVO;
import kr.plusb3b.games.gamehub.domain.board.entity.PostFiles;
import kr.plusb3b.games.gamehub.domain.board.dto.PostRequestDto;
import kr.plusb3b.games.gamehub.domain.board.entity.Posts;
import kr.plusb3b.games.gamehub.domain.board.repository.PostsRepository;
import jakarta.servlet.http.HttpServletRequest;
import kr.plusb3b.games.gamehub.security.AccessControlService;
import kr.plusb3b.games.gamehub.upload.FileUpload;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static org.reflections.Reflections.log;

@RestController("RestBoardController")
@RequestMapping(path="/api/v1/board") // 경로 수정 ver
public class RestBoardController {


//    @Value("${app.api.version}")
//    private String apiVersion;

    private final PostsRepository postsRepo;
    private final AccessControlService access;
    private final FileUpload fileUpload;
    private final PostsServiceImpl postsServiceImpl;
    private final PostFilesServiceImpl postFilesServiceImpl;
    private final BoardServiceImpl boardServiceImpl;

    public RestBoardController(PostsRepository postsRepo, AccessControlService access,
                               FileUpload fileUpload, PostsServiceImpl postsServiceImpl,
                               PostFilesServiceImpl postFilesServiceImpl, BoardServiceImpl boardServiceImpl) {
        this.postsRepo = postsRepo;
        this.access = access;
        this.fileUpload = fileUpload;
        this.postsServiceImpl = postsServiceImpl;
        this.postFilesServiceImpl = postFilesServiceImpl;
        this.boardServiceImpl = boardServiceImpl;
    }

    @PostMapping("/{boardId}/posts")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> insertPosts(@PathVariable("boardId") String boardId,
                                         @ModelAttribute PostRequestDto postRequestDto,
                                         HttpServletRequest request) {
        try {
            // 1. 사용자 인증
            User user = access.getAuthenticatedUser(request);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
            }

            // 2. 게시물 생성 (기본값 포함)
            CreatePostsVO defaultPostValues = new CreatePostsVO(); // viewCount=0, postAct=1 등
            Posts savedPost = postsServiceImpl.createPost(postRequestDto, defaultPostValues, boardId, request);

            // 3. 첨부파일 업로드 및 저장
            Map<String, String> fileUrlAndType = fileUpload.getFileUrlAndType(postRequestDto.getFiles());
            List<PostFiles> savedFiles = postFilesServiceImpl.uploadPostFile(savedPost, fileUrlAndType);

            if (savedFiles.isEmpty()) {
                throw new IllegalStateException("첨부파일이 하나도 저장되지 않았습니다.");
            }

            // 4. 응답 반환
            return ResponseEntity.status(HttpStatus.CREATED).body("게시글이 성공적으로 등록되었습니다.");

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("요청 처리 중 오류: " + e.getMessage());

        } catch (Exception e) {
            log.error("게시글 저장 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("게시글 저장 중 서버 오류가 발생했습니다.");
        }
    }


    @PatchMapping("/{boardId}/posts/{postId}")
    public ResponseEntity<?> updatePosts(@PathVariable("boardId") String boardId, @PathVariable("postId") Long postId,
            @Valid @RequestBody PostRequestDto postRequestDto, HttpServletRequest request) {

        try {

            User user = access.getAuthenticatedUser(request);
            if(user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

            boolean existsPost = postsServiceImpl.validatePost(postId);
            boolean existBoard = boardServiceImpl.validateBoard(boardId);

            if (!existBoard || !existsPost) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("게시판 또는 게시물이 존재하지 않습니다.");
            }


            boolean updateResult = postsServiceImpl.updatePost(postRequestDto, boardId, postId);

            if (updateResult) {
                return ResponseEntity.status(HttpStatus.OK).body("정상적으로 업데이트 되었습니다!");
            }
            return ResponseEntity.status(HttpStatus.CONFLICT).body("업데이트에 실패하였습니다");
        }catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            log.error("게시글 업데이트 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("게시글 업데이트 중 오류가 발생했습니다.");
        }
    }

    @PatchMapping("/{boardId}/posts/{postId}/deactivate")
    public ResponseEntity<?> deactivatePosts(@PathVariable("boardId") String boardId,@PathVariable("postId") Long postId, HttpServletRequest request){


        try {
            //1 ~ 5
            User user = access.getAuthenticatedUser(request);
            if(user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

            boolean existsPost = postsServiceImpl.validatePost(postId);
            boolean existBoard = boardServiceImpl.validateBoard(boardId);

            if (!existBoard || !existsPost) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("게시판 또는 게시물이 존재하지 않습니다.");
            }

            //8. 해당 게시물을 비활성화 시키기
            //int deactivatePosts = postsRepo.deletePostsByPostId(postId);
            boolean deactivatePosts = postsServiceImpl.deactivatePost(postId);

            if(deactivatePosts){
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