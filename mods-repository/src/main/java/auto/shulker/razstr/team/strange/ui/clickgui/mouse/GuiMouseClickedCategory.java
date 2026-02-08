package auto.shulker.razstr.team.strange.ui.clickgui.mouse;

import auto.shulker.razstr.team.strange.Strange;
import auto.shulker.razstr.team.strange.module.api.Category;
import auto.shulker.razstr.team.strange.ui.clickgui.GuiScreen;
import auto.shulker.razstr.team.strange.utils.FontDraw;

public class GuiMouseClickedCategory extends GuiScreen {
    public static boolean clickedCategory(double mouseX, double mouseY) {
        float xGo = 0;
        for (Category category : categories) {
            if (isHovered(mouseX, mouseY, x + 10 + xGo, y + 31, FontDraw.getWidth(FontDraw.FontType.MEDIUM, category.getName(), 5) + 6, 10)) {
                if (selectedCategories != category) {
                    selectedCategories = category;
                    GuiScreen.modules = Strange.get.manager.getType(GuiScreen.selectedCategories);
                    return true;
                }
            }
            xGo += 9 + FontDraw.getWidth(FontDraw.FontType.MEDIUM, category.getName(), 5);
        }
        return false;
    }
}
