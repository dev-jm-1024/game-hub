package kr.plusb3b.games.gamehub.application.board.viewmodel;

import jakarta.servlet.http.HttpServletRequest;
import kr.plusb3b.games.gamehub.domain.board.entity.Board;
import kr.plusb3b.games.gamehub.domain.board.service.viewmodel.PostGamesDetailVmService;
import kr.plusb3b.games.gamehub.domain.game.dto.GameDetailDto;
import kr.plusb3b.games.gamehub.domain.game.service.GameMetadataService;
import kr.plusb3b.games.gamehub.domain.user.entity.User;
import kr.plusb3b.games.gamehub.security.AccessControlService;
import kr.plusb3b.games.gamehub.view.board.PostDetailVM;
import kr.plusb3b.games.gamehub.view.board.PostGamesDetailVM;
import org.springframework.stereotype.Service;

@Service
public class PostGamesDetailVmServiceImpl implements PostGamesDetailVmService {

    private final GameMetadataService gameMetadataService;
    private final AccessControlService access;

    public PostGamesDetailVmServiceImpl(GameMetadataService gameMetadataService, AccessControlService access) {
        this.gameMetadataService = gameMetadataService;
        this.access = access;
    }

    @Override
    public PostGamesDetailVM getPostGamesDetailVm(HttpServletRequest request, String boardId, Long gameId) {

        User user = access.getAuthenticatedUser(request);
        boolean isAdmin = user.getMbRole() == User.Role.ROLE_ADMIN;

        final String replaceProfileUrl = "https://www.shutterstock.com/image-vector/vector-flat-illustration-grayscale-avatar-600nw-2281862025.jpg";
        GameDetailDto result = gameMetadataService.getGameDetail(boardId, gameId);

        return new PostGamesDetailVM(
                user,
                isAdmin,
                user.getMbNickName().getMbNickName(),        // 올바른 닉네임
                user.getMbProfileUrl() != null ? user.getMbProfileUrl() : replaceProfileUrl, // 프로필 URL 로직
                replaceProfileUrl,           // 기본 프로필 URL
                result
        );
    }
}
