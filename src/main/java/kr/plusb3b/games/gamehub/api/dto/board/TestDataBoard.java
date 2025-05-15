package kr.plusb3b.games.gamehub.api.dto.board;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestDataBoard {

    public Map<Long, String> boardIdList(){
        Map<Long, String> boardIdList = new HashMap<Long, String>();

        long free = 6000;
        long report = 6001;
        long qna = 6002;
        long notice = 6003;
        long suggestion = 6004;
        long guide = 6005;


        boardIdList.put(free, "Free");
        boardIdList.put(report, "Report");
        boardIdList.put(qna, "Qna");
        boardIdList.put(notice, "Notice");
        boardIdList.put(suggestion, "Suggestion");
        boardIdList.put(guide, "Guide");


        return boardIdList;
    }

}
