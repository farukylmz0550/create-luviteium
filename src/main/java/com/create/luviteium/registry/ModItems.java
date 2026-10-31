package com.create.luviteium.registry;

import com.create.luviteium.CreateLuviteium;
import com.create.luviteium.content.LuviteiumTier;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;
import java.util.Map;

public final class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(CreateLuviteium.MOD_ID);
    public static final DeferredRegister<ArmorMaterial> ARMOR_MATERIALS =
            DeferredRegister.create(BuiltInRegistries.ARMOR_MATERIAL, CreateLuviteium.MOD_ID);

    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> LUVITEIUM_ARMOR_MATERIAL =
            ARMOR_MATERIALS.register("luviteium", () -> new ArmorMaterial(
                    Map.of(
                            ArmorItem.Type.HELMET, 3,
                            ArmorItem.Type.CHESTPLATE, 8,
                            ArmorItem.Type.LEGGINGS, 6,
                            ArmorItem.Type.BOOTS, 3),
                    18,
                    net.minecraft.sounds.SoundEvents.ARMOR_EQUIP_DIAMOND,
                    () -> Ingredient.of(ModItems.LUVITEIUM_INGOT.get()),
                    List.of(new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath(CreateLuviteium.MOD_ID, "luviteium"))),
                    2.0f,
                    0.1f));

    public static final DeferredItem<BlockItem> LUVITEIUM_ORE = ITEMS.registerSimpleBlockItem("luviteium_ore", ModBlocks.LUVITEIUM_ORE);
    public static final DeferredItem<BlockItem> LUVITEIUM_BLOCK = ITEMS.registerSimpleBlockItem("luviteium_block", ModBlocks.LUVITEIUM_BLOCK);
    public static final DeferredItem<BlockItem> CASTING_TABLE = ITEMS.registerSimpleBlockItem("casting_table", ModBlocks.CASTING_TABLE);
    public static final DeferredItem<BlockItem> BIOMASS_GENERATOR = ITEMS.registerSimpleBlockItem("biomass_generator", ModBlocks.BIOMASS_GENERATOR);

    public static final DeferredItem<Item> RAW_LUVITEIUM = ITEMS.registerSimpleItem("raw_luviteium");
    public static final DeferredItem<Item> LUVITEIUM_INGOT = ITEMS.registerSimpleItem("luviteium_ingot");
    public static final DeferredItem<Item> LUVITEIUM_PANEL = ITEMS.registerSimpleItem("luviteium_panel");
    public static final DeferredItem<Item> HOT_LUVITEIUM_INGOT = ITEMS.registerSimpleItem("hot_luviteium_ingot");
    public static final DeferredItem<Item> HOT_LUVITEIUM_PICKAXE_HEAD = ITEMS.registerSimpleItem("hot_luviteium_pickaxe_head");
    public static final DeferredItem<Item> HOT_LUVITEIUM_AXE_HEAD = ITEMS.registerSimpleItem("hot_luviteium_axe_head");
    public static final DeferredItem<Item> HOT_LUVITEIUM_SHOVEL_HEAD = ITEMS.registerSimpleItem("hot_luviteium_shovel_head");
    public static final DeferredItem<Item> HOT_LUVITEIUM_HOE_HEAD = ITEMS.registerSimpleItem("hot_luviteium_hoe_head");
    public static final DeferredItem<Item> HOT_LUVITEIUM_SWORD_BLADE = ITEMS.registerSimpleItem("hot_luviteium_sword_blade");
    public static final DeferredItem<Item> LUVITEIUM_INGOT_COOLED = ITEMS.registerSimpleItem("cooled_luviteium_ingot");
    public static final DeferredItem<Item> LUVITEIUM_PICKAXE_HEAD = ITEMS.registerSimpleItem("luviteium_pickaxe_head");
    public static final DeferredItem<Item> LUVITEIUM_AXE_HEAD = ITEMS.registerSimpleItem("luviteium_axe_head");
    public static final DeferredItem<Item> LUVITEIUM_SHOVEL_HEAD = ITEMS.registerSimpleItem("luviteium_shovel_head");
    public static final DeferredItem<Item> LUVITEIUM_HOE_HEAD = ITEMS.registerSimpleItem("luviteium_hoe_head");
    public static final DeferredItem<Item> LUVITEIUM_SWORD_BLADE = ITEMS.registerSimpleItem("luviteium_sword_blade");
    public static final DeferredItem<Item> LUVITEIUM_PICKAXE = ITEMS.register("luviteium_pickaxe", () -> new DiggerItem(LuviteiumTier.INSTANCE, net.minecraft.tags.BlockTags.MINEABLE_WITH_PICKAXE, new Item.Properties()));
    public static final DeferredItem<Item> LUVITEIUM_AXE = ITEMS.register("luviteium_axe", () -> new DiggerItem(LuviteiumTier.INSTANCE, net.minecraft.tags.BlockTags.MINEABLE_WITH_AXE, new Item.Properties()));
    public static final DeferredItem<Item> LUVITEIUM_SHOVEL = ITEMS.register("luviteium_shovel", () -> new DiggerItem(LuviteiumTier.INSTANCE, net.minecraft.tags.BlockTags.MINEABLE_WITH_SHOVEL, new Item.Properties()));
    public static final DeferredItem<Item> LUVITEIUM_HOE = ITEMS.register("luviteium_hoe", () -> new DiggerItem(LuviteiumTier.INSTANCE, net.minecraft.tags.BlockTags.MINEABLE_WITH_HOE, new Item.Properties()));
    public static final DeferredItem<Item> LUVITEIUM_SWORD = ITEMS.register("luviteium_sword", () -> new SwordItem(LuviteiumTier.INSTANCE, new Item.Properties()));
    public static final DeferredItem<Item> LUVITEIUM_HELMET = ITEMS.register("luviteium_helmet", () -> new ArmorItem(LUVITEIUM_ARMOR_MATERIAL, ArmorItem.Type.HELMET, new Item.Properties()));
    public static final DeferredItem<Item> LUVITEIUM_CHESTPLATE = ITEMS.register("luviteium_chestplate", () -> new ArmorItem(LUVITEIUM_ARMOR_MATERIAL, ArmorItem.Type.CHESTPLATE, new Item.Properties()));
    public static final DeferredItem<Item> LUVITEIUM_LEGGINGS = ITEMS.register("luviteium_leggings", () -> new ArmorItem(LUVITEIUM_ARMOR_MATERIAL, ArmorItem.Type.LEGGINGS, new Item.Properties()));
    public static final DeferredItem<Item> LUVITEIUM_BOOTS = ITEMS.register("luviteium_boots", () -> new ArmorItem(LUVITEIUM_ARMOR_MATERIAL, ArmorItem.Type.BOOTS, new Item.Properties()));
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
