package kr.plusb3b.games.gamehub.api.service.Board;

import java.util.*;
import java.util.stream.Collectors;

import kr.plusb3b.games.gamehub.api.dto.board.*;
import kr.plusb3b.games.gamehub.repository.boardrepo.BoardRepository;
import kr.plusb3b.games.gamehub.repository.boardrepo.PostsRepository;
import org.springframework.stereotype.Service;

import static java.util.stream.Collectors.toList;

@Service
public class PostService {

    private final PostsRepository postsRepo;
    private final BoardRepository boardRepo;

    public PostService(PostsRepository postsRepo, BoardRepository boardRepo) {
        this.postsRepo = postsRepo;
        this.boardRepo = boardRepo;
    }

    //각각의 게시판에 최신순으로 5개만 반환하는 메소드
    public Map<String, List<Posts>> getLatestFivePosts(){

        Map<String, List<Posts>> latestPosts = new HashMap<>();

        //Board(게시판) 데이터 가져오기
        List<Board> boardAll = boardRepo.findAll();
        List<String> boardIdList = boardAll.stream()
                .map(Board::getBoardId)
                .collect(toList());

        for(int i = 0; i<boardIdList.size(); i++){

            //게시판 아이디 데이터 하나씩 가져오기

            //게시판 아이디를 이용해 해당 게시물 가져오기
            List<Posts> postsList = postsRepo.findByBoard_BoardId(boardIdList.get(i));

            //Stream을 이용해 postAct가 1인 것 필터링
            //그리고 날짜 기준으로 내림차순 정렬
            postsList.stream()
                    .filter(Posts::isActivatePosts)
                    .sorted(Comparator.comparing(Posts::getCreatedAt).reversed())
                    .collect(Collectors.toList());

            latestPosts.put(boardIdList.get(i), postsList);
        }

        return latestPosts;
    }

    //해당 게시판의 postAct의 값이 1인 게시물들 반환
    public List<Posts> activatePosts(String boardId){

        //게시판 아이디, 게시물 아이디를 통해 Posts 얻기
        List<Posts> postsList = postsRepo.findByBoard_BoardId(boardId);

        // createdAt 기준 내림차순 정렬
        postsList.sort((p1, p2) -> p2.getCreatedAt().compareTo(p1.getCreatedAt()));

        List<Posts> result = postsList.stream()
                .filter(Posts::isActivatePosts)
                .collect(toList());

        return result;
    }

    //해당 게시물이 사용자가 작성한 것과 같은 지 비교
    public boolean equalWriter(Long postId, String authUserId){

        boolean result = false;
        Optional<Posts> postsList = postsRepo.findById(postId);

        if(postsList.isPresent()){
            Posts posts = postsList.get();

            if(authUserId.equals(posts.getUser().getUserAuth().getAuthUserId())){
                result = true;
            }else{return false;}
        }else{return false;}

        return result;
    }

}
