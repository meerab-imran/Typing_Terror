package engine;

import models.WordInfo;

public class ScoreCalculator {

    public int calculatePoints(WordInfo word, int combo) {
        if (word == null) return 0;
        int basePoints = word.getPoints();
        double comboMultiplier = getComboMultiplier(combo);
        double difficultyBonus = getDifficultyBonus(word.getDifficulty());
        return (int)(basePoints * comboMultiplier * difficultyBonus);
    }

    private double getComboMultiplier(int combo) {
        if (combo >= 20) return 3.0;
        if (combo >= 16) return 2.5;
        if (combo >= 11) return 2.0;
        if (combo >= 6)  return 1.5;
        return 1.0;
    }

    private double getDifficultyBonus(int difficulty) {
        if (difficulty >= 20) return 2.0;
        if (difficulty >= 16) return 1.8;
        if (difficulty >= 11) return 1.5;
        if (difficulty >= 6)  return 1.2;
        return 1.0;
    }
}
