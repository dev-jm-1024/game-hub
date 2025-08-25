package kr.plusb3b.games.gamehub.api.controller.admin.rest;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.*;
import jakarta.validation.Valid;
import kr.plusb3b.games.gamehub.domain.board.dto.CreateNoticeDto;
import kr.plusb3b.games.gamehub.domain.board.dto.UpdateNoticeDto;
import kr.plusb3b.games.gamehub.domain.board.entity.PostFiles;
import kr.plusb3b.games.gamehub.domain.board.entity.Posts;
import kr.plusb3b.games.gamehub.domain.board.service.BoardService;
import kr.plusb3b.games.gamehub.domain.board.service.PostFilesService;
import kr.plusb3b.games.gamehub.domain.board.service.PostsInteractionService;
import kr.plusb3b.games.gamehub.domain.board.service.PostsService;
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

import java.util.List;
import java.util.Map;

import static org.reflections.Reflections.log;

@RestController
@RequestMapping("/admin/api/v1")
public class RestAdminNoticeController {

    private final PostsInteractionService postsInteractionService;
    private final AccessControlService access;
    private final FileUpload fileUpload;
    private final PostsService postsService;
    private final PostFilesService postFilesService;
    private final BoardService boardService;


    private final String NOTICE_ID = "notice";

    public RestAdminNoticeController(PostsInteractionService postsInteractionService, AccessControlService access, FileUpload fileUpload,
                                     PostsService postsService, PostFilesService postFilesService,
                                     BoardService boardService) {
        this.postsInteractionService = postsInteractionService;
        this.access = access;
        this.fileUpload = fileUpload;
        this.postsService = postsService;
        this.postFilesService = postFilesService;
        this.boardService = boardService;
    }

    @PostMapping(value = "/notice/create",  consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createNotice(@RequestPart("data") @Valid CreateNoticeDto createNoticeDto,
                                          @RequestPart(value = "files", required = false) List<MultipartFile> files,
                                          HttpServletRequest request) {

        String boardId = NOTICE_ID;

        try{

            User user = access.getAuthenticatedUser(request);
            if (user == null) {
                log.warn("비인증 사용자 게시글 등록 시도 - boardId: {}", boardId);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
            }

            //게시물 생성
            CreateNoticeVO defaultNoticeValues = new CreateNoticeVO();
            Posts savedNotice = postsService.createNotice(createNoticeDto, defaultNoticeValues, boardId, request);

            log.info("게시글 등록 성공 - postId: {}, 작성자: {}", savedNotice.getPostId(), user.getMbNickname());

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
                    log.error("파일 업로드 중 오류 - postId: {}", savedNotice.getPostId(), fe);
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("파일 업로드 중 오류가 발생했습니다.");
                }
            }

            // 5. 성공 응답
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("message", "게시글이 성공적으로 등록되었습니다.", "postId", savedNotice.getPostId()));
        } catch (IllegalArgumentException e) {
            log.warn("잘못된 게시글 등록 요청 - {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("요청 오류: " + e.getMessage());

        } catch (Exception e) {
            log.error("게시글 등록 중 서버 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("게시글 등록 중 서버 오류가 발생했습니다.");
        }

    }

    //공지사항 편집
    @PostMapping("/notice/{postId}/edit")
    public ResponseEntity<?> editNotice(
            @PathVariable("postId") Long postId,
            @ModelAttribute UpdateNoticeDto updateNoticeDto,
            HttpServletRequest request
    ) {
        try {
            User user = access.getAuthenticatedUser(request);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("인증이 필요합니다.");
            }

            // PathVariable과 DTO의 postId 일치 확인
            if (!postId.equals(updateNoticeDto.getPostId())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("잘못된 요청입니다.");
            }

            // 체크박스 처리 - 새로운 DTO 생성
            UpdateNoticeDto processedDto = new UpdateNoticeDto(
                    updateNoticeDto.getPostId(),
                    updateNoticeDto.getPostTitle(),
                    updateNoticeDto.getPostContent(),
                    updateNoticeDto.getImportantAct() != null ? updateNoticeDto.getImportantAct() : 0, // null이면 0으로 설정
                    updateNoticeDto.getOldFileUrl(),
                    updateNoticeDto.getFiles()
            );

            // 공지사항 업데이트 실행
            Posts updatedPost = postsService.updateNotice(processedDto);

            if (updatedPost != null) {
                return ResponseEntity.status(HttpStatus.OK).body("공지사항이 정상적으로 수정되었습니다!");
            } else {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("공지사항 수정에 실패했습니다.");
            }

        } catch (IllegalArgumentException e) {
            log.error("공지사항 수정 중 잘못된 파라미터: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (EntityNotFoundException e) {
            log.error("공지사항을 찾을 수 없음: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 공지사항을 찾을 수 없습니다.");
        } catch (Exception e) {
            log.error("공지사항 수정 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("공지사항 수정 중 오류가 발생했습니다.");
        }
    }

    @PostMapping("/notice/{postId}/delete")
    public ResponseEntity<?> deleteNotice(@PathVariable("postId") Long postId, HttpServletRequest request) {

       User user = access.getAuthenticatedUser(request);
       if(user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

       boolean result =  postsService.deactivatePost(postId);
       if(!result) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("DB Error");

       return ResponseEntity.status(HttpStatus.OK).body("성공적으로 삭제 되었습니다");
    }

    @PostMapping("/notice/{postId}/important")
    public ResponseEntity<?> importantNotice(@PathVariable("postId") Long postId, HttpServletRequest request) {

        User user = access.getAuthenticatedUser(request);
        if(user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        boolean result = postsService.markPostAsImportant(postId);
        if(!result) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("DB Error");


        return ResponseEntity.status(HttpStatus.OK).body("성공적으로 중요 공지사항으로 변경되었습니다");
    }

    @PostMapping("/notice/{postId}/unimportant")
    public ResponseEntity<?> unImportantNotice(@PathVariable("postId") Long postId, HttpServletRequest request) {

        User user = access.getAuthenticatedUser(request);
        if(user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        boolean result = postsService.unsetPostImportant(postId);
        if(!result) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("DB Error");


        return ResponseEntity.status(HttpStatus.OK).body("성공적으로 중요 공지사항이 해제되었습니다");
    }
}

