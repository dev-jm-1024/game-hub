package kr.plusb3b.games.gamehub.application.board;

import kr.plusb3b.games.gamehub.domain.board.entity.PostFiles;
import kr.plusb3b.games.gamehub.domain.board.entity.Posts;
import kr.plusb3b.games.gamehub.domain.board.repository.PostFilesRepository;
import kr.plusb3b.games.gamehub.domain.board.service.PostFilesService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class PostFilesServiceImpl implements PostFilesService {

    private final PostFilesRepository postFilesRepo;

    public PostFilesServiceImpl(PostFilesRepository postFilesRepo) {
        this.postFilesRepo = postFilesRepo;
    }

    @Override
    public List<PostFiles> uploadPostFile(Posts savedPost, Map<String, String> fileUrlAndType) {
        List<PostFiles> uploadedFiles = new ArrayList<>();

        for (Map.Entry<String, String> entry : fileUrlAndType.entrySet()) {
            PostFiles attachFiles = new PostFiles();

            attachFiles.setPost(savedPost);
            attachFiles.setFileUrl(entry.getKey());
            attachFiles.setFileType(entry.getValue());
            attachFiles.setUploadDate(LocalDate.now());

            uploadedFiles.add(postFilesRepo.save(attachFiles));
        }

        return uploadedFiles;
    }

}
