package com.create.luviteium.content;

import com.create.luviteium.registry.ModItems;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;

public final class LuviteiumTier implements Tier {
    public static final LuviteiumTier INSTANCE = new LuviteiumTier();

    private LuviteiumTier() {}

    @Override
    public int getUses() {
        return 1250;
    }

    @Override
    public float getSpeed() {
        return 7.5f;
    }

    @Override
    public float getAttackDamageBonus() {
        // Player base 1 + pickaxe modifier 1 + tier bonus 2.5 = 4.5 damage.
        return 2.5f;
    }

    @Override
    public TagKey<Block> getIncorrectBlocksForDrops() {
        return BlockTags.INCORRECT_FOR_DIAMOND_TOOL;
    }

    @Override
    public int getEnchantmentValue() {
        return 9;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return Ingredient.of(ModItems.LUVITEIUM_INGOT.get());
    }
}
