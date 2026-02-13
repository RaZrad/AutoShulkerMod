package auto.shulker.razstr.team;

import auto.shulker.razstr.team.strange.ui.clickgui.GuiClient;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import auto.shulker.razstr.team.ElytraEquipLogic;

public class AutoShulkerSpooky implements ModInitializer, ClientModInitializer {

    public static KeyBinding autoCatKey;

    @Override
    public void onInitialize() {
        System.out.println("Mod loaded!");
    }

    @Override
    public void onInitializeClient() {
        autoCatKey = new KeyBinding(
                "key.autoshulker.auto_cat",
                GLFW.GLFW_KEY_0,
                "category.autoshulker"
        );
        KeyBindingHelper.registerKeyBinding(autoCatKey);

        // Регистрируем все горячие клавиши через Keybinds
        Keybinds.registerKeybinds();

        AutoCat.INSTANCE.register();
        ElytraEquipLogic.INSTANCE.register();

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (Keybinds.OPEN_GUI.wasPressed()) {
                client.setScreen(new GuiClient());
            }
            if (autoCatKey.wasPressed()) {
                AutoCat.INSTANCE.onKeyZeroPressed(client);
            }


            AutoCat.INSTANCE.tick(client);
            AutoShulkerFarmLogic.INSTANCE.tick(client);
            AutoCraftLogic.INSTANCE.tick(client);
        });
    }

    /**
     * Проверяет, нажата ли клавиша с заданным KeyCode
     */
    private static boolean isKeyPressed(MinecraftClient client, int keyCode) {
        if (keyCode < 0) return false;
        long windowHandle = client.getWindow().getHandle();
        return GLFW.glfwGetKey(windowHandle, keyCode) == GLFW.GLFW_PRESS;
    }
}
