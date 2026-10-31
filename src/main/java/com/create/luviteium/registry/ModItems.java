package com.create.luviteium.registry;

import com.create.luviteium.CreateLuviteium;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(CreateLuviteium.MOD_ID);

    public static final DeferredItem<BlockItem> LUVITEIUM_ORE = ITEMS.registerSimpleBlockItem("luviteium_ore", ModBlocks.LUVITEIUM_ORE);
    public static final DeferredItem<BlockItem> LUVITEIUM_BLOCK = ITEMS.registerSimpleBlockItem("luviteium_block", ModBlocks.LUVITEIUM_BLOCK);
    public static final DeferredItem<BlockItem> CASTING_TABLE = ITEMS.registerSimpleBlockItem("casting_table", ModBlocks.CASTING_TABLE);
    public static final DeferredItem<BlockItem> BIOMASS_GENERATOR = ITEMS.registerSimpleBlockItem("biomass_generator", ModBlocks.BIOMASS_GENERATOR);

    public static final DeferredItem<Item> RAW_LUVITEIUM = ITEMS.registerSimpleItem("raw_luviteium");
    public static final DeferredItem<Item> LUVITEIUM_INGOT = ITEMS.registerSimpleItem("luviteium_ingot");
    public static final DeferredItem<Item> LUVITEIUM_PANEL = ITEMS.registerSimpleItem("luviteium_panel");
    public static final DeferredItem<Item> HOT_LUVITEIUM_INGOT = ITEMS.registerSimpleItem("hot_luviteium_ingot");
    public static final DeferredItem<Item> HOT_LUVITEIUM_PICKAXE_HEAD = ITEMS.registerSimpleItem("hot_luviteium_pickaxe_head");
    public static final DeferredItem<Item> BLANK_MOLD = ITEMS.registerSimpleItem("blank_mold");

    // The ten intentionally supported mold shapes. Source mappings are recorded in docs/molds.md.
    public static final DeferredItem<Item> INGOT_MOLD = ITEMS.registerSimpleItem("ingot_mold");
    public static final DeferredItem<Item> PICKAXE_HEAD_MOLD = ITEMS.registerSimpleItem("pickaxe_head_mold");
    public static final DeferredItem<Item> AXE_HEAD_MOLD = ITEMS.registerSimpleItem("axe_head_mold");
    public static final DeferredItem<Item> SHOVEL_HEAD_MOLD = ITEMS.registerSimpleItem("shovel_head_mold");
    public static final DeferredItem<Item> HOE_HEAD_MOLD = ITEMS.registerSimpleItem("hoe_head_mold");
    public static final DeferredItem<Item> SWORD_BLADE_MOLD = ITEMS.registerSimpleItem("sword_blade_mold");
    public static final DeferredItem<Item> HELMET_MOLD = ITEMS.registerSimpleItem("helmet_mold");
    public static final DeferredItem<Item> CHESTPLATE_MOLD = ITEMS.registerSimpleItem("chestplate_mold");
    public static final DeferredItem<Item> LEGGINGS_MOLD = ITEMS.registerSimpleItem("leggings_mold");
    public static final DeferredItem<Item> BOOTS_MOLD = ITEMS.registerSimpleItem("boots_mold");

    private ModItems() {}
}
