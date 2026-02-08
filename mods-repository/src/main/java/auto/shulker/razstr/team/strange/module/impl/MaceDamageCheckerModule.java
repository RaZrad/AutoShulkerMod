package auto.shulker.razstr.team.strange.module.impl;

import auto.shulker.razstr.team.ModConfig;
import auto.shulker.razstr.team.MaceDamageCheckerLogic;
import auto.shulker.razstr.team.strange.module.api.Category;
import auto.shulker.razstr.team.strange.module.api.Module;
import auto.shulker.razstr.team.strange.module.api.setting.impl.BooleanSetting;
import auto.shulker.razstr.team.strange.module.api.setting.impl.ModeSetting;
import auto.shulker.razstr.team.strange.module.api.setting.impl.StringSetting;

public class MaceDamageCheckerModule extends Module {

    public ModeSetting entityFilter;
    public StringSetting textScale;

    public MaceDamageCheckerModule() {
        super("MaceDamageChecker", Category.Utilities);
        this.enable = ModConfig.maceDamageCheckerEnabled;
        if (this.enable) {
            MaceDamageCheckerLogic.INSTANCE.register();
        }
        entityFilter = new ModeSetting("Тип entity", ModConfig.maceDamageCheckerEntityFilter, "all", "players", "mobs", "nonliving");
        entityFilter.onChange = () -> ModConfig.maceDamageCheckerEntityFilter = entityFilter.currentMode;
        textScale = new StringSetting("Размер текста", String.valueOf(ModConfig.maceDamageCheckerTextScale));
        textScale.onChange = () -> {
            try {
                float value = Float.parseFloat(textScale.get());
                if (value < 0.5f) value = 0.5f;
                if (value > 3.0f) value = 3.0f;
                ModConfig.maceDamageCheckerTextScale = value;
                textScale.set(String.valueOf(value)); // Обновляем строку, если значение было скорректировано
            } catch (NumberFormatException e) {
                ModConfig.maceDamageCheckerTextScale = 1.0f;
                textScale.set("1.0");
            }
        };
        addSettings(entityFilter, textScale);
    }

    @Override
    public void onEnable() {
        ModConfig.maceDamageCheckerEnabled = true;
        MaceDamageCheckerLogic.INSTANCE.register(); // Регистрация безопасна (проверяет registered)
    }

    @Override
    public void onDisable() {
        ModConfig.maceDamageCheckerEnabled = false;
    }
}
