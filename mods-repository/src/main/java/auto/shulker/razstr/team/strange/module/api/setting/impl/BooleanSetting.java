package auto.shulker.razstr.team.strange.module.api.setting.impl;

import auto.shulker.razstr.team.strange.module.api.setting.Setting;

import java.util.function.Supplier;

public class BooleanSetting extends Setting {
    public boolean value;
    public String description;

    public BooleanSetting(String name, boolean value) {
        this.name = name;
        this.value = value;
    }

    public boolean get() { return value; }

    public void set(boolean value) {
        this.value = value;
        triggerAutoSave();
    }

    public void toggle() {
        set(!value);
    }

    public BooleanSetting hidden(Supplier<Boolean> hidden) {
        this.hidden = hidden;
        return this;
    }
}
