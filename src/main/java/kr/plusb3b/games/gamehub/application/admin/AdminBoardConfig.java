package kr.plusb3b.games.gamehub.application.admin;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class AdminBoardConfig {

    @Value("${admin.api.path}")
    private String basePath;

    @Value("${admin.board.activate}")
    private String activatePath;

    @Value("${admin.board.deactivate}")
    private String deactivatePate;

    @Value("${admin.board.rename}")
    private String renamePath;

    @Value("${admin.board.create}")
    private String createPath;


    public Map<String, String> getAdminBoardPaths() {
        return Map.ofEntries(
                Map.entry("activate", basePath+activatePath),
                Map.entry("deactivate", basePath+deactivatePate),
                Map.entry("rename", basePath+renamePath),
                Map.entry("makeBoard", basePath+createPath)
        );
    }

}
