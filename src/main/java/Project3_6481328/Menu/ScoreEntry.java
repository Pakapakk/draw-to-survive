package Project3_6481328.Menu;

public class ScoreEntry {

    private final String name;
    private final int score;
    private final String difficulty;
    private final String dateTime;

    public ScoreEntry(String name, int score, String difficulty, String dateTime) {
        this.name = name;
        this.score = score;
        this.difficulty = difficulty;
        this.dateTime = dateTime;
    }

    public String getName() {
        return name;
    }

    public int getScore() {
        return score;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public String getDateTime() {
        return dateTime;
    }

    @Override
    public String toString() {
        return String.format("%-10s | %5d | %-6s | %s",
                name, score, difficulty, dateTime);
    }
}