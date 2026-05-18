package effects;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.AlphaComposite;

public class FloatingText {
    private float x, y;
    private String text;
    private int life;
    private int maxLife = 48;
    private float alpha;
    private Color color;
    private Font font;

    public FloatingText(int x, int y, String text, Color color) {
        this.x = x;
        this.y = y;
        this.text = text;
        this.life = maxLife;
        this.alpha = 1.0f;
        this.color = color;
        this.font = new Font("Arial", Font.BOLD, 20);
    }

    public void update() {
        y -= 2;
        life--;
        alpha = (float) life / maxLife;
    }

    public boolean isAlive() {
        return life > 0;
    }

    public void draw(Graphics g) {
        if (!isAlive()) return;
        Graphics2D g2d = (Graphics2D) g;
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, Math.max(0f, alpha)));
        g2d.setFont(font);

        g2d.setColor(Color.BLACK);
        g2d.drawString(text, (int)x + 2, (int)y + 2);

        g2d.setColor(color);
        g2d.drawString(text, (int)x, (int)y);
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
    }
}
