package com.create.luviteium.registry;

import com.create.luviteium.CreateLuviteium;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.Util;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.FluidType.Properties;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public final class ModFluids {
    public static final DeferredRegister<Fluid> FLUIDS =
            DeferredRegister.create(Registries.FLUID, CreateLuviteium.MOD_ID);
    public static final DeferredRegister<FluidType> FLUID_TYPES =
            DeferredRegister.create(NeoForgeRegistries.FLUID_TYPES, CreateLuviteium.MOD_ID);

    public static final DeferredHolder<FluidType, FluidType> MOLTEN_LUVITEIUM_TYPE = FLUID_TYPES.register(
            "molten_luviteium",
            id -> new FluidType(Properties.create()
                    .descriptionId(Util.makeDescriptionId("fluid", id))
                    .sound(net.neoforged.neoforge.common.SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL_LAVA)
                    .sound(net.neoforged.neoforge.common.SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY_LAVA)
                    .canDrown(false)
                    .canSwim(false)
                    .lightLevel(8)
                    .temperature(1300)
                    .density(2000)
                    .viscosity(5000)));

    public static final DeferredHolder<Fluid, BaseFlowingFluid.Source> MOLTEN_LUVITEIUM = FLUIDS.register(
            "molten_luviteium",
            () -> new BaseFlowingFluid.Source(ModFluids.MOLTEN_LUVITEIUM_PROPERTIES));

    public static final DeferredHolder<Fluid, BaseFlowingFluid.Flowing> FLOWING_MOLTEN_LUVITEIUM = FLUIDS.register(
            "flowing_molten_luviteium",
            () -> new BaseFlowingFluid.Flowing(ModFluids.MOLTEN_LUVITEIUM_PROPERTIES));

    public static final BaseFlowingFluid.Properties MOLTEN_LUVITEIUM_PROPERTIES =
            new BaseFlowingFluid.Properties(MOLTEN_LUVITEIUM_TYPE, MOLTEN_LUVITEIUM, FLOWING_MOLTEN_LUVITEIUM);

    private ModFluids() {}
}
