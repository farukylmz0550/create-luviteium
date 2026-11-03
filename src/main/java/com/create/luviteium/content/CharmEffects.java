package com.create.luviteium.content;

import com.create.luviteium.registry.ModItems;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.event.entity.living.LivingChangeTargetEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.bus.api.SubscribeEvent;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class CharmEffects {
    private static final Map<UUID, Long> BYPASSED_UNTIL = new ConcurrentHashMap<>();

    @SubscribeEvent
    public static void onEnemyTick(EntityTickEvent.Post event) {
        if (!(event.getEntity() instanceof net.minecraft.world.entity.Mob mob)
                || !(mob instanceof Enemy) || mob.level().isClientSide
                || mob instanceof net.minecraft.world.entity.boss.wither.WitherBoss) return;
        Player player = mob.level().getNearestPlayer(mob, 5.0);
        if (player == null || !hasCharm(player, ModItems.NAZAR_BONCUGU.get())
                || BYPASSED_UNTIL.getOrDefault(mob.getUUID(), 0L) > mob.level().getGameTime()) return;
        double dx = mob.getX() - player.getX();
        double dy = mob.getY() - player.getY();
        double dz = mob.getZ() - player.getZ();
        if (Math.max(Math.max(Math.abs(dx), Math.abs(dy)), Math.abs(dz)) <= 5.0) {
            mob.setTarget(null);
            double length = Math.max(0.01, Math.sqrt(dx * dx + dy * dy + dz * dz));
            mob.push(dx / length * 0.18, dy / length * 0.05, dz / length * 0.18);
        }
    }

    @SubscribeEvent
    public static void onLivingChangeTarget(LivingChangeTargetEvent event) {
        LivingEntity mob = event.getEntity();
        LivingEntity target = event.getNewAboutToBeSetTarget();
        if (!(mob instanceof Enemy) || target == null || !(target instanceof Player player)
                || mob instanceof net.minecraft.world.entity.boss.wither.WitherBoss) return;
        if (!hasCharm(player, ModItems.NAZAR_BONCUGU.get())) return;
        if (BYPASSED_UNTIL.getOrDefault(mob.getUUID(), 0L) <= mob.level().getGameTime()
                && Math.max(Math.max(Math.abs(mob.getX() - player.getX()), Math.abs(mob.getY() - player.getY())),
                Math.abs(mob.getZ() - player.getZ())) <= 5.0) {
            event.setNewAboutToBeSetTarget(null);
        }
    }

    @SubscribeEvent
    public static void onLivingAttack(LivingIncomingDamageEvent event) {
        Entity attacker = event.getSource().getEntity();
        if (attacker instanceof Player && event.getEntity() instanceof Enemy mob
                && hasCharm((Player) attacker, ModItems.NAZAR_BONCUGU.get())) {
            BYPASSED_UNTIL.put(event.getEntity().getUUID(), event.getEntity().level().getGameTime() + 200);
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player) || player.tickCount % 40 != 0) return;
        if (hasCharm(player, ModItems.NAZAR_BONCUGU.get())) {
            player.addEffect(new MobEffectInstance(MobEffects.LUCK, 60, 0, true, false));
        }
    }

    @SubscribeEvent
    public static void onCropBreak(BlockEvent.BreakEvent event) {
        if (!(event.getPlayer() instanceof ServerPlayer player) || !hasCharm(player, ModItems.BAYRAMIN_BEREKETI.get())) return;
        BlockState state = event.getState();
        if (state.getBlock() instanceof CropBlock crop && !crop.isMaxAge(state)) return;
        ItemStack bonus = bonusProduce(state);
        if (bonus.isEmpty() || player.getRandom().nextFloat() >= 0.03f) return;
        bonus.setCount(1 + player.getRandom().nextInt(3));
        if (!player.getInventory().add(bonus)) player.drop(bonus, false);
    }

    private static ItemStack bonusProduce(BlockState state) {
        if (state.is(Blocks.WHEAT)) return new ItemStack(net.minecraft.world.item.Items.WHEAT);
        if (state.is(Blocks.CARROTS)) return new ItemStack(net.minecraft.world.item.Items.CARROT);
        if (state.is(Blocks.POTATOES)) return new ItemStack(net.minecraft.world.item.Items.POTATO);
        if (state.is(Blocks.BEETROOTS)) return new ItemStack(net.minecraft.world.item.Items.BEETROOT);
        if (state.is(Blocks.NETHER_WART)) return new ItemStack(net.minecraft.world.item.Items.NETHER_WART);
        if (state.is(Blocks.COCOA)) return new ItemStack(net.minecraft.world.item.Items.COCOA_BEANS);
        if (state.is(Blocks.SWEET_BERRY_BUSH)) return new ItemStack(net.minecraft.world.item.Items.SWEET_BERRIES);
        return ItemStack.EMPTY;
    }

    public static boolean hasCharm(LivingEntity entity, net.minecraft.world.item.Item charm) {
        return CuriosApi.getCuriosInventory(entity).map(handler -> handler.isEquipped(charm)).orElse(false);
    }

    private CharmEffects() {}
}
