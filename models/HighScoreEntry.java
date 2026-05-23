package models;

public class HighScoreEntry {
    private String playerName;
    private int score;
    private String date;

    public HighScoreEntry(String playerName, int score, String date) {
        this.playerName = playerName;
        this.score = score;
        this.date = date;
    }

    public String getPlayerName() { return playerName; }
    public int getScore() { return score; }
    public String getDate() { return date; }

    public String getFormattedString() {
        return playerName + " — " + score + " pts (" + date + ")";
    }

    @Override
    public String toString() {
        return playerName + "," + score + "," + date;
    }
}
