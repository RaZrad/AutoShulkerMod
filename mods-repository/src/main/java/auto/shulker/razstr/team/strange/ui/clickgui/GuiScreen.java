package auto.shulker.razstr.team.strange.ui.clickgui;

import net.minecraft.client.MinecraftClient;
import auto.shulker.razstr.team.strange.module.Theme;
import auto.shulker.razstr.team.strange.module.api.Category;
import auto.shulker.razstr.team.strange.module.api.Module;
import auto.shulker.razstr.team.strange.module.api.setting.Setting;
import auto.shulker.razstr.team.strange.module.api.setting.impl.BooleanSetting;
import auto.shulker.razstr.team.strange.module.api.setting.impl.ModeSetting;
import auto.shulker.razstr.team.strange.module.api.setting.impl.StringSetting;
import auto.shulker.razstr.team.strange.utils.ScrollUtil;
import auto.shulker.razstr.team.strange.utils.FontDraw;
import auto.shulker.razstr.team.strange.utils.RenderUtil;

import java.util.ArrayList;
import java.util.List;

public class GuiScreen {
    public static MinecraftClient mc = MinecraftClient.getInstance();
    public static ScrollUtil scroll = new ScrollUtil();

    public static float x, y;
    public static float width, height;

    public static Category[] categories;
    public static List<Module> modules;
    public static Theme[] themes;
    public static Category selectedCategories = Category.Utilities;
    public static Theme selectedTheme = Theme.WHITE;
    public static Theme preSelectedTheme;

    public static boolean isHovered(double mouseX, double mouseY, float x, float y, float width, float height) {
        return mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
    }

    public static float calcUP(Module module) {
        List<Setting> settings1 = new ArrayList<>();
        List<Setting> settings2 = new ArrayList<>();
        for (int i = 0; i < module.getSettingsForGUI().size(); i++) {
            Setting s = module.getSettingsForGUI().get(i);
            (i % 2 == 0 ? settings1 : settings2).add(s);
        }
        float up1 = 0, up2 = 0;
        float h = 16;
        if (!module.getSettingsForGUI().isEmpty() && module.open) {
            for (Setting setting : settings1) {
                if (setting instanceof StringSetting || setting instanceof BooleanSetting) up1 += h + 4;
                if (setting instanceof ModeSetting) {
                    ModeSetting s = (ModeSetting) setting;
                    if (s.opened) up1 += s.modes.size() * 6;
                    up1 += h + 4;
                }
            }
            for (Setting setting : settings2) {
                if (setting instanceof StringSetting || setting instanceof BooleanSetting) up2 += h + 4;
                if (setting instanceof ModeSetting) {
                    ModeSetting s = (ModeSetting) setting;
                    if (s.opened) up2 += s.modes.size() * 6;
                    up2 += h + 4;
                }
            }
        }
        return Math.max(up1, up2);
    }
}
