package com.create.luviteium.client;

import com.create.luviteium.CreateLuviteium;
import com.create.luviteium.registry.ModItems;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;

/** Temporary warm-tinted versions of the existing original armor silhouettes. */
@EventBusSubscriber(modid = CreateLuviteium.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ClientItemColors {
    @SubscribeEvent
    public static void register(RegisterColorHandlersEvent.Item event) {
        event.register((stack, tint) -> 0xFFFF9955,
                ModItems.HOT_LUVITEIUM_HELMET.get(), ModItems.HOT_LUVITEIUM_CHESTPLATE.get(),
                ModItems.HOT_LUVITEIUM_LEGGINGS.get(), ModItems.HOT_LUVITEIUM_BOOTS.get());
    }
}
