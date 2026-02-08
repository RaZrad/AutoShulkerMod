package auto.shulker.razstr.team.strange.module.api.setting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Config {
    private final ArrayList<Setting> settingList = new ArrayList<>();

    public final void addSettings(Setting... var1) {
        settingList.addAll(Arrays.asList(var1));
    }

    public final List<Setting> getSettingsForGUI() {
        return settingList.stream().filter(s -> !s.hidden.get()).toList();
    }

    public final List<Setting> getSettings() {
        return new ArrayList<>(settingList);
    }
}
