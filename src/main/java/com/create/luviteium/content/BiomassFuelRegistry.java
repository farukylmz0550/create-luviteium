package com.create.luviteium.content;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;
import java.util.function.Predicate;

/** Ordered, single-source fuel policy for the Biomass Generator. */
public final class BiomassFuelRegistry {
    private record FuelRule(int ticks, Predicate<ItemStack> matches) {}

    private static final List<FuelRule> RULES = List.of(
            new FuelRule(120, BiomassFuelRegistry::isStrippedWood),
            new FuelRule(150, stack -> stack.is(ItemTags.LOGS_THAT_BURN)),
            new FuelRule(38, stack -> stack.is(ItemTags.PLANKS)),
            new FuelRule(75, stack -> stack.is(ItemTags.SAPLINGS)),
            new FuelRule(15, stack -> stack.is(ItemTags.LEAVES)),
            new FuelRule(19, stack -> stack.is(Items.STICK)),
            new FuelRule(38, stack -> stack.is(Items.BAMBOO)),
            new FuelRule(38, stack -> stack.is(Items.CACTUS)),
            new FuelRule(30, stack -> stack.is(Items.SUGAR_CANE)),
            new FuelRule(15, stack -> stack.is(Items.VINE)),
            new FuelRule(15, stack -> stack.is(ItemTags.FLOWERS)),
            new FuelRule(120, stack -> stack.is(Items.MOSS_BLOCK)),
            new FuelRule(15, stack -> stack.is(Items.MOSS_CARPET)),
            new FuelRule(300, stack -> stack.is(Items.DRIED_KELP_BLOCK)),
            new FuelRule(270, stack -> stack.is(Items.HAY_BLOCK)),
            new FuelRule(30, stack -> stack.is(Items.WHEAT)),
            new FuelRule(30, stack -> stack.is(Items.CARROT)),
            new FuelRule(30, stack -> stack.is(Items.POTATO)),
            new FuelRule(30, stack -> stack.is(Items.BEETROOT)),
            new FuelRule(15, stack -> stack.is(ItemTags.VILLAGER_PLANTABLE_SEEDS))
    );

    private BiomassFuelRegistry() {}

    public static int burnTimeFor(ItemStack stack) {
        if (stack.isEmpty()) return 0;
        for (FuelRule rule : RULES) {
            if (rule.matches().test(stack)) return rule.ticks();
        }
        return 0;
    }

    private static boolean isStrippedWood(ItemStack stack) {
        if (!stack.is(ItemTags.LOGS_THAT_BURN)) return false;
        String path = BuiltInRegistries.ITEM.getKey(stack.getItem()).getPath();
        return path.startsWith("stripped_") || path.contains("_stripped_");
    }
}
