package auto.shulker.razstr.team.strange.ui.clickgui.mouse;

import auto.shulker.razstr.team.strange.module.api.Module;
import auto.shulker.razstr.team.strange.module.api.setting.Setting;
import auto.shulker.razstr.team.strange.ui.clickgui.GuiScreen;

import java.util.ArrayList;
import java.util.List;

public class GuiMouseClickedModule extends GuiScreen {
    public static boolean clickedModule(double mouseX, double mouseY, int button) {

        float yDown = 0;
        float scrollY = scroll.getScroll();

        for (Module module : modules) {
            float up = calcUP(module);

            float drawY = y + 64 + yDown + scrollY;

            if (isHovered(mouseX, mouseY, x + 7, drawY, 211, 26)) {
                if (button == 0) {
                    module.toggle();
                    return true;
                }

                if (button == 1) {
                    module.open = !module.open;
                    return true;
                }

                if (button == 2) {
                    module.binding = true;
                    module.displayName = "Нажмите кнопку";
                    return true;
                }
            }

            if (!module.getSettingsForGUI().isEmpty() && module.open) {
                List<Setting> settings1 = new ArrayList<>();
                List<Setting> settings2 = new ArrayList<>();

                for (int i = 0; i < module.getSettingsForGUI().size(); i++) {
                    Setting setting = module.getSettingsForGUI().get(i);
                    if (i % 2 == 0) {
                        settings1.add(setting);
                    } else {
                        settings2.add(setting);
                    }
                }
                if (GuiMouseClickedSettings.clickedSettings(settings1, mouseX, mouseY, x + 7, drawY + 26)) {
                    return true;
                }
                if (GuiMouseClickedSettings.clickedSettings(settings2, mouseX, mouseY, x + 109, drawY + 26)) {
                    return true;
                }
            }

            yDown += 30 + up;
        }
        return false;
    }
}
