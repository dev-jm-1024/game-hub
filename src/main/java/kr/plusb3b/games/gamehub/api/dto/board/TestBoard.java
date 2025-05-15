package kr.plusb3b.games.gamehub.api.dto.board;


public class TestBoard {

    private Long board_id;
    private String board_name;


    public TestBoard() {}

    public TestBoard(Long board_id, String board_name) {
        this.board_id = board_id;
    }

    public Long getBoard_id() {
        return board_id;
    }

    public void setBoard_id(Long board_id) {
        this.board_id = board_id;
    }

    public String getBoard_name() {
        return board_name;
    }

    public void setBoard_name(String board_name) {
        this.board_name = board_name;
    }
}
