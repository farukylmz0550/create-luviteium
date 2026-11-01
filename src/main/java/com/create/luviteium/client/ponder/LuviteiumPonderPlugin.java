package com.create.luviteium.client.ponder;

import com.create.luviteium.CreateLuviteium;
import net.createmod.ponder.api.registration.PonderPlugin;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.minecraft.resources.ResourceLocation;

public final class LuviteiumPonderPlugin implements PonderPlugin {
    @Override
    public String getModId() {
        return CreateLuviteium.MOD_ID;
    }

    @Override
    public void registerScenes(PonderSceneRegistrationHelper<ResourceLocation> helper) {
        helper.forComponents(id("casting_table"), id("raw_luviteium"), id("luviteium_bucket"), id("ingot_mold"))
                .addStoryBoard("workshop", LuviteiumScenes::casting);
        helper.forComponents(id("blank_mold"), id("pickaxe_head_mold"), id("hot_luviteium_pickaxe_head"),
                id("luviteium_pickaxe_head"), id("luviteium_pickaxe"))
                .addStoryBoard("workshop", LuviteiumScenes::molds);
        helper.forComponents(id("biomass_generator"))
                .addStoryBoard("workshop", LuviteiumScenes::biomass);
    }

    private static ResourceLocation id(String name) {
        return ResourceLocation.fromNamespaceAndPath(CreateLuviteium.MOD_ID, name);
    }
}
