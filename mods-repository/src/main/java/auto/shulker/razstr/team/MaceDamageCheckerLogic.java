package auto.shulker.razstr.team;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;

import java.util.List;

/**
 * MaceDamageChecker: показывает урон булавы под прицелом с учетом высоты падения и зачарований.
 */
public class MaceDamageCheckerLogic {

    public static final MaceDamageCheckerLogic INSTANCE = new MaceDamageCheckerLogic();
    private static boolean registered = false;

    public void register() {
        if (registered) return;
        registered = true;
        HudRenderCallback.EVENT.register((context, tickCounter) -> {
            if (ModConfig.maceDamageCheckerEnabled) {
                render(context, tickCounter.getTickDelta(true));
            }
        });
    }

    private void render(DrawContext ctx, float tickDelta) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.world == null) return;

        // Проверяем, держит ли игрок булаву
        ItemStack mainHand = client.player.getMainHandStack();
        ItemStack offHand = client.player.getOffHandStack();
        ItemStack mace = null;

        if (mainHand.isOf(Items.MACE)) {
            mace = mainHand;
        } else if (offHand.isOf(Items.MACE)) {
            mace = offHand;
        }

        if (mace == null) return;

        // Находим ближайшее entity под игроком
        Entity targetEntity = findTargetEntity(client, tickDelta);
        if (targetEntity == null) return;

        // Рассчитываем урон
        float damage = calculateMaceDamage(client.player, mace, targetEntity, tickDelta);

        // Рендерим текст под прицелом
        renderDamageText(ctx, client, damage);
    }

    private Entity findTargetEntity(MinecraftClient client, float tickDelta) {
        PlayerEntity player = client.player;
        if (player == null || client.world == null) return null;

        // Сначала проверяем entity под прицелом
        HitResult hitResult = client.crosshairTarget;
        if (hitResult != null && hitResult.getType() == HitResult.Type.ENTITY) {
            EntityHitResult entityHit = (EntityHitResult) hitResult;
            Entity target = entityHit.getEntity();
            if (target != null && shouldCheckEntity(target) && target.getY() < player.getY()) {
                return target;
            }
        }

        // Ищем ближайшее entity под игроком в радиусе поиска
        double searchRadius = 8.0;
        double searchHeight = 32.0;
        Box searchBox = new Box(
            player.getX() - searchRadius, player.getY() - searchHeight, player.getZ() - searchRadius,
            player.getX() + searchRadius, player.getY(), player.getZ() + searchRadius
        );

        List<Entity> entities = client.world.getOtherEntities(player, searchBox);
        
        Entity closest = null;
        double closestDistance = Double.MAX_VALUE;
        
        for (Entity entity : entities) {
            if (!shouldCheckEntity(entity)) continue;
            if (entity.getY() >= player.getY()) continue; // Только под игроком
            
            double distance = player.squaredDistanceTo(entity);
            if (distance < closestDistance) {
                closestDistance = distance;
                closest = entity;
            }
        }

        return closest;
    }

    private boolean shouldCheckEntity(Entity entity) {
        String filter = ModConfig.maceDamageCheckerEntityFilter;
        
        if ("all".equals(filter)) return true;
        if ("players".equals(filter)) return entity instanceof PlayerEntity;
        if ("mobs".equals(filter)) return entity instanceof LivingEntity && !(entity instanceof PlayerEntity) && !(entity instanceof ArmorStandEntity);
        if ("nonliving".equals(filter)) return !(entity instanceof LivingEntity) || entity instanceof ArmorStandEntity;
        
        return true;
    }

    private float calculateMaceDamage(PlayerEntity player, ItemStack mace, Entity target, float tickDelta) {
        // Базовый урон булавы: 5 (2.5 сердца)
        float baseDamage = 5.0f;

        // Рассчитываем урон от падения
        double fallDistance = player.fallDistance;
        // За каждые 2 блока падения добавляется 1 урон (максимум 40 урона с высоты 80+ блоков)
        float fallDamage = (float) Math.min(fallDistance / 2.0, 40.0);
        
        // Урон от зачарований
        float enchantmentDamage = 0.0f;
        
        int smiteLevel = 0, baneLevel = 0, sharpnessLevel = 0;
        for (var entry : EnchantmentHelper.getEnchantments(mace).getEnchantmentEntries()) {
            RegistryEntry<net.minecraft.enchantment.Enchantment> ench = entry.getKey();
            int level = entry.getValue();
            if (ench.matchesKey(Enchantments.SMITE)) smiteLevel = level;
            if (ench.matchesKey(Enchantments.BANE_OF_ARTHROPODS)) baneLevel = level;
            if (ench.matchesKey(Enchantments.SHARPNESS)) sharpnessLevel = level;
        }

        if (target instanceof LivingEntity livingTarget) {
            // Smite против нежити
            if (smiteLevel > 0 && isUndead(livingTarget)) {
                enchantmentDamage += 2.5f * smiteLevel;
            }
            // Bane of Arthropods против членистоногих
            else if (baneLevel > 0 && isArthropod(livingTarget)) {
                enchantmentDamage += 2.5f * baneLevel;
            }
            // Sharpness (общий бонус, если нет Smite или Bane)
            else if (sharpnessLevel > 0) {
                enchantmentDamage += 1.0f * sharpnessLevel;
            }
        } else {
            if (sharpnessLevel > 0) {
                enchantmentDamage += 1.0f * sharpnessLevel;
            }
        }

        // Получаем процент зарядки булавы (от 0 до 1)
        float chargeAmount = 0.0f;
        if (mace.getItem() instanceof net.minecraft.item.Chargeable) {
            chargeAmount = ((net.minecraft.item.Chargeable) mace.getItem()).getChargedAmount(mace);
        }

        // Итоговый урон = базовый + падение + чары, умноженный на процент зарядки
        float totalDamage = baseDamage + fallDamage + enchantmentDamage;
        return totalDamage * chargeAmount;
    }

    private boolean isUndead(LivingEntity entity) {
        // Проверяем тип entity на нежить
        String typeName = entity.getType().toString().toLowerCase();
        return typeName.contains("zombie") || typeName.contains("skeleton") || 
               typeName.contains("wither") || typeName.contains("phantom") ||
               typeName.contains("drowned");
    }

    private boolean isArthropod(LivingEntity entity) {
        // Проверяем тип entity на членистоногих
        String typeName = entity.getType().toString().toLowerCase();
        return typeName.contains("spider") || typeName.contains("silverfish") || 
               typeName.contains("endermite");
    }

    private void renderDamageText(DrawContext ctx, MinecraftClient client, float damage) {
        int screenWidth = client.getWindow().getScaledWidth();
        int screenHeight = client.getWindow().getScaledHeight();
        
        // Позиция под прицелом (чуть ниже центра экрана)
        int x = screenWidth / 2;
        int y = screenHeight / 2 + 20;
        
        String text = String.format("%.1f", damage);
        float scale = ModConfig.maceDamageCheckerTextScale;
        
        // Рисуем текст с тенью
        int textWidth = (int) (client.textRenderer.getWidth(text) * scale);
        int textX = x - textWidth / 2;
        int textY = y;
        
        // Фон для читаемости
        ctx.fill(textX - 2, textY - 1, textX + textWidth + 2, textY + (int)(9 * scale) + 1, 0x80000000);
        
        // Текст
        ctx.getMatrices().push();
        ctx.getMatrices().scale(scale, scale, scale);
        ctx.drawTextWithShadow(client.textRenderer, text, 
            (int)(textX / scale), (int)(textY / scale), 0xFFFFFF);
        ctx.getMatrices().pop();
    }
}
