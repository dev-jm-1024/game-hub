package kr.plusb3b.games.gamehub.domain.board.service;

import kr.plusb3b.games.gamehub.domain.board.dto.RequestCommentDto;
import kr.plusb3b.games.gamehub.domain.board.entity.Comments;
import kr.plusb3b.games.gamehub.domain.board.vo.CreateCommentsVO;
import kr.plusb3b.games.gamehub.domain.user.entity.User;

import java.util.List;

public interface CommentService {

    Comments createComment(CreateCommentsVO cvo, RequestCommentDto requestCommentDto, User user);

    List<Comments> getComments(String boardId, Long postId);

    boolean updateCommentContents(Long commentId, String commentContent);

}
