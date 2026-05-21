package engine;

import dsa.*;
import models.*;
import effects.*;
import sound.SoundManager;
import io.FileManager;

import javax.swing.*;
import java.util.Random;

public class GameEngine {
    private MyQueue<WordInfo> wordQueue = new MyQueue<>();
    private MyPriorityQueue<FallingWord> priorityWords = new MyPriorityQueue<>();
    private MyArrayList<FallingWord> activeWords = new MyArrayList<>();
    private MyStack<String> comboStack = new MyStack<>();
    private MyHashMap wordCache = new MyHashMap();

    private Player player = new Player();
    private WordGenerator wordGenerator;
    private ScoreCalculator scoreCalculator = new ScoreCalculator();
    private CollisionDetector collisionDetector;
    private PowerUpManager powerUpManager;
    private SoundManager soundManager;
    private FileManager fileManager;

    private MyArrayList<Particle> particles = new MyArrayList<>();
    private MyArrayList<FloatingText> floatingTexts = new MyArrayList<>();

    private Timer gameTimer;
    private Timer spawnTimer;

    private boolean isRunning = false;
    private boolean isPaused = false;

    private int screenWidth = 1280;
    private int screenHeight = 720;
    private int bottomThreshold;

    private int shakeOffsetX = 0;
    private int shakeOffsetY = 0;
    private int shakeDuration = 0;
    private int shakeIntensity = 5;
    private Random random = new Random();

    private String powerUpMessage = "";
    private int powerUpMessageTimer = 0;

    private GameListener listener;
    private int spawnIntervalMs = 1500;
    private String playerName = "Player";

    public interface GameListener {
        void onScoreChanged(int score, int lives, int combo);
        void onGameOver(int finalScore, boolean isNewHighScore);
        void onRepaint();
        void onPowerUpMessage(String msg);
    }

    public GameEngine(int screenWidth, int screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.bottomThreshold = screenHeight - 100;

        wordGenerator = new WordGenerator();
        collisionDetector = new CollisionDetector(screenHeight, this);
        powerUpManager = new PowerUpManager(player, activeWords, this);
        soundManager = new SoundManager();
        fileManager = new FileManager();

        fileManager.createDefaultWordBank();

        dsa.MyArray scores = fileManager.loadHighScores();
        int[] arr = scores.getScores();
        if (arr.length > 0 && arr[0] > 0) player.setHighScore(arr[0]);
    }

    public void setListener(GameListener listener) {
        this.listener = listener;
    }

    public void startGame(String name) {
        this.playerName = (name != null && !name.isEmpty()) ? name : "Player";
        isRunning = true;
        isPaused = false;
        player.reset();
        activeWords.clear();
        particles.clear();
        floatingTexts.clear();
        wordQueue.clear();

        java.util.Properties settings = fileManager.loadSettings();
        try {
            int lives = Integer.parseInt(settings.getProperty("startingLives", "5"));
            player.setLives(lives);
        } catch (NumberFormatException ignored) {}

        try {
            spawnIntervalMs = Integer.parseInt(settings.getProperty("spawnInterval", "15")) * 100;
            if (spawnIntervalMs < 500) spawnIntervalMs = 500;
        } catch (NumberFormatException ignored) {}

        startTimers();
        notifyListeners();
    }

    private void startTimers() {
        if (gameTimer != null) gameTimer.stop();
        if (spawnTimer != null) spawnTimer.stop();

        gameTimer = new Timer(16, e -> {
            try {
                updateGame();
            } catch (Exception ex) {
                System.out.println("Game error: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        spawnTimer = new Timer(spawnIntervalMs, e -> {
            if (isRunning && !isPaused) spawnWord();
        });

        gameTimer.start();
        spawnTimer.start();
        spawnWord();
    }

    public void pauseGame() {
        isPaused = true;
        if (gameTimer != null) gameTimer.stop();
        if (spawnTimer != null) spawnTimer.stop();
    }

    public void resumeGame() {
        isPaused = false;
        if (gameTimer != null) gameTimer.start();
        if (spawnTimer != null) spawnTimer.start();
    }

    public void endGame() {
        isRunning = false;
        isPaused = false;
        if (gameTimer != null) gameTimer.stop();
        if (spawnTimer != null) spawnTimer.stop();

        dsa.MyArray scores = fileManager.loadHighScores();
        boolean isNewHighScore = scores.isHighScore(player.getCurrentScore());

        dsa.MyArrayList<models.HighScoreEntry> entries = fileManager.loadHighScoreEntries();
        entries.add(new models.HighScoreEntry(playerName, player.getCurrentScore(), fileManager.nowString()));
        for (int i = 0; i < entries.size() - 1; i++) {
            for (int j = 0; j < entries.size() - 1 - i; j++) {
                if (entries.get(j).getScore() < entries.get(j + 1).getScore()) {
                    models.HighScoreEntry tmp = entries.get(j);
                    entries.set(j, entries.get(j + 1));
                    entries.set(j + 1, tmp);
                }
            }
        }
        fileManager.saveHighScoreEntries(entries);
        soundManager.playGameOver();

        final boolean highScore = isNewHighScore;
        if (listener != null) listener.onGameOver(player.getCurrentScore(), highScore);
    }

    private double getScoreSpeedMultiplier() {
        int score = player.getCurrentScore();
        if (score < 50)        return 1.0;
        else if (score < 100)  return 2.0;
        else if (score < 150)  return 3.0;
        else if (score < 200)  return 4.0;
        else if (score < 250)  return 5.0;
        else                   return 6.0;
    }

    public void updateGame() {
        if (!isRunning || isPaused) return;

        try {
            powerUpManager.updatePowerUps();

            double speedMult = powerUpManager.getSpeedMultiplier() * getScoreSpeedMultiplier();
            for (int i = 0; i < activeWords.size(); i++) {
                FallingWord w = activeWords.get(i);
                if (w != null) w.update(speedMult);
            }

            collisionDetector.checkAllCollisions(activeWords, player);

            if (shakeDuration > 0) {
                shakeOffsetX = random.nextInt(shakeIntensity * 2) - shakeIntensity;
                shakeOffsetY = random.nextInt(shakeIntensity * 2) - shakeIntensity;
                shakeDuration--;
            } else {
                shakeOffsetX = 0;
                shakeOffsetY = 0;
            }

            for (int i = 0; i < particles.size(); i++) {
                Particle p = particles.get(i);
                p.update();
                if (!p.isAlive()) { particles.remove(i); i--; }
            }

            for (int i = 0; i < floatingTexts.size(); i++) {
                FloatingText ft = floatingTexts.get(i);
                ft.update();
                if (!ft.isAlive()) { floatingTexts.remove(i); i--; }
            }

            if (powerUpMessageTimer > 0) powerUpMessageTimer--;

            notifyListeners();
            if (listener != null) listener.onRepaint();
        } catch (Exception e) {
            System.out.println("Game error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void spawnWord() {
        try {
            WordInfo wordInfo = wordGenerator.getRandomWord();
            if (wordInfo == null) return;

            wordQueue.enqueue(wordInfo);
            WordInfo dequeued = wordQueue.dequeue();
            if (dequeued == null) return;

            if ("Boss".equals(dequeued.getCategory())) soundManager.playBossSpawn();

            FallingWord fw = new FallingWord(dequeued, screenWidth);
            activeWords.add(fw);
        } catch (Exception e) {
            System.out.println("Spawn error: " + e.getMessage());
        }
    }

    public boolean processInput(String typedWord) {
        if (typedWord == null || typedWord.isEmpty()) return false;
        String upper = typedWord.trim().toUpperCase();

        for (int i = 0; i < activeWords.size(); i++) {
            try {
                FallingWord fw = activeWords.get(i);
                if (fw != null && fw.getWordInfo().getWord().equalsIgnoreCase(upper)) {
                    activeWords.remove(i);
                    WordInfo wi = fw.getWordInfo();

                    player.incrementCombo();
                    int pts = scoreCalculator.calculatePoints(wi, player.getCurrentCombo());
                    pts *= powerUpManager.getScoreMultiplier();
                    player.addPoints(pts);

                    comboStack.push(wi.getWord());

                    powerUpManager.checkAndActivate(player.getCurrentCombo());

                    powerUpManager.storeWordForPowerUp(wi.getWord());

                    createParticleBurst(fw.getX() + fw.getWordWidth() / 2, fw.getY(), wi.getColor());
                    floatingTexts.add(new FloatingText(fw.getX(), fw.getY(), "+" + pts, wi.getColor()));

                    soundManager.playCorrect();
                    notifyListeners();
                    return true;
                }
            } catch (Exception e) {
                System.out.println("Input processing error: " + e.getMessage());
            }
        }
        return false;
    }

    public boolean activateStackPower() {
        String word = powerUpManager.useStoredWord();
        if (word == null || word.isEmpty()) return false;
        return processInput(word);
    }

    private void createParticleBurst(int x, int y, java.awt.Color baseColor) {
        for (int i = 0; i < 15; i++) {
            float vx = random.nextFloat() * 10 - 5;
            float vy = random.nextFloat() * 6 + 2;
            vy = -vy;
            int life = 20 + random.nextInt(20);
            int size = 3 + random.nextInt(3);
            particles.add(new Particle(x, y, vx, vy, life, baseColor, size));
        }
    }

    public void triggerScreenShake(int durationMs, int intensity) {
        shakeDuration = durationMs * 60 / 1000;
        shakeIntensity = intensity;
    }

    public void showPowerUpMessage(String msg) {
        powerUpMessage = msg;
        powerUpMessageTimer = 180;
        soundManager.playPowerUp();
        if (listener != null) listener.onPowerUpMessage(msg);
    }

    private void notifyListeners() {
        if (listener != null) {
            listener.onScoreChanged(player.getCurrentScore(), player.getLives(), player.getCurrentCombo());
        }
    }

    public void onWordMissed() {
        soundManager.playMiss();
        triggerScreenShake(150, 5);
        notifyListeners();
    }

    public MyArrayList<FallingWord> getActiveWords() { return activeWords; }
    public MyArrayList<Particle> getParticles() { return particles; }
    public MyArrayList<FloatingText> getFloatingTexts() { return floatingTexts; }
    public Player getPlayer() { return player; }
    public PowerUpManager getPowerUpManager() { return powerUpManager; }
    public boolean isRunning() { return isRunning; }
    public boolean isPaused() { return isPaused; }
    public int getShakeOffsetX() { return shakeOffsetX; }
    public int getShakeOffsetY() { return shakeOffsetY; }
    public SoundManager getSoundManager() { return soundManager; }
    public FileManager getFileManager() { return fileManager; }
    public String getPowerUpMessage() { return powerUpMessage; }
    public int getPowerUpMessageTimer() { return powerUpMessageTimer; }
    public int getScreenWidth() { return screenWidth; }
    public int getScreenHeight() { return screenHeight; }
}
