package effects;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.AlphaComposite;

public class Particle {
    private float x, y;
    private float vx, vy;
    private int life;
    private int maxLife;
    private Color color;
    private int size;

    public Particle(float x, float y, float vx, float vy, int life, Color color, int size) {
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.life = life;
        this.maxLife = life;
        this.color = color;
        this.size = size;
    }

    public void update() {
        x += vx;
        y += vy;
        vy += 0.2f; // gravity
        life--;
    }

    public boolean isAlive() {
        return life > 0;
    }

    public int getAlpha() {
        return (int)((float)life / maxLife * 255);
    }

    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        int alpha = getAlpha();
        if (alpha <= 0) return;
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha / 255f));
        g2d.setColor(color);
        g2d.fillOval((int)x - size/2, (int)y - size/2, size, size);
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
    }

    public float getX() { return x; }
    public float getY() { return y; }
}
