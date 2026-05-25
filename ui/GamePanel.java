package ui;

import dsa.MyArrayList;
import effects.FloatingText;
import effects.Particle;
import engine.GameEngine;
import engine.PowerUpManager;
import models.FallingWord;
import models.Player;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.AlphaComposite;

public class GamePanel extends JPanel {
    private GameEngine engine;

    public GamePanel(GameEngine engine) {
        this.engine = engine;
        setDoubleBuffered(true);
        setBackground(new Color(26, 26, 46));
        setPreferredSize(new Dimension(1280, 620));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int ox = engine.getShakeOffsetX();
        int oy = engine.getShakeOffsetY();
        g2d.translate(ox, oy);

        drawBackground(g2d);
        drawGroundLine(g2d);
        drawWords(g2d);
        drawParticles(g2d);
        drawFloatingTexts(g2d);
        drawComboMeter(g2d);
        drawPowerUpStatus(g2d);
        drawPowerUpMessage(g2d);

        if (engine.isPaused()) drawPauseOverlay(g2d);

        g2d.translate(-ox, -oy);
    }

    private void drawBackground(Graphics2D g) {
        GradientPaint gp = new GradientPaint(0, 0, new Color(26, 26, 46), 0, getHeight(), new Color(22, 33, 62));
        g.setPaint(gp);
        g.fillRect(0, 0, getWidth(), getHeight());

        g.setColor(new Color(255, 255, 255, 15));
        g.setStroke(new BasicStroke(1));
        for (int x = 0; x < getWidth(); x += 80) g.drawLine(x, 0, x, getHeight());
        for (int y = 0; y < getHeight(); y += 60) g.drawLine(0, y, getWidth(), y);
    }

    private void drawGroundLine(Graphics2D g) {
        int y = getHeight() - 100;
        g.setColor(new Color(255, 50, 50, 180));
        g.setStroke(new BasicStroke(2));
        g.drawLine(0, y, getWidth(), y);
        g.setColor(new Color(255, 50, 50, 50));
        g.fillRect(0, y, getWidth(), getHeight() - y);
        // Danger text
        g.setColor(new Color(255, 50, 50, 100));
        g.setFont(new Font("Arial", Font.BOLD, 12));
        g.drawString("DANGER ZONE", 10, y - 5);
    }

    private void drawWords(Graphics2D g) {
        MyArrayList<FallingWord> words = engine.getActiveWords();
        if (words == null) return;
        for (int i = 0; i < words.size(); i++) {
            try {
                FallingWord fw = words.get(i);
                if (fw != null && fw.isActive()) drawWord(g, fw);
            } catch (Exception e) {
                System.out.println("Draw word error: " + e.getMessage());
            }
        }
    }

    private void drawWord(Graphics2D g, FallingWord fw) {
        String text = fw.getWordInfo().getWord();
        Color wordColor = fw.getWordInfo().getColor();
        Font font = new Font("Monospaced", Font.BOLD, 18);
        g.setFont(font);
        FontMetrics fm = g.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        fw.setWordWidth(textWidth);
        int x = fw.getX();
        int y = fw.getY();
        int w = textWidth + 20;
        int h = 36;

        g.setColor(new Color(0, 0, 0, 80));
        g.fill(new RoundRectangle2D.Float(x - 5 + 3, y - 18 + 3, w, h, 16, 16));
        g.setColor(new Color(wordColor.getRed(), wordColor.getGreen(), wordColor.getBlue(), 80));
        g.fill(new RoundRectangle2D.Float(x - 5, y - 18, w, h, 16, 16));

        g.setColor(wordColor);
        g.setStroke(new BasicStroke(2f));
        g.draw(new RoundRectangle2D.Float(x - 5, y - 18, w, h, 16, 16));

        g.setColor(new Color(0, 0, 0, 120));
        g.drawString(text, x + 8, y + 2);

        g.setColor(Color.WHITE);
        g.drawString(text, x + 7, y + 1);

        if ("Boss".equals(fw.getWordInfo().getCategory())) {
            long t = System.currentTimeMillis();
            float pulse = (float)(Math.sin(t / 200.0) * 0.5 + 0.5);
            g.setColor(new Color(170, 0, 255, (int)(pulse * 100 + 30)));
            g.setStroke(new BasicStroke(4f));
            g.draw(new RoundRectangle2D.Float(x - 8, y - 21, w + 6, h + 6, 20, 20));
        }

        g.setStroke(new BasicStroke(1f));
    }

    private void drawParticles(Graphics2D g) {
        MyArrayList<Particle> parts = engine.getParticles();
        if (parts == null) return;
        for (int i = 0; i < parts.size(); i++) {
            try {
                Particle p = parts.get(i);
                if (p != null && p.isAlive()) p.draw(g);
            } catch (Exception e) { }
        }
    }

    private void drawFloatingTexts(Graphics2D g) {
        MyArrayList<FloatingText> texts = engine.getFloatingTexts();
        if (texts == null) return;
        for (int i = 0; i < texts.size(); i++) {
            try {
                FloatingText ft = texts.get(i);
                if (ft != null && ft.isAlive()) ft.draw(g);
            } catch (Exception e) { }
        }
    }

    private void drawComboMeter(Graphics2D g) {
        Player player = engine.getPlayer();
        if (player == null) return;
        int combo = player.getCurrentCombo();
        int maxCombo = 30;
        float ratio = Math.min(1f, (float)combo / maxCombo);

        int barX = 20;
        int barY = getHeight() - 70;
        int barW = 200;
        int barH = 18;

        g.setColor(new Color(0, 0, 0, 120));
        g.fillRoundRect(barX - 2, barY - 2, barW + 4, barH + 4, 8, 8);

        Color barColor;
        if (combo < 5)       barColor = new Color(0, 100, 255);
        else if (combo < 10) barColor = new Color(0, 200, 100);
        else if (combo < 15) barColor = new Color(200, 200, 0);
        else if (combo < 20) barColor = new Color(255, 140, 0);
        else if (combo < 25) barColor = new Color(255, 50, 50);
        else                 barColor = new Color(180, 0, 255);

        g.setColor(barColor);
        g.fillRoundRect(barX, barY, (int)(barW * ratio), barH, 8, 8);

        g.setColor(Color.WHITE);
        g.setStroke(new BasicStroke(1.5f));
        g.drawRoundRect(barX, barY, barW, barH, 8, 8);
        g.setStroke(new BasicStroke(1f));

        g.setFont(new Font("Arial", Font.BOLD, 12));
        g.setColor(Color.WHITE);
        g.drawString("COMBO: " + combo + "x", barX, barY - 5);

        double mult = player.getComboMultiplier();
        if (mult > 1.0) {
            g.setColor(new Color(255, 220, 0));
            g.setFont(new Font("Arial", Font.BOLD, 14));
            g.drawString(mult + "x BONUS", barX + barW + 10, barY + barH);
        }
    }

    private void drawPowerUpStatus(Graphics2D g) {
        PowerUpManager pm = engine.getPowerUpManager();
        if (pm == null) return;
        int x = getWidth() - 220;
        int y = getHeight() - 80;

        if (pm.isSlowTimeActive()) {
            float pct = pm.getSlowTimeRemaining() / 300f;
            g.setColor(new Color(0, 200, 255, 180));
            g.fillRoundRect(x, y, (int)(200 * pct), 16, 6, 6);
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 12));
            g.drawString("SLOW TIME", x, y - 4);
        }
        if (pm.isScoreBonusActive()) {
            float pct = pm.getScoreBonusRemaining() / 600f;
            int yy = y + (pm.isSlowTimeActive() ? 30 : 0);
            g.setColor(new Color(255, 200, 0, 180));
            g.fillRoundRect(x, yy, (int)(200 * pct), 16, 6, 6);
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 12));
            g.drawString("2x SCORE BONUS", x, yy - 4);
        }
    }

    private void drawPowerUpMessage(Graphics2D g) {
        if (engine.getPowerUpMessageTimer() <= 0) return;
        String msg = engine.getPowerUpMessage();
        if (msg == null || msg.isEmpty()) return;
        float alpha = Math.min(1f, engine.getPowerUpMessageTimer() / 60f);
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        g.setFont(new Font("Arial", Font.BOLD, 26));
        FontMetrics fm = g.getFontMetrics();
        int tw = fm.stringWidth(msg);
        int cx = (getWidth() - tw) / 2;
        int cy = getHeight() / 2 - 40;
        g.setColor(new Color(0, 0, 0, 160));
        g.fillRoundRect(cx - 20, cy - 30, tw + 40, 50, 20, 20);
        g.setColor(new Color(255, 220, 0));
        g.drawString(msg, cx, cy);
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
    }

    private void drawPauseOverlay(Graphics2D g) {
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRect(0, 0, getWidth(), getHeight());
        g.setFont(new Font("Arial", Font.BOLD, 60));
        g.setColor(Color.WHITE);
        String msg = engine.isPaused() ? "PAUSED" : "";
        if (!msg.isEmpty()) {
            FontMetrics fm = g.getFontMetrics();
            int tw = fm.stringWidth(msg);
            g.drawString(msg, (getWidth() - tw) / 2, getHeight() / 2);
            g.setFont(new Font("Arial", Font.PLAIN, 24));
            String sub = "Press P or ESC to resume";
            fm = g.getFontMetrics();
            g.drawString(sub, (getWidth() - fm.stringWidth(sub)) / 2, getHeight() / 2 + 50);
        }
    }
}
