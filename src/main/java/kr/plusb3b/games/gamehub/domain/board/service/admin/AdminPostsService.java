package kr.plusb3b.games.gamehub.domain.board.service.admin;

import jakarta.servlet.http.HttpServletRequest;
import kr.plusb3b.games.gamehub.domain.board.dto.CreateNoticeDto;
import kr.plusb3b.games.gamehub.domain.board.dto.UpdateNoticeDto;
import kr.plusb3b.games.gamehub.domain.board.entity.Posts;


public interface AdminPostsService {

    Posts createNotice(CreateNoticeDto createNoticeDto, String boardId, HttpServletRequest request);

    Posts updateNotice(UpdateNoticeDto updateNoticeDto);

    // 삭제 - 사용자, 관리자
    boolean deactivatePost(Long postId);

    //게시물 중요도 활성화 - 공지사항: 관리자
    boolean markPostAsImportant(Long postId);

    //게시물 중요도 비활성화 - 공지사항: 관리자
    boolean unsetPostImportant(Long postId);

}