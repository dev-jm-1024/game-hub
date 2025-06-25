package kr.plusb3b.games.gamehub.api.service.Board;

import kr.plusb3b.games.gamehub.api.dto.board.Board;
import kr.plusb3b.games.gamehub.api.dto.board.CreatePostsRequestDto;
import kr.plusb3b.games.gamehub.api.dto.board.Posts;
import kr.plusb3b.games.gamehub.api.dto.user.User;
import kr.plusb3b.games.gamehub.repository.boardrepo.PostsRepository;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class PostApiService {

    private final PostsRepository postsRepo;

    public PostApiService(PostsRepository postsRepo) {
        this.postsRepo = postsRepo;
    }



    public int buildPosts(User user, Board board, CreatePostsRequestDto createPostsRequestDto) {
        try {
            // Null 체크
            if (user == null || board == null || createPostsRequestDto == null) {
                throw new IllegalArgumentException("필수 입력 값이 누락되었습니다.");
            }

            // Posts 객체 생성 및 설정
            Posts posts = new Posts();
            posts.setUser(user);
            posts.setBoard(board);
            posts.setPostTitle(createPostsRequestDto.getPostTitle());
            posts.setPostContent(createPostsRequestDto.getPostContent());
            posts.setViewCount(0);
            posts.setCreatedAt(LocalDate.now());
            posts.setUpdatedAt(null);
            posts.setPostAct(1);

            // 저장
            Posts savedPost = postsRepo.save(posts);

            // 저장 성공 여부 확인
            if (savedPost != null && savedPost.getPostId() != null) {
                return 1; // 성공
            } else {
                return 0; // 저장 실패
            }

        } catch (IllegalArgumentException e) {
            // 입력 값 문제
            System.err.println("입력 값 오류: " + e.getMessage());
            return -1;

        } catch (DataAccessException e) {
            // JPA 저장 과정에서 예외 발생
            System.err.println("데이터베이스 오류: " + e.getMessage());
            return -2;

        } catch (Exception e) {
            // 기타 예외
            System.err.println("알 수 없는 오류: " + e.getMessage());
            return -3;
        }
    }


}
