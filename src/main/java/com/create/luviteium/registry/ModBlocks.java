package com.create.luviteium.registry;

import com.create.luviteium.CreateLuviteium;
import com.create.luviteium.content.CastingTableBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(CreateLuviteium.MOD_ID);

    public static final DeferredBlock<Block> LUVITEIUM_ORE = BLOCKS.registerSimpleBlock(
            "luviteium_ore",
            BlockBehaviour.Properties.of().strength(3.0f, 3.0f).requiresCorrectToolForDrops().sound(SoundType.STONE));

    public static final DeferredBlock<Block> LUVITEIUM_BLOCK = BLOCKS.registerSimpleBlock(
            "luviteium_block",
            BlockBehaviour.Properties.of().strength(5.0f, 6.0f).requiresCorrectToolForDrops().sound(SoundType.METAL));

    public static final DeferredBlock<CastingTableBlock> CASTING_TABLE = BLOCKS.register(
            "casting_table",
            () -> new CastingTableBlock(BlockBehaviour.Properties.of().strength(2.5f, 6.0f).sound(SoundType.METAL)));

    public static final DeferredBlock<Block> BIOMASS_GENERATOR = BLOCKS.registerSimpleBlock(
            "biomass_generator",
            BlockBehaviour.Properties.of().strength(3.0f, 6.0f).sound(SoundType.METAL));

    private ModBlocks() {}
}
