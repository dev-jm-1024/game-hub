package kr.plusb3b.games.gamehub.domain.board.service;

import kr.plusb3b.games.gamehub.domain.board.entity.PostFiles;
import kr.plusb3b.games.gamehub.domain.board.entity.Posts;

import java.util.*;

public interface PostFilesService {

    List<PostFiles> uploadPostFile(Posts savedPost, Map<String, String> fileUrlAndType);

    List<PostFiles> getPostFiles(Long postId);

    void deleteRemovedFiles(Long postId, List<String> oldFileUrls);

}
