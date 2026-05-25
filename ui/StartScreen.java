package ui;

import effects.Particle;
import dsa.MyArrayList;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.AlphaComposite;
import java.util.Random;

public class StartScreen extends JPanel {
    private Runnable onStart;
    private Runnable onHighScores;
    private Runnable onSettings;
    private Runnable onExit;

    private MyArrayList<Particle> bgParticles = new MyArrayList<>();
    private Random random = new Random();
    private Timer animTimer;
    private float pulseAngle = 0;

    public StartScreen(Runnable onStart, Runnable onHighScores, Runnable onSettings, Runnable onExit) {
        this.onStart = onStart;
        this.onHighScores = onHighScores;
        this.onSettings = onSettings;
        this.onExit = onExit;

        setPreferredSize(new Dimension(1280, 720));
        setLayout(null);
        setBackground(new Color(26, 26, 46));

        initParticles();
        setupButtons();
        startAnimation();
    }

    private void initParticles() {
        for (int i = 0; i < 60; i++) {
            float x = random.nextFloat() * 1280;
            float y = random.nextFloat() * 720;
            float vx = (random.nextFloat() - 0.5f) * 1.5f;
            float vy = (random.nextFloat() - 0.5f) * 1.5f;
            Color c = new Color(random.nextInt(100) + 100, random.nextInt(100) + 100, 255);
            bgParticles.add(new Particle(x, y, vx, vy, 9999, c, 2 + random.nextInt(3)));
        }
    }

    public void startAnimation() {
        if (animTimer != null && animTimer.isRunning()) return;
        animTimer = new Timer(16, e -> {
            pulseAngle += 0.05f;
            repaint();
        });
        animTimer.start();
    }

    private void updateParticles() {
        for (int i = 0; i < bgParticles.size(); i++) {
            Particle p = bgParticles.get(i);
        }
    }

    private JButton makeButton(String text, int x, int y, int w, int h, Color base) {
        JButton btn = new JButton(text) {
            private boolean hovered = false;
            {
                addMouseListener(new MouseAdapter() {
                    public void mouseEntered(MouseEvent e) { hovered = true; repaint(); }
                    public void mouseExited(MouseEvent e) { hovered = false; repaint(); }
                });
            }
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color fill = hovered ? base.brighter() : base;
                g2.setColor(fill);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Arial", Font.BOLD, 18));
                FontMetrics fm = g2.getFontMetrics();
                int tx = (getWidth() - fm.stringWidth(text)) / 2;
                int ty = (getHeight() + fm.getAscent()) / 2 - 3;
                g2.drawString(text, tx, ty);
            }
        };
        btn.setBounds(x, y, w, h);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void setupButtons() {
        int cx = 490, bw = 300, bh = 55, gap = 70;
        int startY = 340;

        JButton startBtn = makeButton("▶  START GAME", cx, startY, bw, bh, new Color(0, 160, 80));
        startBtn.addActionListener(e -> { if (onStart != null) onStart.run(); });
        add(startBtn);

        JButton hsBtn = makeButton("🏆  HIGH SCORES", cx, startY + gap, bw, bh, new Color(200, 140, 0));
        hsBtn.addActionListener(e -> { if (onHighScores != null) onHighScores.run(); });
        add(hsBtn);

        JButton settBtn = makeButton("⚙  SETTINGS", cx, startY + gap * 2, bw, bh, new Color(60, 80, 200));
        settBtn.addActionListener(e -> { if (onSettings != null) onSettings.run(); });
        add(settBtn);

        JButton exitBtn = makeButton("✕  EXIT", cx, startY + gap * 3, bw, bh, new Color(180, 40, 40));
        exitBtn.addActionListener(e -> { if (onExit != null) onExit.run(); });
        add(exitBtn);
    }

    public void stopAnimation() {
        if (animTimer != null) animTimer.stop();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        GradientPaint gp = new GradientPaint(0, 0, new Color(10, 10, 30), 0, getHeight(), new Color(30, 10, 60));
        g2.setPaint(gp);
        g2.fillRect(0, 0, getWidth(), getHeight());

        for (int i = 0; i < bgParticles.size(); i++) {
            try {
                Particle p = bgParticles.get(i);
                g2.setColor(new Color(200, 200, 255, 150));
                int px = (int)(System.currentTimeMillis() / 20 * (i % 3 + 1) * 0.02 + i * 47) % getWidth();
                int py = (i * 53 + 100) % getHeight();
                g2.fillOval(px, py, 2, 2);
            } catch (Exception ignored) {}
        }

        drawTitle(g2);

        g2.setFont(new Font("Arial", Font.PLAIN, 20));
        g2.setColor(new Color(150, 150, 200));
        String sub = "Type words before they fall! — DSA Arcade Game";
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(sub, (getWidth() - fm.stringWidth(sub)) / 2, 280);

        float alpha = (float)(Math.sin(pulseAngle) * 0.4 + 0.6);
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        g2.setFont(new Font("Arial", Font.BOLD, 16));
        g2.setColor(new Color(100, 200, 255));
        String hint = "Press ENTER to start";
        fm = g2.getFontMetrics();
        g2.drawString(hint, (getWidth() - fm.stringWidth(hint)) / 2, 650);
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
    }

    private void drawTitle(Graphics2D g2) {
        String title = "TYPING TERROR";
        Font titleFont = new Font("Arial", Font.BOLD, 80);
        g2.setFont(titleFont);
        FontMetrics fm = g2.getFontMetrics();
        int tx = (getWidth() - fm.stringWidth(title)) / 2;
        int ty = 220;

        g2.setColor(new Color(180, 0, 255, 60));
        for (int d = 8; d >= 1; d--) g2.drawString(title, tx + d, ty + d);

        g2.setColor(new Color(200, 100, 255, 80));
        g2.drawString(title, tx - 2, ty);
        g2.drawString(title, tx + 2, ty);
        g2.drawString(title, tx, ty - 2);
        g2.drawString(title, tx, ty + 2);


        GradientPaint gp = new GradientPaint(tx, ty - 80, new Color(255, 150, 255), tx, ty, new Color(150, 50, 255));
        g2.setPaint(gp);
        g2.drawString(title, tx, ty);
    }
}
