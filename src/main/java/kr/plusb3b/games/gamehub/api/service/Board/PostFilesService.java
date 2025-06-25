package kr.plusb3b.games.gamehub.api.service.Board;

import kr.plusb3b.games.gamehub.api.dto.board.PostFiles;
import kr.plusb3b.games.gamehub.repository.boardrepo.PostFilesRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PostFilesService {

    private final PostFilesRepository postFilesRepo;

    public PostFilesService(PostFilesRepository postFilesRepo) {
        this.postFilesRepo = postFilesRepo;
    }

    //해당 게시물의 첨부파일 가져오기
    //만약 첨부파일이 없으면?
    public List<PostFiles> getPostAttachments(Long postId){

        Optional<PostFiles> postFilesOpt = postFilesRepo.findPostFilesByPost_PostId(postId);
        List<PostFiles> result = new ArrayList<>();

        if(postFilesOpt.isPresent()){
            PostFiles postFiles = postFilesOpt.get();
            result.add(postFiles);

            return result;
        }

        return null;
    }
}
