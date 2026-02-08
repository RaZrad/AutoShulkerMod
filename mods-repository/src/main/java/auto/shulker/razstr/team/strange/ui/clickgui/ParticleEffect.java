package auto.shulker.razstr.team.strange.ui.clickgui;

import net.minecraft.client.gui.DrawContext;

import java.util.ArrayList;
import java.util.List;

/**
 * Красивый эффект с летающими точками и соединениями между ними
 */
public class ParticleEffect {
    private static final int PARTICLE_COUNT = 20;
    private static final float CONNECTION_DISTANCE = 150.0f;
    private static final float PARTICLE_SPEED = 1.5f;

    private final List<Particle> particles = new ArrayList<>();
    private float baseX, baseY, width, height;
    private float mouseX = 0, mouseY = 0;

    public ParticleEffect(float x, float y, float width, float height) {
        this.baseX = x;
        this.baseY = y;
        this.width = width;
        this.height = height;
        initParticles();
    }

    private void initParticles() {
        particles.clear();
        for (int i = 0; i < PARTICLE_COUNT; i++) {
            float x = baseX + (float) (Math.random() * width);
            float y = baseY + (float) (Math.random() * height);
            float vx = (float) (Math.random() * 2 - 1) * PARTICLE_SPEED;
            float vy = (float) (Math.random() * 2 - 1) * PARTICLE_SPEED;
            particles.add(new Particle(x, y, vx, vy));
        }
    }

    public void update(float mouseX, float mouseY) {
        this.mouseX = mouseX;
        this.mouseY = mouseY;

        for (Particle p : particles) {
            p.update();

            // Границы
            if (p.x < baseX) {
                p.x = baseX;
                p.vx = Math.abs(p.vx);
            }
            if (p.x > baseX + width) {
                p.x = baseX + width;
                p.vx = -Math.abs(p.vx);
            }
            if (p.y < baseY) {
                p.y = baseY;
                p.vy = Math.abs(p.vy);
            }
            if (p.y > baseY + height) {
                p.y = baseY + height;
                p.vy = -Math.abs(p.vy);
            }

            // Притяжение к мышке
            float dx = mouseX - p.x;
            float dy = mouseY - p.y;
            float distance = (float) Math.sqrt(dx * dx + dy * dy);

            if (distance < CONNECTION_DISTANCE && distance > 5) {
                float force = 0.3f / (distance + 1);
                p.vx += (dx / distance) * force;
                p.vy += (dy / distance) * force;

                // Ограничиваем скорость
                float speed = (float) Math.sqrt(p.vx * p.vx + p.vy * p.vy);
                if (speed > 3.0f) {
                    p.vx = (p.vx / speed) * 3.0f;
                    p.vy = (p.vy / speed) * 3.0f;
                }
            }
        }
    }

    private void drawLine(DrawContext context, float x1, float y1, float x2, float y2, int color) {
        float dx = x2 - x1;
        float dy = y2 - y1;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);

        if (distance < 1) {
            context.fill((int) x1 - 1, (int) y1 - 1, (int) x1 + 1, (int) y1 + 1, color);
            return;
        }

        int steps = (int) distance + 1;
        for (int i = 0; i <= steps; i++) {
            float t = (float) i / steps;
            int x = (int) (x1 + dx * t);
            int y = (int) (y1 + dy * t);
            context.fill(x, y, x + 1, y + 1, color);
        }
    }

    public void render(DrawContext context) {
        // Рисуем соединяющие линии между частицами
        for (int i = 0; i < particles.size(); i++) {
            Particle p1 = particles.get(i);
            for (int j = i + 1; j < particles.size(); j++) {
                Particle p2 = particles.get(j);
                float dx = p2.x - p1.x;
                float dy = p2.y - p1.y;
                float distance = (float) Math.sqrt(dx * dx + dy * dy);

                if (distance < CONNECTION_DISTANCE) {
                    int alpha = (int) (255 * (1 - distance / CONNECTION_DISTANCE) * 0.3f);
                    if (alpha > 10) {
                        int color = (alpha << 24) | 0x00FF00; // Зелёный цвет
                        drawLine(context, p1.x, p1.y, p2.x, p2.y, color);
                    }
                }
            }
        }

        // Соединения с мышкой
        for (Particle p : particles) {
            float dx = p.x - mouseX;
            float dy = p.y - mouseY;
            float distance = (float) Math.sqrt(dx * dx + dy * dy);

            if (distance < CONNECTION_DISTANCE) {
                int alpha = (int) (255 * (1 - distance / CONNECTION_DISTANCE) * 0.4f);
                if (alpha > 10) {
                    int color = (alpha << 24) | 0xFF44FF; // Фиолетовый
                    drawLine(context, p.x, p.y, mouseX, mouseY, color);
                }
            }
        }

        // Рисуем сами точки
        for (Particle p : particles) {
            int color = 0xFF00FF00; // Зелёный ARGB
            context.fill((int) p.x - 2, (int) p.y - 2, (int) p.x + 2, (int) p.y + 2, color);
        }
    }

    public void setBounds(float x, float y, float width, float height) {
        this.baseX = x;
        this.baseY = y;
        this.width = width;
        this.height = height;
    }

    private static class Particle {
        float x, y;
        float vx, vy;

        Particle(float x, float y, float vx, float vy) {
            this.x = x;
            this.y = y;
            this.vx = vx;
            this.vy = vy;
        }

        void update() {
            x += vx;
            y += vy;

            // Трение
            vx *= 0.98f;
            vy *= 0.98f;
        }
    }
}
