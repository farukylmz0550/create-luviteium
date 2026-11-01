package com.create.luviteium.client;

import com.create.luviteium.CreateLuviteium;
import com.create.luviteium.registry.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

@EventBusSubscriber(modid = CreateLuviteium.MOD_ID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public final class ClientTooltipSetup {
    private ClientTooltipSetup() {}

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();

        boolean hot = stack.is(ModItems.HOT_LUVITEIUM_INGOT.get())
                || stack.is(ModItems.HOT_LUVITEIUM_PICKAXE_HEAD.get())
                || stack.is(ModItems.HOT_LUVITEIUM_AXE_HEAD.get())
                || stack.is(ModItems.HOT_LUVITEIUM_SHOVEL_HEAD.get())
                || stack.is(ModItems.HOT_LUVITEIUM_HOE_HEAD.get())
                || stack.is(ModItems.HOT_LUVITEIUM_SWORD_BLADE.get())
                || stack.is(ModItems.HOT_LUVITEIUM_HELMET.get())
                || stack.is(ModItems.HOT_LUVITEIUM_CHESTPLATE.get())
                || stack.is(ModItems.HOT_LUVITEIUM_LEGGINGS.get())
                || stack.is(ModItems.HOT_LUVITEIUM_BOOTS.get());
        if (hot) {
            event.getToolTip().add(Component.translatable("tooltip.luviteium.cool_hint")
                    .withStyle(ChatFormatting.GOLD));
        }

        if (stack.is(ModItems.BLANK_MOLD.get())) {
            event.getToolTip().add(Component.translatable("tooltip.luviteium.blank_mold")
                    .withStyle(ChatFormatting.GRAY));
        }

        if (stack.is(ModItems.RAW_LUVITEIUM.get())) {
            event.getToolTip().add(Component.translatable("tooltip.luviteium.raw_processing")
                    .withStyle(ChatFormatting.GRAY));
        }
        if (stack.is(ModItems.CASTING_TABLE.get())) {
            event.getToolTip().add(Component.translatable("tooltip.luviteium.casting_input")
                    .withStyle(ChatFormatting.GRAY));
            event.getToolTip().add(Component.translatable("tooltip.luviteium.casting_output")
                    .withStyle(ChatFormatting.GOLD));
        }

        if (stack.is(ModItems.INGOT_MOLD.get())
                || stack.is(ModItems.PICKAXE_HEAD_MOLD.get())
                || stack.is(ModItems.AXE_HEAD_MOLD.get())
                || stack.is(ModItems.SHOVEL_HEAD_MOLD.get())
                || stack.is(ModItems.HOE_HEAD_MOLD.get())
                || stack.is(ModItems.SWORD_BLADE_MOLD.get())
                || stack.is(ModItems.HELMET_MOLD.get())
                || stack.is(ModItems.CHESTPLATE_MOLD.get())
                || stack.is(ModItems.LEGGINGS_MOLD.get())
                || stack.is(ModItems.BOOTS_MOLD.get())) {
            event.getToolTip().add(Component.translatable("tooltip.luviteium.mold_hint")
                    .withStyle(ChatFormatting.GRAY));
        }

        if (stack.is(ModItems.LUVITEIUM_BUCKET.get())) {
            event.getToolTip().add(Component.translatable("tooltip.luviteium.bucket_charge")
                    .withStyle(ChatFormatting.GRAY));
            event.getToolTip().add(Component.translatable("tooltip.luviteium.bucket_use")
                    .withStyle(ChatFormatting.GRAY));
        }

        if (stack.is(ModItems.BIOMASS_GENERATOR.get())) {
            event.getToolTip().add(Component.translatable("tooltip.luviteium.generator_fuels")
                    .withStyle(ChatFormatting.GRAY));
            event.getToolTip().add(Component.translatable("tooltip.luviteium.generator_insertion")
                    .withStyle(ChatFormatting.GRAY));
        }
    }
}
