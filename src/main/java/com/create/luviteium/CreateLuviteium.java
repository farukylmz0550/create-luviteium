package com.create.luviteium;

import com.create.luviteium.registry.ModBlocks;
import com.create.luviteium.registry.ModItems;
import com.create.luviteium.registry.ModFluids;
import com.create.luviteium.registry.ModBlockEntities;
import com.simibubi.create.content.kinetics.deployer.DeployerRecipeSearchEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.minecraft.world.item.ItemStack;

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
        NeoForge.EVENT_BUS.addListener(this::rejectDamagedMoldSamples);
    }

    private void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, ModBlockEntities.CASTING_TABLE.get(),
                (table, side) -> table.getFluidHandler(side));
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntities.CASTING_TABLE.get(),
                (table, side) -> table.getItemHandler(side));
    }

    private void rejectDamagedMoldSamples(DeployerRecipeSearchEvent event) {
        ItemStack base = event.getInventory().getItem(0);
        ItemStack sample = event.getInventory().getItem(1);
        if (base.is(ModItems.BLANK_MOLD.get()) && sample.isDamageableItem() && sample.isDamaged()) {
            event.setCanceled(true);
        }
    }
}
