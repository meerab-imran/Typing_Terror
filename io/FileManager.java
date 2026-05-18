package io;

import dsa.MyArray;
import models.HighScoreEntry;
import dsa.MyArrayList;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;

public class FileManager {
    private static final String HIGH_SCORES_FILE = "data/highscores.txt";
    private static final String SETTINGS_FILE = "data/settings.txt";
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public void saveHighScores(MyArray scores) {
        try {
            ensureDir("data");
            PrintWriter writer = new PrintWriter(new FileWriter(HIGH_SCORES_FILE));
            int[] arr = scores.getScores();
            for (int s : arr) if (s > 0) writer.println("Player," + s + "," + LocalDateTime.now().format(FMT));
            writer.close();
        } catch (IOException e) {
            System.out.println("Error saving high scores: " + e.getMessage());
        }
    }

    public void saveHighScoreEntries(MyArrayList<HighScoreEntry> entries) {
        try {
            ensureDir("data");
            PrintWriter writer = new PrintWriter(new FileWriter(HIGH_SCORES_FILE));
            for (int i = 0; i < entries.size(); i++) {
                HighScoreEntry e = entries.get(i);
                writer.println(e.getPlayerName() + "," + e.getScore() + "," + e.getDate());
            }
            writer.close();
        } catch (IOException e) {
            System.out.println("Error saving high scores: " + e.getMessage());
            showErrorDialog("Failed to save high scores. Check permissions.");
        }
    }

    public MyArrayList<HighScoreEntry> loadHighScoreEntries() {
        MyArrayList<HighScoreEntry> list = new MyArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(HIGH_SCORES_FILE));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", 3);
                if (parts.length >= 2) {
                    String name = parts[0].trim();
                    int score = Integer.parseInt(parts[1].trim());
                    String date = parts.length == 3 ? parts[2].trim() : LocalDateTime.now().format(FMT);
                    list.add(new HighScoreEntry(name, score, date));
                }
            }
            reader.close();
        } catch (FileNotFoundException e) {
            System.out.println("High scores file not found, creating default.");
        } catch (IOException | NumberFormatException e) {
            System.out.println("Error loading high scores: " + e.getMessage());
        }
        return list;
    }

    public MyArray loadHighScores() {
        MyArray arr = new MyArray();
        MyArrayList<HighScoreEntry> entries = loadHighScoreEntries();
        for (int i = 0; i < entries.size(); i++) arr.addScore(entries.get(i).getScore());
        return arr;
    }

    public Properties loadSettings() {
        Properties props = defaultSettings();
        try {
            File f = new File(SETTINGS_FILE);
            if (!f.exists()) { saveSettings(props); return props; }
            props.load(new FileReader(f));
        } catch (IOException e) {
            System.out.println("Error loading settings: " + e.getMessage());
        }
        return props;
    }

    public void saveSettings(Properties settings) {
        try {
            ensureDir("data");
            settings.store(new FileWriter(SETTINGS_FILE), "Typing Terror Settings");
        } catch (IOException e) {
            System.out.println("Error saving settings: " + e.getMessage());
        }
    }

    private Properties defaultSettings() {
        Properties p = new Properties();
        p.setProperty("soundEnabled", "true");
        p.setProperty("musicVolume", "70");
        p.setProperty("effectsVolume", "80");
        p.setProperty("difficulty", "Normal");
        p.setProperty("fallSpeedBase", "4");
        p.setProperty("spawnInterval", "15");
        p.setProperty("startingLives", "5");
        return p;
    }

    public void createDefaultWordBank() {
        try {
            ensureDir("resources/words");
            File f = new File("resources/words/wordbank.txt");
            if (!f.exists()) {
                PrintWriter writer = new PrintWriter(new FileWriter(f));
                String[] words = {"CAT","DOG","SUN","CAR","BUS","RED","BLUE","FISH","BIRD","TREE",
                        "HOUSE","PHONE","MOUSE","SCREEN","LAPTOP","WINDOW","DOOR","TABLE","CHAIR",
                        "DIFFICULT","CHALLENGE","BEAUTIFUL","HAPPINESS","ADVENTURE","EXTRAORDINARY"};
                for (String w : words) writer.println(w);
                writer.close();
            }
        } catch (IOException e) {
            System.out.println("Error creating word bank: " + e.getMessage());
        }
    }

    private void ensureDir(String path) {
        File dir = new File(path);
        if (!dir.exists()) dir.mkdirs();
    }

    private void showErrorDialog(String msg) {
        System.err.println("ERROR: " + msg);
    }

    public String nowString() {
        return LocalDateTime.now().format(FMT);
    }
}
