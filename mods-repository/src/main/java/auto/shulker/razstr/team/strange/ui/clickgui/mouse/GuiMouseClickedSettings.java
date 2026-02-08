package auto.shulker.razstr.team.strange.ui.clickgui.mouse;

import auto.shulker.razstr.team.strange.module.api.setting.Setting;
import auto.shulker.razstr.team.strange.module.api.setting.impl.BooleanSetting;
import auto.shulker.razstr.team.strange.module.api.setting.impl.ModeSetting;
import auto.shulker.razstr.team.strange.module.api.setting.impl.StringSetting;
import auto.shulker.razstr.team.strange.ui.clickgui.GuiScreen;

import java.util.List;

public class GuiMouseClickedSettings extends GuiScreen {
    public static boolean clickedSettings(List<Setting> settings, double mouseX, double mouseY, float x, float y) {
        float up = 0;
        for (Setting setting : settings) {
            float widthSettings = 109;
            float heightSettings = 16;

            float xSettings = x;
            float ySettings = y + up;

            if (setting instanceof StringSetting) {
                StringSetting s = (StringSetting) setting;
                if (s.hidden.get()) continue;
                if (isHovered(mouseX, mouseY, xSettings + 58, ySettings + 4, 40, 10)) {
                    s.active = !s.active;
                } else {
                    s.active = false;
                }
                up += heightSettings + 4;
            }
            if (setting instanceof ModeSetting) {
                ModeSetting s = (ModeSetting) setting;
                if (s.hidden.get()) continue;
                if (isHovered(mouseX, mouseY, xSettings + 58, ySettings + 4, 40, 10)) {
                    s.opened = !s.opened;
                }
                if (s.opened) {
                    for (int i = 0; i < s.modes.size(); i++) {
                        if (isHovered(mouseX, mouseY, xSettings + 58, ySettings + 11 + 5 + i * 6, 40, 6)) {
                            s.set(s.modes.get(i));
                            return true;
                        }
                    }
                    up += s.modes.size() * 6;
                }
                up += heightSettings + 4;
            }
            if (setting instanceof BooleanSetting) {
                BooleanSetting s = (BooleanSetting) setting;
                if (s.hidden.get()) continue;
                if (isHovered(mouseX, mouseY, xSettings + 58, ySettings + 4, 40, 10)) {
                    s.toggle();
                    return true;
                }
                up += heightSettings + 4;
            }
        }
        return false;
    }
}
