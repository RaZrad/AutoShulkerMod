package auto.shulker.razstr.team;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import org.lwjgl.glfw.GLFW;

public class Keybinds {
    public static KeyBinding OPEN_GUI;
    public static KeyBinding FAST_SWAP_1;
    public static KeyBinding FAST_SWAP_2;
    public static KeyBinding FAST_SWAP_3;

    public static void registerKeybinds() {
        OPEN_GUI = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.autoshulker.open_gui",
                GLFW.GLFW_KEY_MINUS,
                "category.autoshulker"
        ));

        FAST_SWAP_1 = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.autoshulker.fast_swap_1",
                GLFW.GLFW_KEY_F1,
                "category.autoshulker"
        ));

        FAST_SWAP_2 = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.autoshulker.fast_swap_2",
                GLFW.GLFW_KEY_F2,
                "category.autoshulker"
        ));

        FAST_SWAP_3 = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.autoshulker.fast_swap_3",
                GLFW.GLFW_KEY_F3,
                "category.autoshulker"
        ));
    }
}
