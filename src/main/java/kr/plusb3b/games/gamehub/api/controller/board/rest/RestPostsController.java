package kr.plusb3b.games.gamehub.api.controller.board.rest;


import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import kr.plusb3b.games.gamehub.domain.board.entity.PostsReactionCount;
import kr.plusb3b.games.gamehub.domain.board.repository.PostsReactionCountRepository;
import kr.plusb3b.games.gamehub.domain.board.service.BoardService;
import kr.plusb3b.games.gamehub.domain.board.service.PostFilesService;
import kr.plusb3b.games.gamehub.domain.board.service.PostsInteractionService;
import kr.plusb3b.games.gamehub.domain.board.service.PostsService;
import kr.plusb3b.games.gamehub.domain.board.vo.PostsReactionCountVO;
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.reflections.Reflections.log;

@RestController("RestBoardController")
@RequestMapping(path="/api/v1/board") // 경로 수정 ver
public class RestPostsController {


//    @Value("${app.api.version}")
//    private String apiVersion;
    private final PostsInteractionService postsInteractionService;
    private final AccessControlService access;
    private final FileUpload fileUpload;

    private final PostsService postsService;
    private final PostFilesService postFilesService;


    public RestPostsController(PostsInteractionService postsInteractionService,
                               AccessControlService access, FileUpload fileUpload,
                               PostsService postsService, PostFilesService postFilesService) {
        this.postsInteractionService = postsInteractionService;
        this.access = access;
        this.fileUpload = fileUpload;
        this.postsService = postsService;
        this.postFilesService = postFilesService;
    }

    @PostMapping(value = "/{boardId}/posts", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> insertPosts(@PathVariable("boardId") String boardId,
                                         @RequestPart("data") @Valid PostRequestDto postRequestDto,
                                         @RequestPart(value = "files", required = false) List<MultipartFile> files,
                                         HttpServletRequest request,
                                         HttpServletResponse response) throws Exception {

        try {

            access.validateAdminAccess(request,response);
            User user = access.getAuthenticatedUser(request);

            // 2. 게시물 생성
            Posts savedPost = postsService.createPost(postRequestDto, boardId, request);
            //log.info("게시글 등록 성공 - postId: {}, 작성자: {}", savedPost.getPostId(), user.getMbNickName());

            // 3. 게시글 반응 카운트 초기화
            postsInteractionService.savePostsReactionCount(savedPost.getPostId(), new PostsReactionCountVO());

            // 4. 파일 업로드 처리
            Map<String, String> fileUrlAndType = fileUpload.getFileUrlAndType(files);
            List<PostFiles> savedFiles = postFilesService.uploadPostFile(savedPost, fileUrlAndType);

            if (savedFiles.isEmpty()) {
                log.warn("파일 업로드 요청이 있었으나 저장된 파일이 없습니다 - postId: {}", savedPost.getPostId());
            }
//
//            if (files != null && !files.isEmpty()) {
//                try {
//
//
//                } catch (Exception fe) {
//                    log.error("파일 업로드 중 오류 - postId: {}", savedPost.getPostId(), fe);
//                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("파일 업로드 중 오류가 발생했습니다.");
//                }
//            }

            // 5. 성공 응답
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("message", "게시글이 성공적으로 등록되었습니다.", "postId", savedPost.getPostId()));

        } catch (IllegalArgumentException e) {
            log.warn("잘못된 게시글 등록 요청 - {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("요청 오류: " + e.getMessage());

        } catch (Exception e) {
            log.error("게시글 등록 중 서버 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("게시글 등록 중 서버 오류가 발생했습니다.");
        }
    }


    @PatchMapping("/{boardId}/posts/{postId}")
    public ResponseEntity<?> updatePosts(@PathVariable("boardId") String boardId, @PathVariable("postId") Long postId,
            @Valid @RequestBody PostRequestDto postRequestDto, HttpServletRequest request, HttpServletResponse response) {

        try {

            access.validateAdminAccess(request, response);
            boolean updateResult = postsService.updatePost(postRequestDto, boardId, postId);

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
    public ResponseEntity<?> deactivatePosts(@PathVariable("boardId") String boardId,
                                             @PathVariable("postId") Long postId,
                                             HttpServletRequest request,
                                             HttpServletResponse response) throws IOException {
        access.validateAdminAccess(request, response);

        boolean deactivatePosts = postsService.deactivatePost(boardId, postId);

        if(!deactivatePosts){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("게시물이 삭제가 실패되었습니다");
        }

        return ResponseEntity.status(HttpStatus.OK).body("게시물이 성공적으로 삭제되었습니다");

    }



}