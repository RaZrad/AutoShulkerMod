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
                ModConfig.fastSwapItem1KeyCode,
                "category.autoshulker"
        ));

        FAST_SWAP_2 = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.autoshulker.fast_swap_2",
                ModConfig.fastSwapItem2KeyCode,
                "category.autoshulker"
        ));

        FAST_SWAP_3 = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.autoshulker.fast_swap_3",
                ModConfig.fastSwapItem3KeyCode,
                "category.autoshulker"
        ));
    }

    /**
     * Обновляет KeyCode для FastSwap 1
     */
    public static void setFastSwap1KeyCode(int keyCode) {
        ModConfig.fastSwapItem1KeyCode = keyCode;
    }

    /**
     * Обновляет KeyCode для FastSwap 2
     */
    public static void setFastSwap2KeyCode(int keyCode) {
        ModConfig.fastSwapItem2KeyCode = keyCode;
    }

    /**
     * Обновляет KeyCode для FastSwap 3
     */
    public static void setFastSwap3KeyCode(int keyCode) {
        ModConfig.fastSwapItem3KeyCode = keyCode;
    }
}
