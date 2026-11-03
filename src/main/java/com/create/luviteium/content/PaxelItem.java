package com.create.luviteium.content;

import net.minecraft.core.component.DataComponents;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;

import java.util.List;

/** A three-in-one mining tool. It deliberately has no hoe actions. */
public class PaxelItem extends DiggerItem {
    public PaxelItem(Tier tier, Item.Properties properties) {
        super(tier, BlockTags.MINEABLE_WITH_PICKAXE, properties.component(DataComponents.TOOL,
                new Tool(List.of(
                        Tool.Rule.deniesDrops(tier.getIncorrectBlocksForDrops()),
                        Tool.Rule.minesAndDrops(BlockTags.MINEABLE_WITH_PICKAXE, tier.getSpeed()),
                        Tool.Rule.minesAndDrops(BlockTags.MINEABLE_WITH_AXE, tier.getSpeed()),
                        Tool.Rule.minesAndDrops(BlockTags.MINEABLE_WITH_SHOVEL, tier.getSpeed())), 1.0f, 1)));
    }

    @Override
    public boolean canPerformAction(ItemStack stack, ItemAbility ability) {
        return ItemAbilities.DEFAULT_PICKAXE_ACTIONS.contains(ability)
                || ItemAbilities.DEFAULT_AXE_ACTIONS.contains(ability)
                || ItemAbilities.DEFAULT_SHOVEL_ACTIONS.contains(ability);
    }
}
