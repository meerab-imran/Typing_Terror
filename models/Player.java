package models;

public class Player {
    private String name = "Player";
    private int currentScore = 0;
    private int highScore = 0;
    private int lives = 5;
    private int currentCombo = 0;
    private int wordsTyped = 0;

    public Player() {}

    public Player(String name) {
        this.name = name;
    }

    public void addPoints(int points) {
        currentScore += points;
        if (currentScore > highScore) highScore = currentScore;
    }

    public void loseLife() {
        lives--;
        currentCombo = 0;
    }

    public void resetCombo() {
        currentCombo = 0;
    }

    public void incrementCombo() {
        currentCombo++;
        wordsTyped++;
    }

    public boolean isAlive() {
        return lives > 0;
    }

    public double getComboMultiplier() {
        if (currentCombo >= 20) return 3.0;
        if (currentCombo >= 16) return 2.5;
        if (currentCombo >= 11) return 2.0;
        if (currentCombo >= 6)  return 1.5;
        return 1.0;
    }

    public void reset() {
        currentScore = 0;
        lives = 5;
        currentCombo = 0;
        wordsTyped = 0;
    }

    public String getName() { return name; }
    public int getCurrentScore() { return currentScore; }
    public int getHighScore() { return highScore; }
    public int getLives() { return lives; }
    public int getCurrentCombo() { return currentCombo; }
    public int getWordsTyped() { return wordsTyped; }

    public void setName(String name) { this.name = name; }
    public void setCurrentScore(int score) { this.currentScore = score; }
    public void setHighScore(int score) { this.highScore = score; }
    public void setLives(int lives) { this.lives = Math.min(10, Math.max(0, lives)); }
    public void setCurrentCombo(int combo) { this.currentCombo = combo; }
    public void setWordsTyped(int n) { this.wordsTyped = n; }
}
