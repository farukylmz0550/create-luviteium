package com.create.luviteium.registry;

import com.create.luviteium.CreateLuviteium;
import com.create.luviteium.content.CastingTableBlockEntity;
import com.create.luviteium.content.BiomassGeneratorBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, CreateLuviteium.MOD_ID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CastingTableBlockEntity>> CASTING_TABLE =
            BLOCK_ENTITIES.register("casting_table", () -> BlockEntityType.Builder
                    .of(CastingTableBlockEntity::new, ModBlocks.CASTING_TABLE.get())
                    .build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BiomassGeneratorBlockEntity>> BIOMASS_GENERATOR =
            BLOCK_ENTITIES.register("biomass_generator", () -> BlockEntityType.Builder
                    .of(BiomassGeneratorBlockEntity::new, ModBlocks.BIOMASS_GENERATOR.get())
                    .build(null));

    private ModBlockEntities() {}
}
