package engine;

import dsa.MyArrayList;
import dsa.MyStack;
import models.FallingWord;
import models.Player;

public class PowerUpManager {
    public enum PowerUpType {
        STACK_POWER, SLOW_TIME, EXTRA_LIFE, SCORE_BONUS, CLEAR_SCREEN
    }

    private MyStack<String> storedWords = new MyStack<>();
    private boolean isSlowTimeActive = false;
    private int slowTimeRemaining = 0;
    private boolean isScoreBonusActive = false;
    private int scoreBonusRemaining = 0;
    private int powerUpCooldown = 0;

    private Player player;
    private MyArrayList<FallingWord> activeWords;
    private GameEngine gameEngine;

    public PowerUpManager(Player player, MyArrayList<FallingWord> activeWords, GameEngine gameEngine) {
        this.player = player;
        this.activeWords = activeWords;
        this.gameEngine = gameEngine;
    }

    public void activatePowerUp(PowerUpType type) {
        if (type == null) return;
        switch (type) {
            case STACK_POWER:
                System.out.println("Stack Power Activated! Press S");
                if (gameEngine != null) gameEngine.showPowerUpMessage("STACK POWER! Press S to auto-type!");
                break;
            case SLOW_TIME:
                isSlowTimeActive = true;
                slowTimeRemaining = 300;
                if (gameEngine != null) gameEngine.showPowerUpMessage("SLOW TIME! Words moving slower!");
                break;
            case EXTRA_LIFE:
                if (player != null) player.setLives(player.getLives() + 1);
                if (gameEngine != null) gameEngine.showPowerUpMessage("EXTRA LIFE! ❤️");
                break;
            case SCORE_BONUS:
                isScoreBonusActive = true;
                scoreBonusRemaining = 600;
                if (gameEngine != null) gameEngine.showPowerUpMessage("SCORE BONUS! 2x points for 10 seconds!");
                break;
            case CLEAR_SCREEN:
                if (activeWords != null) activeWords.clear();
                if (player != null) player.addPoints(500);
                if (gameEngine != null) gameEngine.showPowerUpMessage("CLEAR SCREEN! +500 Bonus!");
                break;
        }
    }

    public void updatePowerUps() {
        if (isSlowTimeActive) {
            slowTimeRemaining--;
            if (slowTimeRemaining <= 0) {
                isSlowTimeActive = false;
                slowTimeRemaining = 0;
            }
        }
        if (isScoreBonusActive) {
            scoreBonusRemaining--;
            if (scoreBonusRemaining <= 0) {
                isScoreBonusActive = false;
                scoreBonusRemaining = 0;
            }
        }
        if (powerUpCooldown > 0) powerUpCooldown--;
    }

    public void checkAndActivate(int combo) {
        if (powerUpCooldown > 0) return;
        PowerUpType type = null;
        if (combo == 10) type = PowerUpType.STACK_POWER;
        else if (combo == 15) type = PowerUpType.SLOW_TIME;
        else if (combo == 20) type = PowerUpType.EXTRA_LIFE;
        else if (combo == 25) type = PowerUpType.SCORE_BONUS;
        else if (combo == 30) type = PowerUpType.CLEAR_SCREEN;
        if (type != null) {
            activatePowerUp(type);
            powerUpCooldown = 180;
        }
    }

    public double getSpeedMultiplier() {
        return isSlowTimeActive ? 0.5 : 1.0;
    }

    public int getScoreMultiplier() {
        return isScoreBonusActive ? 2 : 1;
    }

    public void storeWordForPowerUp(String word) {
        if (storedWords.size() < 3) storedWords.push(word);
    }

    public String useStoredWord() {
        return storedWords.pop();
    }

    public boolean hasStoredWords() {
        return !storedWords.isEmpty();
    }

    public boolean isSlowTimeActive() { return isSlowTimeActive; }
    public boolean isScoreBonusActive() { return isScoreBonusActive; }
    public int getSlowTimeRemaining() { return slowTimeRemaining; }
    public int getScoreBonusRemaining() { return scoreBonusRemaining; }
    public int getStoredWordCount() { return storedWords.size(); }
}
