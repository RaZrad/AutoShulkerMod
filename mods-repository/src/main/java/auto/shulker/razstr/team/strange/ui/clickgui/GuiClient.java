package auto.shulker.razstr.team.strange.ui.clickgui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import auto.shulker.razstr.team.ModConfig;
import auto.shulker.razstr.team.strange.Strange;
import auto.shulker.razstr.team.strange.module.Theme;
import auto.shulker.razstr.team.strange.module.ThemeManager;
import auto.shulker.razstr.team.strange.module.api.Category;
import auto.shulker.razstr.team.strange.module.api.Module;
import auto.shulker.razstr.team.strange.module.api.setting.Setting;
import auto.shulker.razstr.team.strange.module.api.setting.impl.StringSetting;
import auto.shulker.razstr.team.strange.module.impl.AutoCraftModule;
import auto.shulker.razstr.team.strange.module.impl.AutoShulkerFarmModule;
import auto.shulker.razstr.team.strange.ui.clickgui.mouse.GuiMouseClicked;
import auto.shulker.razstr.team.strange.ui.clickgui.render.GuiRender;
import auto.shulker.razstr.team.strange.ui.clickgui.ParticleEffect;

public class GuiClient extends Screen {

    private String currentCommand = "";
    private ParticleEffect particleEffect;

    public GuiClient() {
        super(Text.literal("Gui"));
        Strange.init();
        GuiScreen.width = 225;
        GuiScreen.height = 217;
        GuiScreen.categories = Category.values();
        GuiScreen.modules = Strange.get.manager.getType(GuiScreen.selectedCategories);
        GuiScreen.themes = Theme.values();
    }

    @Override
    protected void init() {
        super.init();
        GuiScreen.width = 225;
        GuiScreen.height = 217;
        GuiScreen.x = this.width / 2f - GuiScreen.width / 2f;
        GuiScreen.y = this.height / 2f - GuiScreen.height / 2f;
        
        // Инициализируем эффект частиц
        particleEffect = new ParticleEffect(
            GuiScreen.x, GuiScreen.y,
            GuiScreen.width, GuiScreen.height
        );
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        // Обновляем и рисуем частицы
        if (particleEffect != null) {
            particleEffect.setBounds(GuiScreen.x, GuiScreen.y, GuiScreen.width, GuiScreen.height);
            particleEffect.update(mouseX, mouseY);
            particleEffect.render(context);
        }

        super.render(context, mouseX, mouseY, deltaTicks);
        ThemeManager.update();
        GuiRender.renderGui(context, mouseX, mouseY, deltaTicks);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (Category c : Category.values()) {
            for (Module m : Strange.get.manager.getType(c)) {
                if (m.binding) {
                    m.bind = button;
                    m.binding = false;
                    m.displayName = m.name;
                    return true;
                }
            }
        }
        return GuiMouseClicked.mouseClickedGui(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        float modulesX = GuiScreen.x + 7;
        float modulesY = GuiScreen.y + 64;
        float modulesWidth = 211;
        float modulesHeight = GuiScreen.height - 64 - 7;

        if (GuiScreen.isHovered(mouseX, mouseY, modulesX, modulesY, modulesWidth, modulesHeight)) {
            GuiScreen.scroll.handleScroll(verticalAmount);
            return true;
        }

        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        for (Category c : Category.values()) {
            for (Module m : Strange.get.manager.getType(c)) {
                if (m.binding) {
                    if (keyCode == GLFW.GLFW_KEY_DELETE || keyCode == GLFW.GLFW_KEY_BACKSPACE) {
                        m.bind = -1;
                    } else {
                        m.bind = keyCode;
                    }
                    m.binding = false;
                    m.displayName = m.name;
                    return true;
                }
                for (Setting setting : m.getSettingsForGUI()) {
                    if (setting instanceof StringSetting) {
                        StringSetting s = (StringSetting) setting;
                        if (s.hidden.get()) continue;

                        if (s.active && keyCode == GLFW.GLFW_KEY_BACKSPACE && s.input.length() > 0) {
                            s.set(s.input.substring(0, s.input.length() - 1));
                            return true;
                        }
                    }
                }
            }
        }


        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        // Если есть активное StringSetting, не обрабатываем команды
        for (Category c : Category.values()) {
            for (Module m : Strange.get.manager.getType(c)) {
                for (Setting setting : m.getSettingsForGUI()) {
                    if (setting instanceof StringSetting) {
                        StringSetting s = (StringSetting) setting;
                        if (s.active) {
                            if (codePoint >= 32 && codePoint != 127) {
                                s.set(s.input + codePoint);
                            }
                            return true;
                        }
                    }
                }
            }
        }

        // Добавляем символ к команде, если она начинается с точки или уже продолжается
        if (codePoint == '.' || currentCommand.length() > 0) {
            if (currentCommand.length() < 100) {
                currentCommand += codePoint;
            }
            return true;
        }

        return super.charTyped(codePoint, modifiers);
    }

    @Override
    public void close() {
        syncSettingsToModConfig();
        super.close();
    }

    private void syncSettingsToModConfig() {
        for (Module m : Strange.get.manager.getModules()) {
            if (m instanceof AutoShulkerFarmModule am) {
                ModConfig.pricePerUnit = am.pricePerUnit.get();
                ModConfig.balance = am.balance.get();
                ModConfig.sellCount = am.sellCount.get();
                ModConfig.waitTimeAfterLimitSeconds = am.waitTimeAfterLimit.get();
            }
        }
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
