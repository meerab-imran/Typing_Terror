package ui;

import io.FileManager;
import dsa.MyArrayList;
import models.HighScoreEntry;

import javax.swing.*;
import java.awt.*;

public class GameOverScreen extends JDialog {
    private int finalScore;
    private boolean isHighScore;
    private FileManager fileManager;

    private Runnable onPlayAgain;
    private Runnable onMainMenu;

    private JLabel animatedScoreLabel;
    private int displayedScore = 0;
    private Timer scoreTimer;
    private Timer shakeTimer;
    private int shakeCount = 0;

    public GameOverScreen(JFrame parent, int finalScore, boolean isHighScore,FileManager fileManager, Runnable onPlayAgain, Runnable onMainMenu) {
        super(parent, "Game Over", true);
        this.finalScore = finalScore;
        this.isHighScore = isHighScore;
        this.fileManager = fileManager;
        this.onPlayAgain = onPlayAgain;
        this.onMainMenu = onMainMenu;

        setSize(520, 480);
        setLocationRelativeTo(parent);
        setUndecorated(true);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(0, 0, new Color(30, 0, 0), 0, getHeight(), new Color(20, 10, 40));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(new Color(200, 50, 50));
                g2.setStroke(new BasicStroke(3));
                g2.drawRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 20, 20);
            }
        };
        mainPanel.setOpaque(false);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        setContentPane(mainPanel);

        buildContent(mainPanel);
        startAnimations();
    }

    private void buildContent(JPanel panel) {
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);

        JLabel gameOverLabel = new JLabel("GAME OVER") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth("GAME OVER")) / 2;
                // Glow
                g2.setColor(new Color(255, 50, 50, 80));
                for (int d = 6; d >= 1; d--) g2.drawString("GAME OVER", x - d, getHeight() / 2 + d);
                g2.setColor(new Color(255, 80, 80));
                g2.drawString("GAME OVER", x, getHeight() / 2);
            }
        };
        gameOverLabel.setFont(new Font("Arial", Font.BOLD, 56));
        gameOverLabel.setForeground(new Color(255, 80, 80));
        gameOverLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        gameOverLabel.setPreferredSize(new Dimension(440, 70));
        content.add(gameOverLabel);
        content.add(Box.createVerticalStrut(15));

        animatedScoreLabel = new JLabel("Score: 0");
        animatedScoreLabel.setFont(new Font("Arial", Font.BOLD, 36));
        animatedScoreLabel.setForeground(new Color(255, 220, 50));
        animatedScoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        content.add(animatedScoreLabel);
        content.add(Box.createVerticalStrut(10));

        if (isHighScore) {
            JLabel hsLabel = new JLabel("⭐ NEW HIGH SCORE! ⭐");
            hsLabel.setFont(new Font("Arial", Font.BOLD, 22));
            hsLabel.setForeground(new Color(255, 200, 0));
            hsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            content.add(hsLabel);
            content.add(Box.createVerticalStrut(10));
        }

        content.add(Box.createVerticalStrut(5));

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        buttons.setOpaque(false);

        JButton playAgainBtn = makeButton("PLAY AGAIN", new Color(0, 140, 70));
        playAgainBtn.addActionListener(e -> {
            dispose();
            if (onPlayAgain != null) onPlayAgain.run();
        });
        buttons.add(playAgainBtn);

        JButton menuBtn = makeButton("MAIN MENU", new Color(60, 80, 200));
        menuBtn.addActionListener(e -> {
            dispose();
            if (onMainMenu != null) onMainMenu.run();
        });
        buttons.add(menuBtn);

        JButton exitBtn = makeButton("EXIT", new Color(160, 40, 40));
        exitBtn.addActionListener(e -> System.exit(0));
        buttons.add(exitBtn);

        content.add(buttons);
        panel.add(content, BorderLayout.CENTER);
    }

    private JButton makeButton(String text, Color color) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isPressed() ? color.darker() :
                        getModel().isRollover() ? color.brighter() : color);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Arial", Font.BOLD, 15));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(text, (getWidth() - fm.stringWidth(text)) / 2,
                        (getHeight() + fm.getAscent()) / 2 - 3);
            }
        };
        btn.setPreferredSize(new Dimension(140, 44));
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void saveHighScore(String name, int score) {
        try {
            MyArrayList<HighScoreEntry> entries = fileManager.loadHighScoreEntries();
            entries.add(new HighScoreEntry(name, score, fileManager.nowString()));
            for (int i = 0; i < entries.size() - 1; i++) {
                for (int j = 0; j < entries.size() - 1 - i; j++) {
                    if (entries.get(j).getScore() < entries.get(j + 1).getScore()) {
                        HighScoreEntry tmp = entries.get(j);
                        entries.set(j, entries.get(j + 1));
                        entries.set(j + 1, tmp);
                    }
                }
            }
            fileManager.saveHighScoreEntries(entries);
        } catch (Exception e) {
            System.out.println("Error saving high score: " + e.getMessage());
        }
    }

    private void startAnimations() {
        scoreTimer = new Timer(20, e -> {
            if (displayedScore < finalScore) {
                displayedScore = Math.min(finalScore, displayedScore + Math.max(1, finalScore / 80));
                animatedScoreLabel.setText("Score: " + displayedScore);
            } else {
                ((Timer) e.getSource()).stop();
            }
        });
        scoreTimer.start();
    }

    @Override
    public void dispose() {
        if (scoreTimer != null) scoreTimer.stop();
        super.dispose();
    }
}
