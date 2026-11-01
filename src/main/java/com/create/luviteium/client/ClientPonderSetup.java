package com.create.luviteium.client;

import com.create.luviteium.CreateLuviteium;
import com.create.luviteium.client.ponder.LuviteiumPonderPlugin;
import net.createmod.ponder.foundation.PonderIndex;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.Mod;

/** Client-only entry point: registered before Ponder builds its scene index. */
@Mod(value = CreateLuviteium.MOD_ID, dist = Dist.CLIENT)
public final class ClientPonderSetup {
    public ClientPonderSetup() {
        PonderIndex.addPlugin(new LuviteiumPonderPlugin());
    }
}
