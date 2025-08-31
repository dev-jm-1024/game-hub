package kr.plusb3b.games.gamehub.view.admin;

import kr.plusb3b.games.gamehub.domain.board.entity.*;

import java.util.List;

public record AdminPostFilesVM(
        Board board,
        Posts posts,
        List<PostFiles> postFiles
) {
}
