package models;

import java.awt.Color;

public class WordInfo implements Comparable<WordInfo> {
    private String word;
    private int points;
    private int difficulty;
    private int fallSpeed;
    private Color color;
    private String category;

    public WordInfo(String word, int points, int difficulty, int fallSpeed, Color color, String category) {
        this.word = word;
        this.points = points;
        this.difficulty = difficulty;
        this.fallSpeed = fallSpeed;
        this.color = color;
        this.category = category;
    }

    public String getWord() { return word; }
    public int getPoints() { return points; }
    public int getDifficulty() { return difficulty; }
    public int getFallSpeed() { return fallSpeed; }
    public Color getColor() { return color; }
    public String getCategory() { return category; }

    public void setWord(String word) { this.word = word; }
    public void setPoints(int points) { this.points = points; }
    public void setDifficulty(int difficulty) { this.difficulty = difficulty; }
    public void setFallSpeed(int fallSpeed) { this.fallSpeed = fallSpeed; }
    public void setColor(Color color) { this.color = color; }
    public void setCategory(String category) { this.category = category; }

    @Override
    public int compareTo(WordInfo other) {
        return Integer.compare(this.difficulty, other.difficulty);
    }

    @Override
    public String toString() {
        return word + " [" + category + ", diff=" + difficulty + ", pts=" + points + "]";
    }
}
