package auto.shulker.razstr.team;

import auto.shulker.razstr.team.strange.ui.clickgui.GuiClient;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.CraftingScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.screen.CraftingScreenHandler;
import net.minecraft.screen.slot.SlotActionType;

/**
 * AutoCraft: при включении проверяет инвентарь (≥8 панцирей шалкера, ≥1 сундук).
 * Панцири распределяются по 1 в каждый из 8 слотов по кругу (даже из разных стаков).
 * Режим /craft: пишет /craft в чат; режим Верстак: после закрытия GUI ждёт 1 сек и симулирует ПКМ.
 */
public class AutoCraftLogic {

    public static final AutoCraftLogic INSTANCE = new AutoCraftLogic();

    private static final int CRAFT_RESULT_SLOT = 0;
    private static final int CENTER_SLOT = 5;
    private static final int[] SHELL_GRID_SLOTS = { 1, 2, 3, 4, 6, 7, 8, 9 };

    private static final int PLAYER_INV_START = 10;
    private static final int PLAYER_INV_END = 46;

    private static final int TICKS_AFTER_GUI_CLOSE = 20;
    private static final int TICKS_BETWEEN_ACTIONS = 3;

    private int ticksAfterGuiClose = -1;
    private int ticksUntilNextAction = 0;
    private boolean rmbRequested = false;
    /** 0 = забрать результат, 1..8 = положить панцирь в слот, 9 = положить сундук в центр. Чётный шаг = клик "взять", нечётный = клик "положить". */
    private int craftStep = 0;
    private boolean stepIsPick = true;
    private int pendingSourceSlot = -1;
    private int pendingTargetSlot = -1;

    public void reset() {
        ticksAfterGuiClose = -1;
        ticksUntilNextAction = 0;
        rmbRequested = false;
        craftStep = 0;
        stepIsPick = true;
        pendingSourceSlot = -1;
        pendingTargetSlot = -1;
    }

    public void tick(MinecraftClient client) {
        if (!ModConfig.autoCraftEnabled || client.player == null || client.world == null) return;

        if ("workbench".equals(ModConfig.autoCraftMode)) {
            if (client.currentScreen instanceof GuiClient) {
                ticksAfterGuiClose = 0;
            } else if (ticksAfterGuiClose >= 0) {
                if (ticksAfterGuiClose == 0) ticksAfterGuiClose = 1;
                else ticksAfterGuiClose++;
                if (ticksAfterGuiClose >= TICKS_AFTER_GUI_CLOSE && !rmbRequested) {
                    rmbRequested = true;
                    if (client.options != null && client.options.useKey != null)
                        client.options.useKey.setPressed(true);
                } else if (ticksAfterGuiClose >= TICKS_AFTER_GUI_CLOSE + 2) {
                    if (client.options != null && client.options.useKey != null)
                        client.options.useKey.setPressed(false);
                    ticksAfterGuiClose = -1;
                    rmbRequested = false;
                }
            }
        }

        if (client.currentScreen != null && !(client.currentScreen instanceof CraftingScreen)) {
            if (client.currentScreen instanceof GuiClient) return;
            if (client.isPaused()) return;
        }

        if (!(client.currentScreen instanceof CraftingScreen)) {
            if (client.currentScreen instanceof GuiClient) return;
            if (countShells(client.player) < 8 || countChests(client.player) < 1) return;
            if ("craft".equals(ModConfig.autoCraftMode)) {
                client.player.networkHandler.sendCommand("craft");
            }
            return;
        }

        if (!(client.player.currentScreenHandler instanceof CraftingScreenHandler handler)) return;
        if (ticksUntilNextAction > 0) {
            ticksUntilNextAction--;
            return;
        }

        var resultStack = getStackInSlot(handler, CRAFT_RESULT_SLOT);
        int shellsInGrid = countFilledShellSlots(handler);
        var centerStack = getStackInSlot(handler, CENTER_SLOT);
        boolean hasChestInCenter = !centerStack.isEmpty() && centerStack.getItem() == Items.CHEST;

        // Приоритет 1: забрать готовый результат из слота результата
        if (!resultStack.isEmpty()) {
            sendClick(client, handler, CRAFT_RESULT_SLOT, 0, SlotActionType.QUICK_MOVE);
            ticksUntilNextAction = TICKS_BETWEEN_ACTIONS;
            return;
        }

        // Приоритет 2: положить сундук в центр (если там его нет и все панцири в сетке)
        if (!hasChestInCenter && shellsInGrid >= 8) {
            int chestSlot = findChestSlot(client.player);
            if (chestSlot >= 0) {
                int hSlot = invToHandlerSlot(chestSlot);
                sendClick(client, handler, hSlot, 0, SlotActionType.PICKUP);
                ticksUntilNextAction = TICKS_BETWEEN_ACTIONS;
                pendingSourceSlot = hSlot;
                pendingTargetSlot = CENTER_SLOT;
                stepIsPick = false;
                return;
            }
        }

        // Завершение действия "положить" для сундука
        if (stepIsPick == false && pendingSourceSlot >= 0 && pendingTargetSlot >= 0) {
            sendClick(client, handler, pendingTargetSlot, 0, SlotActionType.PICKUP);
            pendingSourceSlot = -1;
            pendingTargetSlot = -1;
            stepIsPick = true;
            ticksUntilNextAction = TICKS_BETWEEN_ACTIONS;
            return;
        }

        // Приоритет 3: распределить панцири по 8 слотам сетки по кругу (если не заполнено)
        if (shellsInGrid < 8) {
            int shellSlot = findShellSlot(client.player);
            if (shellSlot >= 0) {
                int hSlot = invToHandlerSlot(shellSlot);
                // Используем текущее количество панцирей как индекс для распределения по кругу
                int targetGrid = SHELL_GRID_SLOTS[shellsInGrid % 8];
                sendClick(client, handler, hSlot, 1, SlotActionType.PICKUP);
                pendingSourceSlot = hSlot;
                pendingTargetSlot = targetGrid;
                stepIsPick = false;
                ticksUntilNextAction = TICKS_BETWEEN_ACTIONS;
                return;
            }
        }

        // Если всё готово (8 панцирей + сундук в центре)
        if (hasChestInCenter && shellsInGrid >= 8) {
            ticksUntilNextAction = 5;
        }
    }

    private static int invToHandlerSlot(int playerInvSlot) {
        if (playerInvSlot < 9) return PLAYER_INV_END - 9 + playerInvSlot;
        return PLAYER_INV_START + (playerInvSlot - 9);
    }

    private static ItemStack getStackInSlot(CraftingScreenHandler handler, int index) {
        if (index < 0 || index >= handler.slots.size()) return ItemStack.EMPTY;
        return handler.slots.get(index).getStack();
    }

    private static int countFilledShellSlots(CraftingScreenHandler handler) {
        int n = 0;
        for (int s : SHELL_GRID_SLOTS) {
            ItemStack st = getStackInSlot(handler, s);
            if (!st.isEmpty() && st.getItem() == Items.SHULKER_SHELL) n++;
        }
        return n;
    }

    private static int countShells(ClientPlayerEntity player) {
        int n = 0;
        for (int i = 0; i < 41; i++) {
            ItemStack stack = player.getInventory().getStack(i);
            if (stack.getItem() == Items.SHULKER_SHELL) n += stack.getCount();
        }
        return n;
    }

    private static int countChests(ClientPlayerEntity player) {
        int n = 0;
        for (int i = 0; i < 41; i++) {
            ItemStack stack = player.getInventory().getStack(i);
            if (stack.getItem() == Items.CHEST) n += stack.getCount();
        }
        return n;
    }

    private static int findShellSlot(ClientPlayerEntity player) {
        for (int i = 0; i < 41; i++) {
            if (player.getInventory().getStack(i).getItem() == Items.SHULKER_SHELL) return i;
        }
        return -1;
    }

    private static int findChestSlot(ClientPlayerEntity player) {
        for (int i = 0; i < 41; i++) {
            if (player.getInventory().getStack(i).getItem() == Items.CHEST) return i;
        }
        return -1;
    }

    private static void sendClick(MinecraftClient client, CraftingScreenHandler handler, int slotId, int button, SlotActionType action) {
        client.player.networkHandler.sendPacket(new ClickSlotC2SPacket(
                handler.syncId,
                handler.getRevision(),
                slotId,
                button,
                action,
                ItemStack.EMPTY,
                new Int2ObjectOpenHashMap<>()
        ));
    }
}
