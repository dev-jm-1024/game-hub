package kr.plusb3b.games.gamehub.api.service.Board;

import kr.plusb3b.games.gamehub.api.dto.board.PostFiles;
import kr.plusb3b.games.gamehub.api.dto.board.Posts;
import kr.plusb3b.games.gamehub.repository.boardrepo.PostFilesRepository;
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
