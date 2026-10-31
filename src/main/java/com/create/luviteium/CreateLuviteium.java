package com.create.luviteium;

import com.create.luviteium.registry.ModBlocks;
import com.create.luviteium.registry.ModItems;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(CreateLuviteium.MOD_ID)
public final class CreateLuviteium {
    public static final String MOD_ID = "luviteium";

    public CreateLuviteium() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModBlocks.BLOCKS.register(modBus);
        ModItems.ITEMS.register(modBus);
    }
}
