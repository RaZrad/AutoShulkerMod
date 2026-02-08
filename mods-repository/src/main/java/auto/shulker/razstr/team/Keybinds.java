package auto.shulker.razstr.team;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import org.lwjgl.glfw.GLFW;

public class Keybinds {
    public static KeyBinding OPEN_GUI;

    public static void registerKeybinds() {
        OPEN_GUI = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.autoshulker.open_gui", // идентификатор
                GLFW.GLFW_KEY_MINUS,        // клавиша "-"
                "category.autoshulker.main" // категория клавиш в настройках
        ));
    }
}
