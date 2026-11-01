package com.create.luviteium.client;

import com.create.luviteium.CreateLuviteium;
import com.create.luviteium.registry.ModFluids;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;

@EventBusSubscriber(modid = CreateLuviteium.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ClientFluidSetup {
    private ClientFluidSetup() {}

    @SubscribeEvent
    public static void registerFluidClientExtensions(RegisterClientExtensionsEvent event) {
        event.registerFluidType(new IClientFluidTypeExtensions() {
            @Override
            public int getTintColor() {
                // The placeholder PNG already contains the intended green metal palette.
                return 0xFFFFFFFF;
            }

            @Override
            public ResourceLocation getStillTexture() {
                return ResourceLocation.fromNamespaceAndPath(CreateLuviteium.MOD_ID, "block/molten_luviteium_still");
            }

            @Override
            public ResourceLocation getFlowingTexture() {
                return ResourceLocation.fromNamespaceAndPath(CreateLuviteium.MOD_ID, "block/molten_luviteium_flowing");
            }
        }, ModFluids.MOLTEN_LUVITEIUM_TYPE);
    }
}
