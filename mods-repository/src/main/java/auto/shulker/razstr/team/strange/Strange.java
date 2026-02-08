package auto.shulker.razstr.team.strange;

import auto.shulker.razstr.team.strange.module.api.Manager;

/**
 * Точка входа GUI (порт Strange Visuals).
 */
public class Strange {
    public static Strange get;

    public static String name = "AutoShulker";
    public static String rootRes = "autoshulkerspooky";

    public Manager manager;

    public static void init() {
        if (get == null) {
            get = new Strange();
            get.manager = new Manager();
        }
    }
}
