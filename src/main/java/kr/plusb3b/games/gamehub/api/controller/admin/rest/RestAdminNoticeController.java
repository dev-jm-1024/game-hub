package kr.plusb3b.games.gamehub.api.controller.admin.rest;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.*;
import jakarta.validation.Valid;
import kr.plusb3b.games.gamehub.domain.board.dto.CreateNoticeDto;
import kr.plusb3b.games.gamehub.domain.board.dto.NoticeUpdateResponse;
import kr.plusb3b.games.gamehub.domain.board.dto.UpdateNoticeDto;
import kr.plusb3b.games.gamehub.domain.board.entity.PostFiles;
import kr.plusb3b.games.gamehub.domain.board.entity.Posts;
import kr.plusb3b.games.gamehub.domain.board.service.BoardService;
import kr.plusb3b.games.gamehub.domain.board.service.PostFilesService;
import kr.plusb3b.games.gamehub.domain.board.service.PostsInteractionService;
import kr.plusb3b.games.gamehub.domain.board.service.PostsService;
import kr.plusb3b.games.gamehub.domain.board.service.admin.AdminPostsService;
import kr.plusb3b.games.gamehub.domain.board.vo.CreateNoticeVO;
import kr.plusb3b.games.gamehub.domain.board.vo.PostsReactionCountVO;
import kr.plusb3b.games.gamehub.domain.user.entity.User;
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

@RestController
@RequestMapping("/admin/api/v1")
public class RestAdminNoticeController {

    private final PostsInteractionService postsInteractionService;
    private final AccessControlService access;
    private final FileUpload fileUpload;
    private final PostFilesService postFilesService;
    private final BoardService boardService;
    private final AdminPostsService adminPostsService;


    private final String NOTICE_ID = "notice";

    public RestAdminNoticeController(PostsInteractionService postsInteractionService, AccessControlService access,
                                     FileUpload fileUpload, PostFilesService postFilesService,
                                     BoardService boardService, AdminPostsService adminPostsService) {
        this.postsInteractionService = postsInteractionService;
        this.access = access;
        this.fileUpload = fileUpload;
        this.postFilesService = postFilesService;
        this.boardService = boardService;
        this.adminPostsService = adminPostsService;
    }

    @PostMapping(value = "/notice/create",  consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createNotice(@RequestPart("data") @Valid CreateNoticeDto createNoticeDto,
                                          @RequestPart(value = "files", required = false) List<MultipartFile> files,
                                          HttpServletRequest request,
                                          HttpServletResponse response) throws IOException {

        String boardId = NOTICE_ID;
        access.validateAdminAccess(request,response);

        boolean checkBoard = boardService.validateBoard(boardId);
        if(!checkBoard) return ResponseEntity.badRequest().body("해당 게시판이 존재하지 않습니다.");
        Posts savedNotice = adminPostsService.createNotice(createNoticeDto, boardId, request);

        // 3. 게시글 반응 카운트 초기화
        postsInteractionService.savePostsReactionCount(savedNotice.getPostId(), new PostsReactionCountVO());

        // 4. 파일 업로드 처리
        if (files != null && !files.isEmpty()) {
            try {
                Map<String, String> fileUrlAndType = fileUpload.getFileUrlAndType(files);
                List<PostFiles> savedFiles = postFilesService.uploadPostFile(savedNotice, fileUrlAndType);

                if (savedFiles.isEmpty()) {
                    log.warn("파일 업로드 요청이 있었으나 저장된 파일이 없습니다 - postId: {}", savedNotice.getPostId());
                }

            } catch (Exception fe) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("파일 업로드 중 오류가 발생했습니다.");
            }
        }

        // 5. 성공 응답
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "게시글이 성공적으로 등록되었습니다.", "postId", savedNotice.getPostId()));

    }

    @PatchMapping("/notices/{postId}")
    public ResponseEntity<?> editNotice(
            @PathVariable Long postId,
            @Valid @ModelAttribute UpdateNoticeDto dto,
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        // 관리자 권한 확인
        access.validateAdminAccess(request, response);

        // PathVariable과 DTO의 postId 일치 확인
        if (!postId.equals(dto.getPostId())) {
            throw new IllegalArgumentException("잘못된 요청입니다. PathVariable과 DTO의 ID가 일치하지 않습니다.");
        }

        Posts updatedPost = adminPostsService.updateNotice(dto);

        return ResponseEntity.ok(NoticeUpdateResponse.of(updatedPost));
    }

    @PostMapping("/notice/{postId}/delete")
    public ResponseEntity<?> deleteNotice(@PathVariable("postId") Long postId, HttpServletRequest request, HttpServletResponse response) throws IOException {

        access.validateAdminAccess(request, response);

       boolean result =  adminPostsService.deactivatePost(postId);
       if(!result) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("DB Error");

       return ResponseEntity.status(HttpStatus.OK).body("성공적으로 삭제 되었습니다");
    }

    @PostMapping("/notice/{postId}/important")
    public ResponseEntity<?> importantNotice(@PathVariable("postId") Long postId, HttpServletRequest request, HttpServletResponse response) throws IOException {

        access.validateAdminAccess(request, response);

        boolean result = adminPostsService.markPostAsImportant(postId);
        if(!result) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("DB Error");


        return ResponseEntity.status(HttpStatus.OK).body("성공적으로 중요 공지사항으로 변경되었습니다");
    }

    @PostMapping("/notice/{postId}/unimportant")
    public ResponseEntity<?> unImportantNotice(@PathVariable("postId") Long postId, HttpServletRequest request) {

        User user = access.getAuthenticatedUser(request);
        if(user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        boolean result = adminPostsService.unsetPostImportant(postId);
        if(!result) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("DB Error");


        return ResponseEntity.status(HttpStatus.OK).body("성공적으로 중요 공지사항이 해제되었습니다");
    }
}

