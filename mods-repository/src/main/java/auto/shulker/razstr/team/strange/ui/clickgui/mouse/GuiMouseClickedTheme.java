package auto.shulker.razstr.team.strange.ui.clickgui.mouse;

import auto.shulker.razstr.team.strange.module.Theme;
import auto.shulker.razstr.team.strange.module.ThemeManager;
import auto.shulker.razstr.team.strange.module.api.Category;
import auto.shulker.razstr.team.strange.ui.clickgui.GuiScreen;

public class GuiMouseClickedTheme extends GuiScreen {
    public static boolean clickedTheme(double mouseX, double mouseY) {
        if (selectedCategories != Category.Theme) return false;

        float startXLeft = x + 7;
        float startXRight = x + 7 + 110;
        float startY = y + 64;

        float yDown = 0;

        for (int index = 0; index < themes.length; index++) {
            Theme theme = themes[index];
            boolean leftColumn = index % 2 == 0;
            float drawX = leftColumn ? startXLeft : startXRight;
            float drawY = startY + yDown;

            if (isHovered(mouseX, mouseY, drawX, drawY, 102, 26)) {
                ThemeManager.setTheme(theme);
                return true;
            }

            if (!leftColumn) {
                yDown += 26 + 4;
            }
        }
        return false;
    }
}
