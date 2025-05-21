package kr.plusb3b.games.gamehub.api.dto.board;

public class PostsNotFoundException extends RuntimeException {

    public PostsNotFoundException(Long postsId) {
        super("게시글을 찾을 수 없습니다. ID: " + postsId);
    }

    // 필요하다면 다른 생성자도 추가 가능
    public PostsNotFoundException(String message) {
        super(message);
    }

    public PostsNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}

