package Project3_6481328.Menu;

import Project3_6481328.utils.Settings;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class ScoreManager {

    private static final String PATH = Settings.SCORE_FILE_PATH;

    public static void saveScore(String name, int score, String difficulty) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(PATH, true))) {
            String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            bw.write(name + "," + score + "," + difficulty + "," + timestamp);
            bw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<ScoreEntry> loadScoreEntries() {
        List<ScoreEntry> entries = new ArrayList<>();
        File file = new File(PATH);

        if (!file.exists()) return entries;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;

            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",", 4);

                if (parts.length == 4) {
                    try {
                        String name = parts[0].trim();
                        int score = Integer.parseInt(parts[1].trim());
                        String difficulty = parts[2].trim();
                        String dateTime = parts[3].trim();

                        entries.add(new ScoreEntry(name, score, difficulty, dateTime));
                    } catch (NumberFormatException ignored) {
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        entries.sort((a, b) -> Integer.compare(b.getScore(), a.getScore()));
        return entries;
    }
}