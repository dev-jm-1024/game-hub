package kr.plusb3b.games.gamehub.security;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import kr.plusb3b.games.gamehub.api.dto.board.Board;
import kr.plusb3b.games.gamehub.api.dto.board.Posts;
import kr.plusb3b.games.gamehub.api.dto.user.User;
import kr.plusb3b.games.gamehub.api.jwt.JwtProvider;
import kr.plusb3b.games.gamehub.repository.boardrepo.BoardRepository;
import kr.plusb3b.games.gamehub.repository.boardrepo.PostsRepository;
import kr.plusb3b.games.gamehub.repository.userrepo.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AccessControlService {

    private final UserRepository userRepo;
    private final JwtProvider jwtProvider;
    private final BoardRepository boardRepo;
    private final PostsRepository postsRepo;

    public AccessControlService(UserRepository userRepo, JwtProvider jwtProvider,
                                BoardRepository boardRepo, PostsRepository postsRepo) {
        this.userRepo = userRepo;
        this.jwtProvider = jwtProvider;
        this.boardRepo = boardRepo;
        this.postsRepo = postsRepo;
    }

    public User getAuthenticatedUser(HttpServletRequest request) {

        // 1. JWT 쿠키 추출
        Cookie[] cookies = request.getCookies();
        String jwt = null;

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("jwt".equals(cookie.getName())) {
                    jwt = cookie.getValue();
                    break;
                }
            }
        }

        // 2. JWT 존재 여부 확인
        if (jwt == null || !jwtProvider.validateToken(jwt)) {
            throw new IllegalStateException("로그인이 필요합니다.");
        }

        // 3. JWT에서 사용자 ID 추출
        String authUserId = jwtProvider.getUserId(jwt);

        // 4. 사용자 조회 --- 여기서 오류남
        User user = userRepo.findByUserAuth_AuthUserId(authUserId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 5. 탈퇴 여부 확인
        if (user.getMbAct() == 0) {
            throw new IllegalStateException("탈퇴한 회원입니다.");
        }

        return user;
    }

    public boolean validateBoardAndPost(String boardId, Long postId) {

        boolean checkBoardId = boardId.isEmpty();
        boolean checkPostId = postId == null;

        //1차로 boardId, postId 값 검사
        if(!checkBoardId && !checkPostId){

            try{
                Optional<Board> optionalBoard = boardRepo.findById(boardId);
                Optional<Posts> optionalPosts = postsRepo.findById(postId);

                if(optionalBoard.isPresent() || optionalPosts.isPresent()) return true;
                else{throw new IllegalArgumentException("게시판 혹은 게시물이 존재하지 않음");}
            }catch(IllegalArgumentException e){
                e.printStackTrace();
            }
        }

        return false;
    }
}
