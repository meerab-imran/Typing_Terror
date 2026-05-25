package ui;

import engine.GameEngine;


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MainWindow extends JFrame implements GameEngine.GameListener {
    private GameEngine engine;
    private GamePanel gamePanel;
    private StartScreen startScreen;

    private JPanel topPanel;
    private JLabel scoreLabel;
    private JLabel livesLabel;
    private JLabel comboLabel;
    private JTextField inputField;
    private JButton submitBtn;
    private JButton pauseBtn;
    private JButton quitBtn;

    private CardLayout cardLayout;
    private JPanel cardPanel;

    private static final String CARD_START = "start";
    private static final String CARD_GAME = "game";

    public MainWindow() {
        setTitle("Typing Terror — DSA Arcade");
        setSize(1280, 720);
        setMinimumSize(new Dimension(1280, 720));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        engine = new GameEngine(1280, 620);
        engine.setListener(this);

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        startScreen = new StartScreen(
                this::showGameScreen,
                this::showHighScores,
                this::showSettings,
                () -> System.exit(0)
        );

        JPanel gameView = buildGameView();
        cardPanel.add(startScreen, CARD_START);
        cardPanel.add(gameView, CARD_GAME);

        setContentPane(cardPanel);
        cardLayout.show(cardPanel, CARD_START);

        setVisible(true);
    }

    private JPanel buildGameView() {
        JPanel view = new JPanel(new BorderLayout());
        view.setBackground(new Color(26, 26, 46));

        topPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(0, 0, new Color(15, 15, 40), getWidth(), 0, new Color(30, 20, 60));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(new Color(100, 100, 200, 100));
                g2.fillRect(0, getHeight() - 2, getWidth(), 2);
            }
        };
        topPanel.setPreferredSize(new Dimension(1280, 60));
        topPanel.setLayout(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));

        JPanel leftHud = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        leftHud.setOpaque(false);
        scoreLabel = makeHudLabel("Score: 0");
        livesLabel = makeHudLabel("❤ ❤ ❤ ❤ ❤");
        comboLabel = makeHudLabel("Combo: 0");
        leftHud.add(scoreLabel);
        leftHud.add(livesLabel);
        leftHud.add(comboLabel);
        topPanel.add(leftHud, BorderLayout.WEST);

        JPanel rightHud = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightHud.setOpaque(false);

        pauseBtn = makeHudButton("⏸ PAUSE");
        pauseBtn.addActionListener(e -> togglePause());
        quitBtn = makeHudButton("✕ QUIT");
        quitBtn.addActionListener(e -> quitToMenu());
        rightHud.add(pauseBtn);
        rightHud.add(quitBtn);
        topPanel.add(rightHud, BorderLayout.EAST);

        view.add(topPanel, BorderLayout.NORTH);

        gamePanel = new GamePanel(engine);
        view.add(gamePanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout(10, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(new Color(15, 15, 35));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(new Color(80, 80, 160, 120));
                g2.fillRect(0, 0, getWidth(), 2);
            }
        };
        bottomPanel.setPreferredSize(new Dimension(1280, 60));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        inputField = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(40, 40, 70));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                super.paintComponent(g);
            }
        };
        inputField.setFont(new Font("Monospaced", Font.BOLD, 22));
        inputField.setForeground(Color.WHITE);
        inputField.setCaretColor(new Color(150, 200, 255));
        inputField.setOpaque(false);
        inputField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(100, 100, 200), 2),
                BorderFactory.createEmptyBorder(4, 12, 4, 12)));
        inputField.setBackground(new Color(40, 40, 70));

        submitBtn = new JButton("SUBMIT") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isPressed() ? new Color(0, 100, 50) :
                        getModel().isRollover() ? new Color(0, 200, 100) : new Color(0, 160, 80));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Arial", Font.BOLD, 16));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString("SUBMIT", (getWidth() - fm.stringWidth("SUBMIT")) / 2,
                        (getHeight() + fm.getAscent()) / 2 - 3);
            }
        };
        submitBtn.setPreferredSize(new Dimension(120, 40));
        submitBtn.setBorderPainted(false);
        submitBtn.setContentAreaFilled(false);
        submitBtn.setFocusPainted(false);
        submitBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        submitBtn.addActionListener(e -> submitInput());

        JLabel typeHint = new JLabel("Type word → ENTER");
        typeHint.setForeground(new Color(120, 120, 160));
        typeHint.setFont(new Font("Arial", Font.PLAIN, 13));

        JPanel inputWrap = new JPanel(new BorderLayout(8, 0));
        inputWrap.setOpaque(false);
        inputWrap.add(typeHint, BorderLayout.WEST);
        inputWrap.add(inputField, BorderLayout.CENTER);

        bottomPanel.add(inputWrap, BorderLayout.CENTER);
        bottomPanel.add(submitBtn, BorderLayout.EAST);
        view.add(bottomPanel, BorderLayout.SOUTH);

        setupKeyBindings();
        return view;
    }

    private JLabel makeHudLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Arial", Font.BOLD, 18));
        lbl.setForeground(Color.WHITE);
        return lbl;
    }

    private JButton makeHudButton(String text) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? new Color(80, 80, 120) : new Color(50, 50, 90));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Arial", Font.BOLD, 13));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(text, (getWidth() - fm.stringWidth(text)) / 2,
                        (getHeight() + fm.getAscent()) / 2 - 3);
            }
        };
        btn.setPreferredSize(new Dimension(110, 36));
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void setupKeyBindings() {
        inputField.addActionListener(e -> submitInput());

        JPanel content = (JPanel) cardPanel;
        KeyStroke escape = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        KeyStroke sKey   = KeyStroke.getKeyStroke(KeyEvent.VK_S, 0);
        KeyStroke mKey   = KeyStroke.getKeyStroke(KeyEvent.VK_M, 0);

        InputMap im = gamePanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = gamePanel.getActionMap();

        im.put(escape, "pause");
        am.put("pause", new AbstractAction() {
            public void actionPerformed(ActionEvent e) { togglePause(); }
        });
        im.put(sKey, "stack");
        am.put("stack", new AbstractAction() {
            public void actionPerformed(ActionEvent e) { engine.activateStackPower(); }
        });
        im.put(mKey, "mute");
        am.put("mute", new AbstractAction() {
            public void actionPerformed(ActionEvent e) { engine.getSoundManager().toggleMute(); }
        });
    }

    private void submitInput() {
        String text = inputField.getText().trim();
        if (!text.isEmpty() && engine.isRunning() && !engine.isPaused()) {
            boolean correct = engine.processInput(text);
            if (!correct) {
                inputField.setForeground(new Color(255, 100, 100));
                Timer t = new Timer(200, ev -> inputField.setForeground(Color.WHITE));
                t.setRepeats(false);
                t.start();
            }
            inputField.setText("");
        }
        inputField.requestFocus();
    }

    private void togglePause() {
        if (engine.isPaused()) {
            engine.resumeGame();
            pauseBtn.setText("⏸ PAUSE");
        } else {
            engine.pauseGame();
            pauseBtn.setText("▶ RESUME");
        }
        gamePanel.repaint();
    }

    private void quitToMenu() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Quit to main menu? Progress will be lost.", "Quit", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            engine.pauseGame();
            showStartScreen();
        }
    }

    private void showGameScreen() {
        JTextField nameField = new JTextField("Player", 15);
        nameField.setFont(new Font("Arial", Font.PLAIN, 16));
        JPanel namePanel = new JPanel(new BorderLayout(8, 8));
        namePanel.add(new JLabel("Enter your name:"), BorderLayout.NORTH);
        namePanel.add(nameField, BorderLayout.CENTER);
        int result = JOptionPane.showConfirmDialog(this, namePanel,
                "Typing Terror", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) return;
        String playerName = nameField.getText().trim();
        if (playerName.isEmpty()) playerName = "Player";

        startScreen.stopAnimation();
        cardLayout.show(cardPanel, CARD_GAME);
        engine.startGame(playerName);
        inputField.requestFocusInWindow();
    }

    private void showStartScreen() {
        startScreen.startAnimation();
        cardLayout.show(cardPanel, CARD_START);
    }

    private void showHighScores() {
        HighScoreWindow hw = new HighScoreWindow(this, engine.getFileManager());
        hw.setVisible(true);
    }

    private void showSettings() {
        SettingsWindow sw = new SettingsWindow(this, engine.getFileManager(), engine.getSoundManager());
        sw.setVisible(true);
    }

    @Override
    public void onScoreChanged(int score, int lives, int combo) {
        SwingUtilities.invokeLater(() -> {
            scoreLabel.setText("Score: " + score);
            StringBuilder hearts = new StringBuilder();
            for (int i = 0; i < Math.max(0, lives); i++) hearts.append("❤ ");
            livesLabel.setText(hearts.length() > 0 ? hearts.toString().trim() : "☠ DEAD");
            livesLabel.setForeground(lives <= 2 ? new Color(255, 80, 80) : Color.WHITE);
            comboLabel.setText("Combo: " + combo + "x");
            comboLabel.setForeground(combo >= 10 ? new Color(255, 200, 0) : Color.WHITE);
        });
    }

    @Override
    public void onGameOver(int finalScore, boolean isNewHighScore) {
        SwingUtilities.invokeLater(() -> {
            GameOverScreen gos = new GameOverScreen(this, finalScore, isNewHighScore,
                    engine.getFileManager(),
                    () -> showGameScreen(),
                    () -> showStartScreen()
            );
            gos.setVisible(true);
        });
    }

    @Override
    public void onRepaint() {
        gamePanel.repaint();
    }

    @Override
    public void onPowerUpMessage(String msg) {
    }
}
