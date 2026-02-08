package auto.shulker.razstr.team.strange.module.impl;

import auto.shulker.razstr.team.ModConfig;
import auto.shulker.razstr.team.strange.module.api.Category;
import auto.shulker.razstr.team.strange.module.api.Module;
import auto.shulker.razstr.team.strange.module.api.setting.impl.ModeSetting;

public class AutoCraftModule extends Module {

    public ModeSetting craftMode;

    public AutoCraftModule() {
        super("AutoCraft", Category.Utilities);
        this.enable = ModConfig.autoCraftEnabled;
        craftMode = new ModeSetting("Режим крафта", ModConfig.autoCraftMode, "craft", "workbench");
        craftMode.onChange = () -> ModConfig.autoCraftMode = craftMode.currentMode;
        addSettings(craftMode);
    }

    @Override
    public void onEnable() {
        ModConfig.autoCraftEnabled = true;
    }

    @Override
    public void onDisable() {
        ModConfig.autoCraftEnabled = false;
    }
}
