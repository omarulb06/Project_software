package il.cshaifasweng.OCSFMediatorExample.client;

public class MoveEvent {
    private final int row;
    private final int col;
    private final char player;

    public MoveEvent(int row, int col, char player) {
        this.row = row;
        this.col = col;
        this.player = player;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public char getPlayer() {
        return player;
    }
}