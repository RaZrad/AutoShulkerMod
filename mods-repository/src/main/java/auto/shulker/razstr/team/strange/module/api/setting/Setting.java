package auto.shulker.razstr.team.strange.module.api.setting;

import java.util.function.Supplier;

public class Setting extends Config {
    public String name;
    public Supplier<Boolean> hidden = () -> false;

    public void triggerAutoSave() {
        // Stub - наш мод сохраняет в ModConfig напрямую
    }
}
