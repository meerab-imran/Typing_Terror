package engine;

import dsa.MyTree;
import dsa.MyHashMap;
import dsa.MyArrayList;
import models.WordInfo;

import java.awt.Color;
import java.io.*;
import java.util.Random;

public class WordGenerator {
    private MyTree<String> easyWords = new MyTree<>();
    private MyTree<String> mediumWords = new MyTree<>();
    private MyTree<String> hardWords = new MyTree<>();
    private MyTree<String> bossWords = new MyTree<>();
    private MyHashMap wordCache = new MyHashMap();
    private Random random = new Random();

    private MyArrayList<String> easyList = new MyArrayList<>();
    private MyArrayList<String> mediumList = new MyArrayList<>();
    private MyArrayList<String> hardList = new MyArrayList<>();
    private MyArrayList<String> bossList = new MyArrayList<>();

    public WordGenerator() {
        createDefaultWordBank();
    }

    public void createDefaultWordBank() {
        String[] allWords = {
                "CAT", "DOG", "SUN", "CAR", "BUS", "RED", "BLUE", "FISH", "BIRD",
                "TREE", "MOON", "STAR", "FLY", "RUN", "JUMP", "SIT", "WALK", "TALK", "EAT",
                "HOUSE", "PHONE", "MOUSE", "SCREEN", "LAPTOP", "WINDOW", "DOOR",
                "TABLE", "CHAIR", "PENCIL", "PAPER", "BOOK", "TEACHER", "STUDENT",
                "SCHOOL", "COLLEGE", "RIVER", "STAND",
                "MOUNTAIN", "COMPUTER", "KEYBOARD", "DIFFICULT", "CHALLENGE",
                "BEAUTIFUL", "HAPPINESS", "ADVENTURE", "EXCITING", "WONDERFUL",
                "BRILLIANT", "COURAGEOUS", "DANGEROUS", "MYSTERIOUS", "STRENGTH",
                "VICTORY", "JOURNEY",
                "CELEBRATION", "EXTRAORDINARY", "UNBELIEVABLE", "ACKNOWLEDGMENT",
                "COMPREHENSIVE", "RECOMMENDATION", "RESPONSIBILITY", "UNDERSTANDING"
        };
        for (String w : allWords) {
            addWordToCorrectList(w);
            cacheWord(w);
        }
    }

    private void addWordToCorrectList(String word) {
        int len = word.length();
        if (len <= 4)       { easyWords.insert(word);   easyList.add(word); }
        else if (len <= 7)  { mediumWords.insert(word); mediumList.add(word); }
        else if (len <= 10) { hardWords.insert(word);   hardList.add(word); }
        else                { bossWords.insert(word);   bossList.add(word); }
    }

    private void cacheWord(String word) {
        WordInfo info = buildWordInfo(word);
        wordCache.put(word, info);
    }

    private WordInfo buildWordInfo(String word) {
        int len = word.length();
        int difficulty, fallSpeed, points;
        Color color;
        String category;
        if (len <= 4) {
            difficulty = 1;  fallSpeed = 1;  points = 10;
            color = new Color(0, 255, 0);   category = "Easy";
        } else if (len <= 7) {
            difficulty = 5;  fallSpeed = 2;  points = 20;
            color = new Color(255, 255, 0); category = "Medium";
        } else if (len <= 10) {
            difficulty = 10; fallSpeed = 4;  points = 50;
            color = new Color(255, 136, 0); category = "Hard";
        } else {
            // 11+ letters = Boss (per spec)
            difficulty = 20; fallSpeed = 6; points = 200;
            color = new Color(170, 0, 255); category = "Boss";
        }
        return new WordInfo(word, points, difficulty, fallSpeed, color, category);
    }

    public void loadWordsFromFile(String filename) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            String line;
            while ((line = reader.readLine()) != null) {
                String word = line.trim().toUpperCase();
                if (word.isEmpty()) continue;
                addWordToCorrectList(word);
                cacheWord(word);
            }
            reader.close();
        } catch (FileNotFoundException e) {
            System.out.println("Word bank file not found, using defaults.");
            createDefaultWordBank();
        } catch (IOException e) {
            System.out.println("Error loading word bank: " + e.getMessage());
        }
    }

    public WordInfo getRandomWord() {
        double roll = random.nextDouble();
        String word;
        if (roll < 0.20 && easyList.size() > 0) {
            word = easyList.get(random.nextInt(easyList.size()));
        } else if (roll < 0.80 && mediumList.size() > 0) {
            word = mediumList.get(random.nextInt(mediumList.size()));
        } else if (roll < 0.95 && hardList.size() > 0) {
            word = hardList.get(random.nextInt(hardList.size()));
        } else if (bossList.size() > 0) {
            word = bossList.get(random.nextInt(bossList.size()));
        } else {
            word = easyList.get(0);
        }
        return getWordInfo(word);
    }

    public WordInfo getBossWord() {
        if (bossList.isEmpty()) return getWordInfo("EXTRAORDINARY");
        String word = bossList.get(random.nextInt(bossList.size()));
        return getWordInfo(word);
    }

    public WordInfo getWordByDifficulty(int minDiff, int maxDiff) {
        MyArrayList<String> candidates = new MyArrayList<>();
        for (int i = 0; i < easyList.size(); i++) {
            WordInfo wi = wordCache.get(easyList.get(i));
            if (wi != null && wi.getDifficulty() >= minDiff && wi.getDifficulty() <= maxDiff) candidates.add(easyList.get(i));
        }
        for (int i = 0; i < mediumList.size(); i++) {
            WordInfo wi = wordCache.get(mediumList.get(i));
            if (wi != null && wi.getDifficulty() >= minDiff && wi.getDifficulty() <= maxDiff) candidates.add(mediumList.get(i));
        }
        for (int i = 0; i < hardList.size(); i++) {
            WordInfo wi = wordCache.get(hardList.get(i));
            if (wi != null && wi.getDifficulty() >= minDiff && wi.getDifficulty() <= maxDiff) candidates.add(hardList.get(i));
        }
        for (int i = 0; i < bossList.size(); i++) {
            WordInfo wi = wordCache.get(bossList.get(i));
            if (wi != null && wi.getDifficulty() >= minDiff && wi.getDifficulty() <= maxDiff) candidates.add(bossList.get(i));
        }
        if (candidates.isEmpty()) return getRandomWord();
        return getWordInfo(candidates.get(random.nextInt(candidates.size())));
    }

    public WordInfo getWordInfo(String word) {
        WordInfo cached = wordCache.get(word);
        if (cached != null) return cached;
        WordInfo info = buildWordInfo(word);
        wordCache.put(word, info);
        return info;
    }
}
