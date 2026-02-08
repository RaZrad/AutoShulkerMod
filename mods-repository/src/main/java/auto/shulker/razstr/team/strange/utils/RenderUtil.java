package auto.shulker.razstr.team.strange.utils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;
import auto.shulker.razstr.team.strange.module.Theme;
import auto.shulker.razstr.team.strange.module.ThemeManager;
import auto.shulker.razstr.team.strange.ui.clickgui.GuiScreen;

import java.awt.Color;

/**
 * Упрощённый RenderUtil на DrawContext (без кастомного рендер-пайплайна).
 */
public class RenderUtil {

    public static MinecraftClient mc = MinecraftClient.getInstance();

    private static void drawRound(DrawContext ctx, float x, float y, float w, float h, float r, int color) {
        int ri = Math.min((int) r, (int) Math.min(w, h) / 2);
        if (ri <= 0) {
            ctx.fill((int) x, (int) y, (int) (x + w), (int) (y + h), color);
            return;
        }
        int left = (int) x, top = (int) y, right = (int) (x + w), bottom = (int) (y + h);
        ctx.fill(left + ri, top, right - ri, bottom, color);
        ctx.fill(left, top + ri, left + ri, bottom - ri, color);
        ctx.fill(right - ri, top + ri, right, bottom - ri, color);
        for (int dy = 0; dy < ri; dy++) {
            for (int dx = 0; dx < ri; dx++) {
                int cx = dx - ri, cy = dy - ri;
                if (cx * cx + cy * cy <= ri * ri) {
                    ctx.fill(left + dx, top + dy, left + dx + 1, top + dy + 1, color);
                }
                int rx = ri - 1 - dx;
                if (rx * rx + cy * cy <= ri * ri) {
                    ctx.fill(right - 1 - dx, top + dy, right - dx, top + dy + 1, color);
                }
                int by = ri - 1 - dy;
                if (cx * cx + by * by <= ri * ri) {
                    ctx.fill(left + dx, bottom - 1 - dy, left + dx + 1, bottom - dy, color);
                }
                if (rx * rx + by * by <= ri * ri) {
                    ctx.fill(right - 1 - dx, bottom - 1 - dy, right - dx, bottom - dy, color);
                }
            }
        }
    }

    public static class Round {
        public static void draw(DrawContext ctx, float x, float y, float w, float h, float radius, int color) {
            drawRound(ctx, x, y, w, h, radius, color);
        }
        public static void draw(DrawContext ctx, float x, float y, float w, float h, float radius, Color color) {
            draw(ctx, x, y, w, h, radius, toARGB(color));
        }
    }

    public static class Shadow {
        public static void draw(DrawContext ctx, float x, float y, float w, float h, float radius, float blur, int color) {
            drawRound(ctx, x - 2, y - 2, w + 4, h + 4, radius + 2, color);
        }
    }

    public static class Blur {
        public static void draw(DrawContext ctx, float x, float y, float w, float h, float radius, float blur, int color) {
            drawRound(ctx, x, y, w, h, radius, color);
        }
        public static void draw(DrawContext ctx, float x, float y, float w, float h, float radius, float blur, Color color) {
            draw(ctx, x, y, w, h, radius, blur, toARGB(color));
        }
    }

    public static class Border {
        public static void draw(DrawContext ctx, float x, float y, float w, float h, float radius, float thickness, int color) {
            drawRound(ctx, x, y, w, h, radius, color);
        }
    }

    public static class Image {
        public static void draw(DrawContext ctx, Identifier id, float x, float y, float w, float h, int color) {
            try {
                ctx.drawTexture(RenderLayer::getGuiTextured, id, (int) x, (int) y, 0f, 0f, (int) w, (int) h, (int) w, (int) h);
            } catch (Exception ignored) {}
        }
        public static void draw(DrawContext ctx, Identifier id, float x, float y, float w, float h, Color color) {
            draw(ctx, id, x, y, w, h, toARGB(color));
        }
    }

    public static class ColorUtil {
        public static int getBackGroundColor(int speed, int index) {
            Theme t = ThemeManager.getTheme();
            return t.getBg().getRGB();
        }
        public static int getMainColor(int speed, int index) {
            Theme t = ThemeManager.getTheme();
            return t.getMain().getRGB();
        }
        public static int getTextColor(int speed, int index) {
            Theme t = ThemeManager.getTheme();
            return t.getText().getRGB();
        }
        public static int replAlpha(int c, int a) {
            return (c & 0x00FFFFFF) | (a << 24);
        }
        public static int red(int c) { return c >> 16 & 0xFF; }
        public static int green(int c) { return c >> 8 & 0xFF; }
        public static int blue(int c) { return c & 0xFF; }
        public static int alpha(int c) { return c >> 24 & 0xFF; }
        public static int getColor(int r, int g, int b, int a) {
            return (a << 24) | (r << 16) | (g << 8) | b;
        }
    }

    private static int toARGB(Color c) {
        return (c.getAlpha() << 24) | (c.getRed() << 16) | (c.getGreen() << 8) | c.getBlue();
    }
}
