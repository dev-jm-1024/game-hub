package kr.plusb3b.games.gamehub.application.admin.viewmodel;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import kr.plusb3b.games.gamehub.domain.board.dto.CreateNoticeDto;
import kr.plusb3b.games.gamehub.domain.board.dto.UpdateNoticeDto;
import kr.plusb3b.games.gamehub.domain.board.entity.Board;
import kr.plusb3b.games.gamehub.domain.board.entity.Posts;
import kr.plusb3b.games.gamehub.domain.board.repository.BoardRepository;
import kr.plusb3b.games.gamehub.domain.board.repository.PostFilesRepository;
import kr.plusb3b.games.gamehub.domain.board.repository.PostsRepository;
import kr.plusb3b.games.gamehub.domain.board.service.PostFilesService;
import kr.plusb3b.games.gamehub.domain.board.service.admin.AdminPostsService;
import kr.plusb3b.games.gamehub.domain.board.vo.CreatePostsVO;
import kr.plusb3b.games.gamehub.domain.board.vo.business.PostContent;
import kr.plusb3b.games.gamehub.domain.board.vo.business.PostTitle;
import kr.plusb3b.games.gamehub.domain.user.entity.User;
import kr.plusb3b.games.gamehub.security.AccessControlService;
import kr.plusb3b.games.gamehub.upload.FileUpload;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.Map;

@Service
public class AdminPostsServiceImpl implements AdminPostsService {

    private final PostsRepository postsRepo;
    private final BoardRepository boardRepo;
    private final AccessControlService access;
    private final PostFilesService postFilesService;
    private final FileUpload fileUpload;
    private final PostFilesRepository postFilesRepo;

    public AdminPostsServiceImpl(PostsRepository postsRepo, BoardRepository boardRepo,
                                 AccessControlService access, PostFilesService postFilesService,
                                 FileUpload fileUpload, PostFilesRepository postFilesRepo) {
        this.postsRepo = postsRepo;
        this.boardRepo = boardRepo;
        this.access = access;
        this.postFilesService = postFilesService;
        this.fileUpload = fileUpload;
        this.postFilesRepo = postFilesRepo;
    }

    @Override
    public Posts createNotice(CreateNoticeDto createNoticeDto, String boardId, HttpServletRequest request) {
        Board board = boardRepo.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시판이 존재하지 않습니다."));

        CreatePostsVO createPostsVO = new CreatePostsVO();
        User user = access.getAuthenticatedUser(request);

        return postsRepo.save( new Posts(
                        board,
                        user,
                        PostTitle.of(createNoticeDto.getPostTitle()),
                        PostContent.of(createNoticeDto.getPostContent()),
                        createPostsVO.getViewCount(),
                        LocalDate.now(),
                        createPostsVO.getUpdatedAt(),
                        createPostsVO.getPostAct(),
                        createNoticeDto.getImportantAct()
                )
        );
    }

    @Override
    @Transactional
    public Posts updateNotice(UpdateNoticeDto dto) {

        // 1. 엔티티 조회
        Posts post = postsRepo.findById(dto.getPostId())
                .orElseThrow(() -> new EntityNotFoundException("게시물을 찾을 수 없습니다."));

        // 2. 값 변경 (Dirty Checking)
        post.changeTitle(PostTitle.of(dto.getPostTitle()));
        post.changeContent(PostContent.of(dto.getPostContent()));

        if (dto.getImportantAct() == 1) {
            post.changeActivateImportant();
        } else {
            post.changeDeactivateImportant();
        }
        post.setUpdatedAt(LocalDate.now());

        handleFileUpdates(post, dto);

        return post;
    }


    @Override
    public boolean deactivatePost(Long postId) {

        int deactivatePosts = postsRepo.deletePostsByPostId(postId);
        return deactivatePosts > 0;
    }

    @Override
    @Transactional
    public boolean markPostAsImportant(Long postId) {
        // 입력값 검증
        if (postId == null || postId <= 0) {
            throw new IllegalArgumentException("유효하지 않은 게시물 ID입니다.");
        }

        int result = postsRepo.updateImportantActByPostId(postId);

        // 업데이트 결과 확인
        if (result == 0) {
            throw new RuntimeException("게시물을 찾을 수 없습니다. ID: " + postId);
        }

        return result > 0;
    }

    @Override
    @Transactional
    public boolean unsetPostImportant(Long postId) {
        // 입력값 검증
        if (postId == null || postId <= 0) {
            throw new IllegalArgumentException("유효하지 않은 게시물 ID 입니다.");
        }

        int result = postsRepo.unsetImportantActByPostId(postId);

        // 업데이트 결과 확인
        if (result == 0) {
            throw new RuntimeException("게시물을 찾을 수 없습니다. ID: " + postId);
        }

        return result > 0;
    }


    private void handleFileUpdates(Posts savedPosts, UpdateNoticeDto dto) {
        boolean hasOldFiles = dto.getOldFileUrl() != null && !dto.getOldFileUrl().isEmpty();
        boolean hasNewFiles = dto.getFiles() != null && !dto.getFiles().isEmpty();

        if (!hasOldFiles && hasNewFiles) {
            // 시나리오 1: 기존 파일 모두 삭제 + 새 파일 업로드
            postFilesRepo.deletePostFilesByPostId(savedPosts.getPostId());
            Map<String, String> fileUrlMap = fileUpload.getFileUrlAndType(dto.getFiles());
            postFilesService.uploadPostFile(savedPosts, fileUrlMap);

        } else if (hasOldFiles && !hasNewFiles) {
            // 시나리오 2: 기존 파일 유지 (아무것도 안함)
            // 기존 파일은 그대로 유지

        } else if (hasOldFiles && hasNewFiles) {
            // 시나리오 3: 기존 파일 일부 유지 + 새 파일 추가
            // 제거된 파일들 삭제
            postFilesService.deleteRemovedFiles(savedPosts.getPostId(), dto.getOldFileUrl());
            // 새 파일 업로드
            Map<String, String> fileUrlMap = fileUpload.getFileUrlAndType(dto.getFiles());
            postFilesService.uploadPostFile(savedPosts, fileUrlMap);

        } else if (!hasOldFiles && !hasNewFiles) {
            // 시나리오 4: 모든 파일 삭제
            postFilesRepo.deletePostFilesByPostId(savedPosts.getPostId());
        }
    }
}
