package auto.shulker.razstr.team.strange.module.api;

import auto.shulker.razstr.team.strange.module.impl.AutoCraftModule;
import auto.shulker.razstr.team.strange.module.impl.AutoShulkerFarmModule;
import auto.shulker.razstr.team.strange.module.impl.MaceDamageCheckerModule;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Manager {
    public ArrayList<Module> module = new ArrayList<>();

    public Manager() {
        module.add(new AutoShulkerFarmModule());
        module.add(new AutoCraftModule());
        module.add(new MaceDamageCheckerModule());
        module.sort(Comparator.comparing(m -> m.getDisplayName().toLowerCase()));
    }

    public ArrayList<Module> getModules() { return module; }

    public ArrayList<Module> getType(Category category) {
        ArrayList<Module> list = new ArrayList<>();
        for (Module m : module) {
            if (m.category == category) list.add(m);
        }
        return list;
    }
}
