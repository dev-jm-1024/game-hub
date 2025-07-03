package kr.plusb3b.games.gamehub.api.service.Board;

import kr.plusb3b.games.gamehub.api.dto.board.PostFiles;
import kr.plusb3b.games.gamehub.api.dto.board.Posts;

import java.util.*;

public interface PostFilesService {

    List<PostFiles> uploadPostFile(Posts savedPost, Map<String, String> fileUrlAndType);

}
