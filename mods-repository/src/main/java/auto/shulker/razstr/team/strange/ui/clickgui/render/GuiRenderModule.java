package auto.shulker.razstr.team.strange.ui.clickgui.render;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;
import auto.shulker.razstr.team.strange.Strange;
import auto.shulker.razstr.team.strange.module.Theme;
import auto.shulker.razstr.team.strange.module.ThemeManager;
import auto.shulker.razstr.team.strange.module.api.Category;
import auto.shulker.razstr.team.strange.module.api.Module;
import auto.shulker.razstr.team.strange.module.api.setting.Setting;
import auto.shulker.razstr.team.strange.ui.clickgui.GuiScreen;
import auto.shulker.razstr.team.strange.utils.KeyUtil;
import auto.shulker.razstr.team.strange.utils.FontDraw;
import auto.shulker.razstr.team.strange.utils.RenderUtil;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class GuiRenderModule extends GuiScreen {

    /** Имена файлов иконок только в нижнем регистре (Identifier допускает только [a-z0-9/._-]). */
    private static String getModuleIconName(Module module) {
        return switch (module.name) {
            case "AutoShulkerFarm" -> "autoshulkerblack";
            case "AutoCraft" -> "autocraftblack";
            default -> selectedCategories.toString().toLowerCase();
        };
    }

    public static void renderModule(DrawContext ctx, double mouseX, double mouseY) {
        boolean themea = ThemeManager.getTheme() == Theme.TRANSPARENT_WHITE || ThemeManager.getTheme() == Theme.TRANSPARENT_BLACK
                || ThemeManager.getTheme() == Theme.PURPLE || ThemeManager.getTheme() == Theme.PINK;
        boolean blackTheme = ThemeManager.getTheme() == Theme.BLACK;

        float modulesX = x + 7;
        float modulesY = y + 64;
        float modulesWidth = 211;
        float modulesHeight = height - 64 - 7;

        for (Category category : categories) {
            if (category == selectedCategories)
                FontDraw.drawText(FontDraw.FontType.MEDIUM, ctx, category.getName(), x + 10, y + 42 + 13, 7, RenderUtil.ColorUtil.getTextColor(1, 1));
        }

        ctx.enableScissor(
                (int) modulesX,
                (int) modulesY,
                (int) (modulesX + modulesWidth),
                (int) (modulesY + modulesHeight)
        );

        scroll.update();

        float yDown = 0;
        float scrollY = scroll.getScroll();

        for (Module module : modules) {
            float up = calcUP(module);

            float drawY = modulesY + yDown + scrollY;

            RenderUtil.Border.draw(ctx, modulesX, drawY, 211, 26 + up, 5, 0.1f, blackTheme ? RenderUtil.ColorUtil.replAlpha(new Color(0xFFFFFF).getRGB(), 80) : themea ? RenderUtil.ColorUtil.replAlpha(new Color(0x000000).getRGB(), 40) : RenderUtil.ColorUtil.replAlpha(new Color(0x000000).getRGB(), 80));

            RenderUtil.Round.draw(ctx, modulesX, drawY, 211, 26 + up, 5, themea ? RenderUtil.ColorUtil.replAlpha(RenderUtil.ColorUtil.getBackGroundColor(1, 1), 64) : RenderUtil.ColorUtil.getBackGroundColor(1, 1));

            Identifier iconId = Identifier.of(Strange.rootRes, "textures/gui/" + getModuleIconName(module) + ".png");
            RenderUtil.Image.draw(ctx, iconId, modulesX + 7, drawY + 7, 12, 12, RenderUtil.ColorUtil.getTextColor(1, 1));

            String displayText = module.getDisplayName();
            if (!module.binding) {
                String bindText = KeyUtil.getKey(module.bind);
                if (!bindText.equals("null")) {
                    displayText += " [" + bindText + "]";
                }
            }
            FontDraw.drawText(FontDraw.FontType.MEDIUM, ctx, displayText, modulesX + 25, drawY + 12, 6, RenderUtil.ColorUtil.getTextColor(1, 1));

            boolean enable = module.enable;
            float widthEnable = FontDraw.getWidth(FontDraw.FontType.MEDIUM, enable ? "ВКЛЮЧЕНО" : "ВЫКЛЮЧЕНО", 4);

            RenderUtil.Round.draw(ctx, modulesX + 23, drawY + 14.5f, widthEnable + 5.5f, 7, 3, enable ? new Color(0x3300FF3A, true) : new Color(0x33FF0010, true));

            FontDraw.drawText(FontDraw.FontType.MEDIUM, ctx, enable ? "ВКЛЮЧЕНО" : "ВЫКЛЮЧЕНО", modulesX + 26, drawY + 19.5f, 4, enable ? new Color(0x266E2C).getRGB() : new Color(0x920009).getRGB());

            if (!module.getSettingsForGUI().isEmpty()) {
                float dotsX = modulesX + 201;
                RenderUtil.Round.draw(ctx, dotsX, drawY + 8, 3, 3, 1.5f, RenderUtil.ColorUtil.getTextColor(1, 1));
                RenderUtil.Round.draw(ctx, dotsX, drawY + 11.5F, 3, 3, 1.5f, RenderUtil.ColorUtil.getTextColor(1, 1));
                RenderUtil.Round.draw(ctx, dotsX, drawY + 15, 3, 3, 1.5f, RenderUtil.ColorUtil.getTextColor(1, 1));
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
                GuiRenderSettings.renderSettings(ctx, settings1, x + 7, drawY + 26, mouseX, mouseY);
                GuiRenderSettings.renderSettings(ctx, settings2, x + 109, drawY + 26, mouseX, mouseY);
            }

            yDown += 30 + up;
        }

        float contentHeight = modules.size() * 30;
        for (Module m : modules) {
            contentHeight += calcUP(m);
        }
        scroll.setMax(contentHeight, modulesHeight);

        ctx.disableScissor();
    }
}
