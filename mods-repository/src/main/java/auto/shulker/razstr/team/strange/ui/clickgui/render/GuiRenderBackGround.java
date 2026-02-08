package auto.shulker.razstr.team.strange.ui.clickgui.render;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;
import auto.shulker.razstr.team.strange.module.Theme;
import auto.shulker.razstr.team.strange.module.ThemeManager;
import auto.shulker.razstr.team.strange.Strange;
import auto.shulker.razstr.team.strange.ui.clickgui.GuiScreen;
import auto.shulker.razstr.team.strange.utils.FontDraw;
import auto.shulker.razstr.team.strange.utils.RenderUtil;

import java.awt.Color;

public class GuiRenderBackGround extends GuiScreen {
    public static void renderBackGround(DrawContext ctx) {
        boolean theme = ThemeManager.getTheme() == Theme.TRANSPARENT_WHITE || ThemeManager.getTheme() == Theme.TRANSPARENT_BLACK
                || ThemeManager.getTheme() == Theme.PURPLE || ThemeManager.getTheme() == Theme.PINK;
        RenderUtil.Shadow.draw(ctx, x - 2, y - 2, width, height, 8, 12, new Color(0x40000000, true).getRGB());
        if (theme) RenderUtil.Blur.draw(ctx, x, y, width, height, 8, 20, new Color(255, 255, 255));
        RenderUtil.Round.draw(ctx, x, y, width, height, 8, theme ? RenderUtil.ColorUtil.replAlpha(RenderUtil.ColorUtil.getMainColor(1, 1), 127) : RenderUtil.ColorUtil.getBackGroundColor(1, 1));
        try {
            RenderUtil.Image.draw(ctx, Identifier.of(Strange.rootRes, "textures/gui/gui-logoblack.png"), x + 8, y + 8, 16, 16, RenderUtil.ColorUtil.replAlpha(RenderUtil.ColorUtil.getTextColor(1, 1), 204));
        } catch (Exception ignored) {}
        FontDraw.drawText(FontDraw.FontType.MEDIUM, ctx, Strange.name, x + 28, y + 15, 8, RenderUtil.ColorUtil.replAlpha(RenderUtil.ColorUtil.getTextColor(1, 1), 204));
        FontDraw.drawText(FontDraw.FontType.MEDIUM, ctx, "MOD", x + 28, y + 22, 5, RenderUtil.ColorUtil.replAlpha(RenderUtil.ColorUtil.getTextColor(1, 1), 127));
    }
}
