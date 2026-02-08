package auto.shulker.razstr.team.strange.ui.clickgui.render;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;
import auto.shulker.razstr.team.strange.Strange;
import auto.shulker.razstr.team.strange.module.Theme;
import auto.shulker.razstr.team.strange.module.ThemeManager;
import auto.shulker.razstr.team.strange.module.api.Category;
import auto.shulker.razstr.team.strange.ui.clickgui.GuiScreen;
import auto.shulker.razstr.team.strange.utils.FontDraw;
import auto.shulker.razstr.team.strange.utils.RenderUtil;

import java.awt.Color;

public class GuiRenderTheme extends GuiScreen {
    public static void renderTheme(DrawContext ctx) {
        if (selectedCategories != Category.Theme) return;
        boolean themea = ThemeManager.getTheme() == Theme.TRANSPARENT_WHITE || ThemeManager.getTheme() == Theme.TRANSPARENT_BLACK
                || ThemeManager.getTheme() == Theme.PURPLE || ThemeManager.getTheme() == Theme.PINK;
        boolean blackTheme = ThemeManager.getTheme() == Theme.BLACK;
        float startXLeft = x + 7;
        float startXRight = x + 7 + 110;
        float startY = y + 64;

        float yDown = 0;

        for (int index = 0; index < themes.length; index++) {
            Theme theme = themes[index];
            boolean leftColumn = index % 2 == 0;
            float drawX = leftColumn ? startXLeft : startXRight;
            float drawY = startY + yDown;

            RenderUtil.Border.draw(ctx, drawX, drawY, 102, 26, 2.5f, 0.1f,
                    blackTheme ? RenderUtil.ColorUtil.replAlpha(new Color(0xFFFFFF).getRGB(), 80) : themea ? RenderUtil.ColorUtil.replAlpha(new Color(0x000000).getRGB(), 40) : RenderUtil.ColorUtil.replAlpha(new Color(0x000000).getRGB(), 80));

            RenderUtil.Round.draw(ctx, drawX, drawY, 102, 26, 2.5f, themea ? RenderUtil.ColorUtil.replAlpha(RenderUtil.ColorUtil.getBackGroundColor(1, 1), 57) : RenderUtil.ColorUtil.getBackGroundColor(1, 1));

            String name = theme.toString().toLowerCase();
            Identifier texId = Identifier.of(Strange.rootRes, "textures/theme/" + name + ".png");
            RenderUtil.Image.draw(ctx, texId, drawX - 0.5f, drawY, 102 + 1, 14f, new Color(255, 255, 255));

            FontDraw.drawText(FontDraw.FontType.MEDIUM, ctx, theme.getName(), drawX + 5, drawY + 20, 6, RenderUtil.ColorUtil.getTextColor(1, 1));

            if (!leftColumn) {
                yDown += 26 + 4;
            }
        }
    }
}
