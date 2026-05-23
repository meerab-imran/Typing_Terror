package models;

import java.awt.Rectangle;

public class FallingWord implements Comparable<FallingWord> {
    private WordInfo wordInfo;
    private int x;
    private int y;
    private boolean isActive;
    private long spawnTime;
    private int wordWidth;

    public FallingWord(WordInfo wordInfo, int screenWidth) {
        this.wordInfo = wordInfo;
        this.isActive = true;
        this.spawnTime = System.currentTimeMillis();
        this.wordWidth = wordInfo.getWord().length() * 14 + 20;
        int minX = 100;
        int maxX = Math.max(minX + 1, screenWidth - wordWidth - 100);
        this.x = minX + (int)(Math.random() * (maxX - minX));
        this.y = 50;
    }

    public void update() {
        if (isActive) {
            y += wordInfo.getFallSpeed();
        }
    }

    public void update(double speedMultiplier) {
        if (isActive) {
            y += (int)(wordInfo.getFallSpeed() * speedMultiplier);
        }
    }

    public Rectangle getBounds() {
        return new Rectangle(x - 5, y - 5, wordWidth + 10, 40);
    }

    public boolean isOffScreen(int screenHeight) {
        return y > screenHeight - 100;
    }

    public WordInfo getWordInfo() { return wordInfo; }
    public int getX() { return x; }
    public int getY() { return y; }
    public boolean isActive() { return isActive; }
    public long getSpawnTime() { return spawnTime; }
    public int getWordWidth() { return wordWidth; }

    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }
    public void setActive(boolean active) { this.isActive = active; }
    public void setWordWidth(int w) { this.wordWidth = w; }

    @Override
    public int compareTo(FallingWord other) {
        return Integer.compare(this.wordInfo.getDifficulty(), other.wordInfo.getDifficulty());
    }
}
