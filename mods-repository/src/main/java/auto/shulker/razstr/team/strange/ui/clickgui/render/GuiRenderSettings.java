package auto.shulker.razstr.team.strange.ui.clickgui.render;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;
import auto.shulker.razstr.team.strange.Strange;
import auto.shulker.razstr.team.strange.module.Theme;
import auto.shulker.razstr.team.strange.module.ThemeManager;
import auto.shulker.razstr.team.strange.module.api.setting.Setting;
import auto.shulker.razstr.team.strange.module.api.setting.impl.BooleanSetting;
import auto.shulker.razstr.team.strange.module.api.setting.impl.ModeSetting;
import auto.shulker.razstr.team.strange.module.api.setting.impl.StringSetting;
import auto.shulker.razstr.team.strange.ui.clickgui.GuiScreen;
import auto.shulker.razstr.team.strange.utils.FontDraw;
import auto.shulker.razstr.team.strange.utils.RenderUtil;

import java.awt.Color;
import java.util.List;

public class GuiRenderSettings extends GuiScreen {
    public static void renderSettings(DrawContext ctx, List<Setting> settings, float x, float y, double mouseX, double mouseY) {
        boolean themea = ThemeManager.getTheme() == Theme.TRANSPARENT_WHITE || ThemeManager.getTheme() == Theme.TRANSPARENT_BLACK
                || ThemeManager.getTheme() == Theme.PURPLE || ThemeManager.getTheme() == Theme.PINK;

        float up = 0;
        for (Setting setting : settings) {
            float widthSettings = 109;
            float heightSettings = 16;

            float xSettings = x;
            float ySettings = y + up;

            if (setting instanceof StringSetting) {
                StringSetting s = (StringSetting) setting;
                if (s.hidden.get()) continue;
                FontDraw.drawText(FontDraw.FontType.MEDIUM, ctx, setting.name, xSettings + 6, ySettings + 11, 5, RenderUtil.ColorUtil.getTextColor(1, 1));
                RenderUtil.Round.draw(ctx, xSettings + 58, ySettings + 4, 40, 10, 1.5f, themea ? RenderUtil.ColorUtil.replAlpha(RenderUtil.ColorUtil.getMainColor(1, 1), 125) : RenderUtil.ColorUtil.getMainColor(1, 1));
                RenderUtil.Image.draw(ctx, Identifier.of(Strange.rootRes, "icons/gui/t_s.png"), xSettings + 89, ySettings + 5, 8, 8, RenderUtil.ColorUtil.getTextColor(1, 1));

                String textS = (s.get().isEmpty() && !s.active) ? "..." : s.get() + (s.active ? (System.currentTimeMillis() % 1000 >= 500 ? " " : "_") : " ");
                if (textS.length() > 8) {
                    textS = textS.substring(0, 8) + "...";
                }
                FontDraw.drawText(FontDraw.FontType.MEDIUM, ctx, textS, xSettings + 61, ySettings + 11, 5, RenderUtil.ColorUtil.getTextColor(1, 1));

                up += heightSettings + 4;
            }
            if (setting instanceof ModeSetting) {
                ModeSetting s = (ModeSetting) setting;
                if (s.hidden.get()) continue;
                FontDraw.drawText(FontDraw.FontType.MEDIUM, ctx, setting.name, xSettings + 6, ySettings + 11, 5, RenderUtil.ColorUtil.getTextColor(1, 1));
                RenderUtil.Round.draw(ctx, xSettings + 58, ySettings + 4, 40, 10 + (s.opened ? (s.modes.size() * 6 + 5) : 0), 1.5f, themea ? RenderUtil.ColorUtil.replAlpha(RenderUtil.ColorUtil.getMainColor(1, 1), 125) : RenderUtil.ColorUtil.getMainColor(1, 1));
                RenderUtil.Image.draw(ctx, Identifier.of(Strange.rootRes, "icons/gui/m_d.png"), xSettings + 87, ySettings + 3, 12, 12, RenderUtil.ColorUtil.getTextColor(1, 1));

                String textS = s.get();
                if (textS.length() > 8) {
                    textS = textS.substring(0, 8) + "...";
                }
                FontDraw.drawText(FontDraw.FontType.MEDIUM, ctx, textS, xSettings + 61, ySettings + 11, 5, RenderUtil.ColorUtil.getTextColor(1, 1));

                if (s.opened) {
                    for (int i = 0; i < s.modes.size(); i++) {
                        String textS2 = s.modes.get(i);
                        if (textS2.length() > 10) {
                            textS2 = textS2.substring(0, 10) + "...";
                        }
                        FontDraw.drawText(FontDraw.FontType.MEDIUM, ctx, textS2, xSettings + 61, ySettings + 11 + 10 + i * 6, 5, s.modes.get(i).equals(s.currentMode) ? RenderUtil.ColorUtil.getTextColor(1, 1) : RenderUtil.ColorUtil.replAlpha(RenderUtil.ColorUtil.getTextColor(1, 1), 90));
                    }
                    up += s.modes.size() * 6;
                }

                up += heightSettings + 4;
            }
            if (setting instanceof BooleanSetting) {
                BooleanSetting s = (BooleanSetting) setting;
                if (s.hidden.get()) continue;
                FontDraw.drawText(FontDraw.FontType.MEDIUM, ctx, setting.name, xSettings + 6, ySettings + 11, 5, RenderUtil.ColorUtil.getTextColor(1, 1));
                int boolColor = s.get() ? 0x3300FF3A : 0x33FF0010;
                RenderUtil.Round.draw(ctx, xSettings + 58, ySettings + 4, 40, 10, 1.5f, boolColor);
                String textS = s.get() ? "ВКЛ" : "ВЫКЛ";
                int textColor = s.get() ? 0x266E2C : 0x920009;
                FontDraw.drawText(FontDraw.FontType.MEDIUM, ctx, textS, xSettings + 61, ySettings + 11, 5, textColor);
                up += heightSettings + 4;
            }
        }
    }
}
