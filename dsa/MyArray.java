package dsa;

import java.io.*;

public class MyArray {
    private int[] scores;
    private int count = 0;
    private static final int CAPACITY = 10;

    public MyArray() {
        scores = new int[CAPACITY];
    }

    public boolean addScore(int score) {
        if (score < 0) return false;
        if (!isHighScore(score) && count >= CAPACITY) return false;
        int pos = count < CAPACITY ? count : CAPACITY - 1;
        for (int i = 0; i < count && i < CAPACITY; i++) {
            if (score > scores[i]) { pos = i; break; }
        }
        if (count < CAPACITY) {
            for (int i = Math.min(count, CAPACITY - 1); i > pos; i--) scores[i] = scores[i - 1];
            scores[pos] = score;
            count++;
            return true;
        } else {
            for (int i = CAPACITY - 1; i > pos; i--) scores[i] = scores[i - 1];
            scores[pos] = score;
            return true;
        }
    }

    public int[] getScores() {
        int[] copy = new int[CAPACITY];
        for (int i = 0; i < CAPACITY; i++) copy[i] = scores[i];
        return copy;
    }

    public boolean isHighScore(int score) {
        if (count < CAPACITY) return score > 0;
        return score > scores[CAPACITY - 1];
    }

    public void saveToFile(String filename) {
        try {
            File file = new File(filename);
            File parent = file.getParentFile();
            if (parent != null && !parent.exists()) parent.mkdirs();
            PrintWriter writer = new PrintWriter(new FileWriter(file));
            for (int i = 0; i < count && i < CAPACITY; i++) {
                writer.println(scores[i]);
            }
            writer.close();
        } catch (IOException e) {
            System.out.println("Error saving scores: " + e.getMessage());
        }
    }

    public void loadFromFile(String filename) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            count = 0;
            String line;
            while ((line = reader.readLine()) != null && count < CAPACITY) {
                try {
                    scores[count++] = Integer.parseInt(line.trim());
                } catch (NumberFormatException ignored) {}
            }
            reader.close();
        } catch (FileNotFoundException e) {
            System.out.println("High scores file not found, starting fresh.");
        } catch (IOException e) {
            System.out.println("Error loading scores: " + e.getMessage());
        }
    }

    public int getRank(int score) {
        for (int i = 0; i < count && i < CAPACITY; i++) {
            if (scores[i] == score) return i + 1;
        }
        return -1;
    }

    public void clear() {
        scores = new int[CAPACITY];
        count = 0;
    }

    public int getCount() {
        return count;
    }
}
