package com.create.luviteium;

import com.create.luviteium.registry.ModBlocks;
import com.create.luviteium.registry.ModItems;
import com.create.luviteium.registry.ModFluids;
import com.create.luviteium.registry.ModBlockEntities;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(CreateLuviteium.MOD_ID)
public final class CreateLuviteium {
    public static final String MOD_ID = "luviteium";

    public CreateLuviteium(IEventBus modBus) {
        ModBlocks.BLOCKS.register(modBus);
        ModItems.ITEMS.register(modBus);
        ModFluids.FLUID_TYPES.register(modBus);
        ModFluids.FLUIDS.register(modBus);
        ModBlockEntities.BLOCK_ENTITIES.register(modBus);
        modBus.addListener(this::registerCapabilities);
    }

    private void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, ModBlockEntities.CASTING_TABLE.get(),
                (table, side) -> table.getFluidHandler(side));
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntities.CASTING_TABLE.get(),
                (table, side) -> table.getItemHandler(side));
    }
}
