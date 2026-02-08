package auto.shulker.razstr.team.strange.utils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

/**
 * Рисует текст через Minecraft textRenderer (без кастомного шрифта).
 */
public class FontDraw {

    public static void drawText(FontType f, DrawContext ctx, String text, float x, float y, int size, int color) {
        drawText(f, ctx, text, x, y, size, color, false);
    }

    public static void drawText(FontType f, DrawContext ctx, String text, float x, float y, int size, int color, boolean shadow) {
        if (size <= 0) size = 1;
        float scale = size / 9f;
        ctx.getMatrices().push();
        ctx.getMatrices().scale(scale, scale, 1f);
        float sx = x / scale;
        float sy = y / scale;
        if (shadow) {
            ctx.drawText(MinecraftClient.getInstance().textRenderer, text, (int) sx, (int) sy, color, true);
        } else {
            ctx.drawText(MinecraftClient.getInstance().textRenderer, text, (int) sx, (int) sy, color, false);
        }
        ctx.getMatrices().pop();
    }

    public static float getWidth(FontType f, String text, int size) {
        if (size <= 0) size = 1;
        float scale = size / 9f;
        return MinecraftClient.getInstance().textRenderer.getWidth(text) * scale;
    }

    public enum FontType { MEDIUM, SEMIBOLD }
}
