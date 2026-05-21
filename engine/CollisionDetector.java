package engine;

import dsa.MyArrayList;
import models.FallingWord;
import models.Player;

public class CollisionDetector {
    private int screenHeight;
    private int bottomThreshold;
    private GameEngine gameEngine;

    public CollisionDetector(int screenHeight, GameEngine gameEngine) {
        this.screenHeight = screenHeight;
        this.bottomThreshold = screenHeight - 100;
        this.gameEngine = gameEngine;
    }

    public boolean hasReachedBottom(FallingWord word) {
        return word.getY() > bottomThreshold;
    }

    public void checkAllCollisions(MyArrayList<FallingWord> activeWords, Player player) {
        for (int i = 0; i < activeWords.size(); i++) {
            try {
                FallingWord word = activeWords.get(i);
                if (word != null && word.isActive() && hasReachedBottom(word)) {
                    activeWords.remove(i);
                    player.loseLife();
                    i--;
                    if (gameEngine != null) {
                        gameEngine.onWordMissed();
                        if (!player.isAlive()) {
                            gameEngine.endGame();
                            return;
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println("Collision check error: " + e.getMessage());
            }
        }
    }

    public void setScreenHeight(int h) {
        this.screenHeight = h;
        this.bottomThreshold = h - 100;
    }
}
