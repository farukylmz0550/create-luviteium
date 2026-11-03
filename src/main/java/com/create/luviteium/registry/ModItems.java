package com.create.luviteium.registry;

import com.create.luviteium.CreateLuviteium;
import com.create.luviteium.content.LuviteiumTier;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
import com.create.luviteium.content.PaxelItem;
import com.create.luviteium.content.CharmItem;
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
                            ArmorItem.Type.CHESTPLATE, 7,
                            ArmorItem.Type.LEGGINGS, 5,
                            ArmorItem.Type.BOOTS, 3),
                    9,
                    net.minecraft.sounds.SoundEvents.ARMOR_EQUIP_DIAMOND,
                    () -> Ingredient.of(ModItems.LUVITEIUM_INGOT.get()),
                    List.of(new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath(CreateLuviteium.MOD_ID, "luviteium"))),
                    1.0f,
                    0.05f));

    public static final DeferredItem<BlockItem> LUVITEIUM_ORE = ITEMS.registerSimpleBlockItem("luviteium_ore", ModBlocks.LUVITEIUM_ORE);
    public static final DeferredItem<BlockItem> LUVITEIUM_BLOCK = ITEMS.registerSimpleBlockItem("luviteium_block", ModBlocks.LUVITEIUM_BLOCK);
    public static final DeferredItem<BlockItem> CASTING_TABLE = ITEMS.registerSimpleBlockItem("casting_table", ModBlocks.CASTING_TABLE);
    public static final DeferredItem<BlockItem> BIOMASS_GENERATOR = ITEMS.registerSimpleBlockItem("biomass_generator", ModBlocks.BIOMASS_GENERATOR);

    public static final DeferredItem<Item> RAW_LUVITEIUM = ITEMS.registerSimpleItem("raw_luviteium");
    public static final DeferredItem<Item> LUVITEIUM_INGOT = ITEMS.registerSimpleItem("luviteium_ingot");
    public static final DeferredItem<Item> LUVITEIUM_PANEL = ITEMS.registerSimpleItem("luviteium_panel");
    public static final DeferredItem<Item> LUVITEIUM_BUCKET = ITEMS.registerSimpleItem("luviteium_bucket");
    public static final DeferredItem<Item> HOT_LUVITEIUM_INGOT = ITEMS.registerSimpleItem("hot_luviteium_ingot");
    public static final DeferredItem<Item> HOT_LUVITEIUM_PICKAXE_HEAD = ITEMS.registerSimpleItem("hot_luviteium_pickaxe_head");
    public static final DeferredItem<Item> HOT_LUVITEIUM_AXE_HEAD = ITEMS.registerSimpleItem("hot_luviteium_axe_head");
    public static final DeferredItem<Item> HOT_LUVITEIUM_SHOVEL_HEAD = ITEMS.registerSimpleItem("hot_luviteium_shovel_head");
    public static final DeferredItem<Item> HOT_LUVITEIUM_HOE_HEAD = ITEMS.registerSimpleItem("hot_luviteium_hoe_head");
    public static final DeferredItem<Item> HOT_LUVITEIUM_SWORD_BLADE = ITEMS.registerSimpleItem("hot_luviteium_sword_blade");
    public static final DeferredItem<Item> HOT_LUVITEIUM_HELMET = ITEMS.registerSimpleItem("hot_luviteium_helmet");
    public static final DeferredItem<Item> HOT_LUVITEIUM_CHESTPLATE = ITEMS.registerSimpleItem("hot_luviteium_chestplate");
    public static final DeferredItem<Item> HOT_LUVITEIUM_LEGGINGS = ITEMS.registerSimpleItem("hot_luviteium_leggings");
    public static final DeferredItem<Item> HOT_LUVITEIUM_BOOTS = ITEMS.registerSimpleItem("hot_luviteium_boots");
    public static final DeferredItem<Item> LUVITEIUM_PICKAXE_HEAD = ITEMS.registerSimpleItem("luviteium_pickaxe_head");
    public static final DeferredItem<Item> LUVITEIUM_AXE_HEAD = ITEMS.registerSimpleItem("luviteium_axe_head");
    public static final DeferredItem<Item> LUVITEIUM_SHOVEL_HEAD = ITEMS.registerSimpleItem("luviteium_shovel_head");
    public static final DeferredItem<Item> LUVITEIUM_HOE_HEAD = ITEMS.registerSimpleItem("luviteium_hoe_head");
    public static final DeferredItem<Item> LUVITEIUM_SWORD_BLADE = ITEMS.registerSimpleItem("luviteium_sword_blade");
    public static final DeferredItem<Item> LUVITEIUM_PICKAXE = ITEMS.register("luviteium_pickaxe", () -> new PickaxeItem(LuviteiumTier.INSTANCE, new Item.Properties()
            .attributes(PickaxeItem.createAttributes(LuviteiumTier.INSTANCE, 1.0f, -2.8f))));
    public static final DeferredItem<Item> LUVITEIUM_AXE = ITEMS.register("luviteium_axe", () -> new AxeItem(LuviteiumTier.INSTANCE, new Item.Properties()
            .attributes(AxeItem.createAttributes(LuviteiumTier.INSTANCE, 5.5f, -3.1f))));
    public static final DeferredItem<Item> LUVITEIUM_SHOVEL = ITEMS.register("luviteium_shovel", () -> new ShovelItem(LuviteiumTier.INSTANCE, new Item.Properties()
            .attributes(ShovelItem.createAttributes(LuviteiumTier.INSTANCE, 1.5f, -3.0f))));
    public static final DeferredItem<Item> WOODEN_PAXEL = ITEMS.register("wooden_paxel", () -> new PaxelItem(Tiers.WOOD, new Item.Properties().attributes(PaxelItem.createAttributes(Tiers.WOOD, 1.0f, -2.8f))));
    public static final DeferredItem<Item> STONE_PAXEL = ITEMS.register("stone_paxel", () -> new PaxelItem(Tiers.STONE, new Item.Properties().attributes(PaxelItem.createAttributes(Tiers.STONE, 1.0f, -2.8f))));
    public static final DeferredItem<Item> IRON_PAXEL = ITEMS.register("iron_paxel", () -> new PaxelItem(Tiers.IRON, new Item.Properties().attributes(PaxelItem.createAttributes(Tiers.IRON, 1.0f, -2.8f))));
    public static final DeferredItem<Item> GOLDEN_PAXEL = ITEMS.register("golden_paxel", () -> new PaxelItem(Tiers.GOLD, new Item.Properties().attributes(PaxelItem.createAttributes(Tiers.GOLD, 1.0f, -2.8f))));
    public static final DeferredItem<Item> DIAMOND_PAXEL = ITEMS.register("diamond_paxel", () -> new PaxelItem(Tiers.DIAMOND, new Item.Properties().attributes(PaxelItem.createAttributes(Tiers.DIAMOND, 1.0f, -2.8f))));
    public static final DeferredItem<Item> NETHERITE_PAXEL = ITEMS.register("netherite_paxel", () -> new PaxelItem(Tiers.NETHERITE, new Item.Properties().fireResistant().attributes(PaxelItem.createAttributes(Tiers.NETHERITE, 1.0f, -2.8f))));
    public static final DeferredItem<Item> LUVITEIUM_HOE = ITEMS.register("luviteium_hoe", () -> new HoeItem(LuviteiumTier.INSTANCE, new Item.Properties()
            .attributes(HoeItem.createAttributes(LuviteiumTier.INSTANCE, -2.5f, -1.0f))));
    public static final DeferredItem<Item> LUVITEIUM_SWORD = ITEMS.register("luviteium_sword", () -> new SwordItem(LuviteiumTier.INSTANCE, new Item.Properties()
            .attributes(SwordItem.createAttributes(LuviteiumTier.INSTANCE, 3, -2.4f))));
    public static final DeferredItem<Item> LUVITEIUM_PAXEL = ITEMS.register("luviteium_paxel", () -> new PaxelItem(LuviteiumTier.INSTANCE, new Item.Properties()
            .attributes(PaxelItem.createAttributes(LuviteiumTier.INSTANCE, 1.0f, -2.8f))));
    public static final DeferredItem<Item> LUVITEIUM_HELMET = ITEMS.register("luviteium_helmet", () -> new ArmorItem(LUVITEIUM_ARMOR_MATERIAL, ArmorItem.Type.HELMET, new Item.Properties()
            .durability(ArmorItem.Type.HELMET.getDurability(30))));
    public static final DeferredItem<Item> LUVITEIUM_CHESTPLATE = ITEMS.register("luviteium_chestplate", () -> new ArmorItem(LUVITEIUM_ARMOR_MATERIAL, ArmorItem.Type.CHESTPLATE, new Item.Properties()
            .durability(ArmorItem.Type.CHESTPLATE.getDurability(30))));
    public static final DeferredItem<Item> LUVITEIUM_LEGGINGS = ITEMS.register("luviteium_leggings", () -> new ArmorItem(LUVITEIUM_ARMOR_MATERIAL, ArmorItem.Type.LEGGINGS, new Item.Properties()
            .durability(ArmorItem.Type.LEGGINGS.getDurability(30))));
    public static final DeferredItem<Item> LUVITEIUM_BOOTS = ITEMS.register("luviteium_boots", () -> new ArmorItem(LUVITEIUM_ARMOR_MATERIAL, ArmorItem.Type.BOOTS, new Item.Properties()
            .durability(ArmorItem.Type.BOOTS.getDurability(30))));
    public static final DeferredItem<Item> BLANK_MOLD = ITEMS.registerSimpleItem("blank_mold");

    // The ten intentionally supported mold shapes. Source mappings are recorded in docs/molds.md.
    public static final DeferredItem<Item> INGOT_MOLD = registerSpecializedMold("ingot_mold");
    public static final DeferredItem<Item> PICKAXE_HEAD_MOLD = registerSpecializedMold("pickaxe_head_mold");
    public static final DeferredItem<Item> AXE_HEAD_MOLD = registerSpecializedMold("axe_head_mold");
    public static final DeferredItem<Item> SHOVEL_HEAD_MOLD = registerSpecializedMold("shovel_head_mold");
    public static final DeferredItem<Item> HOE_HEAD_MOLD = registerSpecializedMold("hoe_head_mold");
    public static final DeferredItem<Item> SWORD_BLADE_MOLD = registerSpecializedMold("sword_blade_mold");
    public static final DeferredItem<Item> HELMET_MOLD = registerSpecializedMold("helmet_mold");
    public static final DeferredItem<Item> CHESTPLATE_MOLD = registerSpecializedMold("chestplate_mold");
    public static final DeferredItem<Item> LEGGINGS_MOLD = registerSpecializedMold("leggings_mold");
    public static final DeferredItem<Item> BOOTS_MOLD = registerSpecializedMold("boots_mold");
    public static final DeferredItem<Item> PAXEL_HEAD_MOLD = registerSpecializedMold("paxel_head_mold");
    public static final DeferredItem<Item> LUVITEIUM_PAXEL_HEAD = ITEMS.registerSimpleItem("luviteium_paxel_head");

    public static final DeferredItem<Item> NAZAR_BONCUGU = ITEMS.register("nazar_boncugu", CharmItem::new);
    public static final DeferredItem<Item> BAYRAMIN_BEREKETI = ITEMS.register("bayramin_bereketi", CharmItem::new);

    private static DeferredItem<Item> registerSpecializedMold(String name) {
        return ITEMS.register(name, () -> new Item(new Item.Properties().stacksTo(1)));
    }

    private ModItems() {}
}
