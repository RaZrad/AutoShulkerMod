package auto.shulker.razstr.team.strange.module.api;

import net.minecraft.client.MinecraftClient;
import auto.shulker.razstr.team.strange.module.api.setting.Setting;

import java.util.List;

public class Module extends auto.shulker.razstr.team.strange.module.api.setting.Config {
    public static MinecraftClient mc = MinecraftClient.getInstance();
    public String name;
    public int bind;
    public boolean enable;
    public boolean open = false;
    public Category category;
    public String displayName;
    public String description;
    public boolean binding;

    public Module(String name, Category category) {
        this.name = name;
        this.category = category;
        this.bind = -1;
        this.enable = false;
        this.description = "";
        this.displayName = name;
    }

    public String getDisplayName() { return displayName; }

    public void toggle() {
        enable = !enable;
        if (enable) onEnable();
        else onDisable();
    }

    public void onEnable() { }

    public void onDisable() { }

    public void setState(boolean enable) {
        this.enable = enable;
        if (enable) onEnable();
        else onDisable();
    }
}
