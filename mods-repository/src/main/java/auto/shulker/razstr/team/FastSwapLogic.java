package auto.shulker.razstr.team;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

/**
 * FastSwap: быстро берет предмет из хотбара, нажимает ПКМ и свапает обратно
 */
public class FastSwapLogic {

    public static final FastSwapLogic INSTANCE = new FastSwapLogic();

    private int ticksUntilNextAction = 0;
    private int swapStep = 0; // 0 = ждет, 1 = взял предмет, 2 = нажал ПКМ, 3 = свапа обратно
    private int originalSlot = -1;
    private Item targetItem = null;

    private static boolean registered = false;

    public void register() {
        if (registered) return;
        registered = true;
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (ModConfig.fastSwapEnabled && client.player != null) {
                tick(client);
            }
        });
    }

    public void tick(MinecraftClient client) {
        if (!ModConfig.fastSwapEnabled || client.player == null) return;

        if (ticksUntilNextAction > 0) {
            ticksUntilNextAction--;
            return;
        }

        ClientPlayerEntity player = client.player;
        int currentSlot = player.getInventory().selectedSlot;

        switch (swapStep) {
            case 0:
                // Ничего не делаем, ждем срабатыванияfastSwapItem1/2/3
                break;
            case 1:
                // Шаг 1: взяли предмет, ждем перед ПКМ
                ticksUntilNextAction = (ModConfig.fastSwapDelay + 9) / 10; // Конвертируем мс в тики
                swapStep = 2;
                break;
            case 2:
                // Шаг 2: нажимаем ПКМ
                if (client.options != null && client.options.useKey != null) {
                    client.options.useKey.setPressed(true);
                }
                ticksUntilNextAction = 1; // ПКМ один тик
                swapStep = 3;
                break;
            case 3:
                // Шаг 3: отпускаем ПКМ
                if (client.options != null && client.options.useKey != null) {
                    client.options.useKey.setPressed(false);
                }
                ticksUntilNextAction = (ModConfig.fastSwapDelay + 9) / 10; // Ждем перед свопом
                swapStep = 4;
                break;
            case 4:
                // Шаг 4: свапаем обратно
                if (originalSlot >= 0) {
                    player.getInventory().selectedSlot = originalSlot;
                }
                swapStep = 0;
                originalSlot = -1;
                targetItem = null;
                break;
        }
    }

    public void triggerFastSwap(MinecraftClient client, int itemIndex) {
        if (!ModConfig.fastSwapEnabled || client.player == null || swapStep != 0) return;

        ClientPlayerEntity player = client.player;
        String itemName = "";
        int delay = ModConfig.fastSwapDelay;

        // Выбираем предмет и задержку на основе индекса
        switch (itemIndex) {
            case 1:
                itemName = ModConfig.fastSwapItem1Name;
                delay = ModConfig.fastSwapItem1Delay;
                break;
            case 2:
                itemName = ModConfig.fastSwapItem2Name;
                delay = ModConfig.fastSwapItem2Delay;
                break;
            case 3:
                itemName = ModConfig.fastSwapItem3Name;
                delay = ModConfig.fastSwapItem3Delay;
                break;
            default:
                return;
        }

        // Находим предмет в инвентаре
        int slotIndex = findItemInHotbar(player, itemName);
        if (slotIndex < 0) return;

        // Сохраняем текущий слот и меняемся на новый
        originalSlot = player.getInventory().selectedSlot;
        player.getInventory().selectedSlot = slotIndex;

        // Запускаем процесс свопа
        swapStep = 1;
        ticksUntilNextAction = 5;
    }

    private int findItemInHotbar(ClientPlayerEntity player, String itemName) {
        // Нормализуем название предмета
        String normalizedName = itemName.toLowerCase().replace(" ", "_");

        // Пытаемся найти по ID
        for (int i = 0; i < 9; i++) {
            ItemStack stack = player.getInventory().getStack(i);
            if (stack.isEmpty()) continue;

            Item item = stack.getItem();
            String itemId = Registries.ITEM.getId(item).toString();
            
            // Проверяем совпадение
            if (itemId.contains(normalizedName) || item.getTranslationKey().toLowerCase().contains(normalizedName)) {
                return i;
            }
        }

        return -1;
    }

    public void reset() {
        swapStep = 0;
        ticksUntilNextAction = 0;
        originalSlot = -1;
        targetItem = null;
    }
}
