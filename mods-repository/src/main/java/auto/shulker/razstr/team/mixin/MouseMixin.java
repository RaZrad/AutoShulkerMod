package auto.shulker.razstr.team.mixin;

import auto.shulker.razstr.team.AutoShulkerFarmLogic;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.client.Mouse;

@Mixin(Mouse.class)
public class MouseMixin {

    @Inject(method = "onMouseButton", at = @At("HEAD"))
    private void autoshulker_onMouseButton(long window, int button, int action, int mods, CallbackInfo ci) {
        if (button == GLFW.GLFW_MOUSE_BUTTON_RIGHT && action == GLFW.GLFW_PRESS
                && MinecraftClient.getInstance().currentScreen == null) {
            AutoShulkerFarmLogic.rightMouseButtonPressed = true;
        }
    }
}
