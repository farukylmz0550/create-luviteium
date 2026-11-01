package com.create.luviteium.registry;

import com.create.luviteium.CreateLuviteium;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CreateLuviteium.MOD_ID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> LUVITEIUM =
            TABS.register("luviteium", () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.luviteium"))
                    .icon(() -> new ItemStack(ModItems.LUVITEIUM_INGOT.get()))
                    .displayItems((parameters, output) -> {
                        output.accept(ModItems.LUVITEIUM_ORE.get());
                        output.accept(ModBlocks.LUVITEIUM_ORE.get());
                        output.accept(ModItems.RAW_LUVITEIUM.get());
                        output.accept(ModItems.LUVITEIUM_INGOT.get());
                        output.accept(ModItems.HOT_LUVITEIUM_INGOT.get());
                        output.accept(ModItems.LUVITEIUM_BLOCK.get());
                        output.accept(ModItems.LUVITEIUM_PANEL.get());
                        output.accept(ModItems.LUVITEIUM_BUCKET.get());

                        output.accept(ModItems.BLANK_MOLD.get());
                        output.accept(ModItems.INGOT_MOLD.get());
                        output.accept(ModItems.PICKAXE_HEAD_MOLD.get());
                        output.accept(ModItems.AXE_HEAD_MOLD.get());
                        output.accept(ModItems.SHOVEL_HEAD_MOLD.get());
                        output.accept(ModItems.HOE_HEAD_MOLD.get());
                        output.accept(ModItems.SWORD_BLADE_MOLD.get());
                        output.accept(ModItems.HELMET_MOLD.get());
                        output.accept(ModItems.CHESTPLATE_MOLD.get());
                        output.accept(ModItems.LEGGINGS_MOLD.get());
                        output.accept(ModItems.BOOTS_MOLD.get());

                        output.accept(ModItems.HOT_LUVITEIUM_PICKAXE_HEAD.get());
                        output.accept(ModItems.HOT_LUVITEIUM_AXE_HEAD.get());
                        output.accept(ModItems.HOT_LUVITEIUM_SHOVEL_HEAD.get());
                        output.accept(ModItems.HOT_LUVITEIUM_HOE_HEAD.get());
                        output.accept(ModItems.HOT_LUVITEIUM_SWORD_BLADE.get());
                        output.accept(ModItems.LUVITEIUM_PICKAXE_HEAD.get());
                        output.accept(ModItems.LUVITEIUM_AXE_HEAD.get());
                        output.accept(ModItems.LUVITEIUM_SHOVEL_HEAD.get());
                        output.accept(ModItems.LUVITEIUM_HOE_HEAD.get());
                        output.accept(ModItems.LUVITEIUM_SWORD_BLADE.get());

                        output.accept(ModItems.LUVITEIUM_PICKAXE.get());
                        output.accept(ModItems.LUVITEIUM_AXE.get());
                        output.accept(ModItems.LUVITEIUM_SHOVEL.get());
                        output.accept(ModItems.LUVITEIUM_HOE.get());
                        output.accept(ModItems.LUVITEIUM_SWORD.get());

                        output.accept(ModItems.LUVITEIUM_HELMET.get());
                        output.accept(ModItems.LUVITEIUM_CHESTPLATE.get());
                        output.accept(ModItems.LUVITEIUM_LEGGINGS.get());
                        output.accept(ModItems.LUVITEIUM_BOOTS.get());

                        output.accept(ModItems.CASTING_TABLE.get());
                        output.accept(ModItems.BIOMASS_GENERATOR.get());
                    })
                    .build());

    private ModCreativeTabs() {}
}
