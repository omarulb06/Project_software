package il.cshaifasweng.OCSFMediatorExample.client;

public class StartGameEvent {
    private final char signal; // 'X' או 'O'
    private final boolean isFirstTurn; // האם זה תורו הראשון של השחקן

    public StartGameEvent(char signal, boolean isFirstTurn) {
        this.signal = signal;
        this.isFirstTurn = isFirstTurn;
    }

    public char getSignal() {
        return signal;
    }

    public boolean isFirstTurn() {
        return isFirstTurn;
    }
}
