package com.create.luviteium.client.ponder;

import com.create.luviteium.content.CastingTableBlockEntity;
import com.create.luviteium.registry.ModBlocks;
import com.create.luviteium.registry.ModFluids;
import com.create.luviteium.registry.ModItems;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.kinetics.base.DirectionalKineticBlock;
import com.simibubi.create.foundation.ponder.CreateSceneBuilder;
import net.createmod.catnip.math.Pointing;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

public final class LuviteiumScenes {
    private LuviteiumScenes() {}

    public static void casting(SceneBuilder builder, SceneBuildingUtil util) {
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        scene.title("casting", "Casting Luviteium with reusable molds");
        scene.configureBasePlate(0, 0, 7);
        scene.showBasePlate();
        BlockPos table = util.grid().at(4, 1, 3);
        scene.world().setBlock(table, ModBlocks.CASTING_TABLE.get().defaultBlockState(), false);
        scene.world().showSection(util.select().position(table), Direction.DOWN);
        scene.idle(20);
        scene.overlay().showControls(util.vector().topOf(table), Pointing.DOWN, 60)
                .rightClick().withItem(ModItems.INGOT_MOLD.toStack());
        scene.overlay().showText(80).text("Insert a reusable mold before supplying molten Luviteium.")
                .pointAt(util.vector().topOf(table)).placeNearTarget();
        scene.world().modifyBlockEntity(table, CastingTableBlockEntity.class,
                be -> be.getItemHandler(null).insertItem(0, ModItems.INGOT_MOLD.toStack(), false));
        scene.idle(90);
        scene.addKeyframe();
        scene.world().setBlock(util.grid().at(1, 1, 3), AllBlocks.FLUID_TANK.getDefaultState(), false);
        scene.world().setBlock(util.grid().at(2, 1, 3), AllBlocks.MECHANICAL_PUMP.getDefaultState()
                .setValue(DirectionalKineticBlock.FACING, Direction.EAST), false);
        scene.world().setBlock(util.grid().at(3, 1, 3), AllBlocks.FLUID_PIPE.getDefaultState()
                .setValue(BlockStateProperties.EAST, true).setValue(BlockStateProperties.WEST, true), false);
        scene.world().showSection(util.select().fromTo(1, 1, 3, 3, 1, 3), Direction.DOWN);
        scene.world().setKineticSpeed(util.select().position(2, 1, 3), 32);
        scene.overlay().showText(100).text("Heated mixing produces 144 mB per Raw Luviteium. Power the pump to feed the table through pipes.")
                .pointAt(util.vector().centerOf(2, 1, 3)).placeNearTarget();
        scene.idle(110);
        scene.addKeyframe();
        scene.world().modifyBlockEntity(table, CastingTableBlockEntity.class, be -> {
            be.getFluidHandler(null).fill(new FluidStack(ModFluids.MOLTEN_LUVITEIUM.get(), 144),
                    IFluidHandler.FluidAction.EXECUTE);
            // Ponder worlds do not run ordinary server-side casting ticks.
        });
        scene.overlay().showText(80).text("The ingot mold holds exactly 144 mB and casts in 20 ticks. Tool heads need 288 mB and 40 ticks.")
                .pointAt(util.vector().topOf(table)).placeNearTarget();
        scene.idle(20);
        scene.world().modifyBlockEntity(table, CastingTableBlockEntity.class, be -> {
            for (int i = 0; i < 20; i++) CastingTableBlockEntity.serverTick(be.getLevel(), table, be.getBlockState(), be);
        });
        scene.world().createItemEntity(util.vector().topOf(table).add(0, 0.3, 0),
                util.vector().of(0, 0, 0), ModItems.HOT_LUVITEIUM_INGOT.toStack());
        scene.idle(70);
        scene.addKeyframe();
        scene.overlay().showText(100).text("Collect the hot output, then fan-wash it into an ingot. Press the ingot into a panel. Sneak-right-click empty-handed to remove an idle mold.")
                .pointAt(util.vector().topOf(table)).placeNearTarget();
        scene.idle(110);
        scene.markAsFinished();
    }

    public static void molds(SceneBuilder builder, SceneBuildingUtil util) {
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        scene.title("molds", "Imprinting and cooling tool parts");
        scene.configureBasePlate(0, 0, 7);
        scene.showBasePlate();
        BlockPos depot = util.grid().at(2, 1, 3);
        BlockPos deployer = util.grid().at(2, 3, 3);
        scene.world().setBlock(depot, AllBlocks.DEPOT.getDefaultState(), false);
        scene.world().setBlock(deployer, AllBlocks.DEPLOYER.getDefaultState()
                .setValue(DirectionalKineticBlock.FACING, Direction.DOWN), false);
        scene.world().showSection(util.select().fromTo(2, 1, 3, 2, 3, 3), Direction.DOWN);
        scene.world().createItemOnBeltLike(depot, Direction.NORTH, ModItems.BLANK_MOLD.toStack());
        scene.world().setKineticSpeed(util.select().position(deployer), 32);
        scene.overlay().showControls(util.vector().centerOf(deployer), Pointing.RIGHT, 80)
                .rightClick().withItem(new ItemStack(Items.IRON_PICKAXE));
        scene.overlay().showText(100).text("Place a Blank Mold on a Depot. Give the powered Deployer an undamaged sample tool or armor piece.")
                .pointAt(util.vector().centerOf(deployer)).placeNearTarget();
        scene.idle(110);
        scene.addKeyframe();
        scene.world().moveDeployer(deployer, 1, 20);
        scene.idle(20);
        scene.world().removeItemsFromBelt(depot);
        scene.world().createItemOnBeltLike(depot, Direction.NORTH, ModItems.PICKAXE_HEAD_MOLD.toStack());
        scene.world().moveDeployer(deployer, -1, 20);
        scene.overlay().showText(90).text("The sample is consumed. The resulting mold is reusable; damaged samples cannot imprint it.")
                .pointAt(util.vector().topOf(depot)).placeNearTarget();
        scene.idle(100);
        scene.addKeyframe();
        BlockPos washing = util.grid().at(4, 1, 2);
        scene.world().setBlock(washing, AllBlocks.DEPOT.getDefaultState(), false);
        scene.world().setBlock(util.grid().at(4, 1, 4), AllBlocks.ENCASED_FAN.getDefaultState()
                .setValue(DirectionalKineticBlock.FACING, Direction.NORTH), false);
        scene.world().setBlock(util.grid().at(4, 1, 3), Blocks.WATER.defaultBlockState(), false);
        scene.world().showSection(util.select().fromTo(4, 1, 2, 4, 1, 4), Direction.DOWN);
        scene.world().setKineticSpeed(util.select().position(4, 1, 4), 32);
        scene.world().createItemOnBeltLike(washing, Direction.NORTH, ModItems.HOT_LUVITEIUM_PICKAXE_HEAD.toStack());
        scene.overlay().showText(100).text("Cast the head, then wash it with a powered fan blowing through water. Only cooled heads can be crafted with sticks.")
                .pointAt(util.vector().topOf(washing)).placeNearTarget();
        scene.idle(70);
        scene.world().removeItemsFromBelt(washing);
        scene.world().createItemOnBeltLike(washing, Direction.NORTH, ModItems.LUVITEIUM_PICKAXE_HEAD.toStack());
        scene.idle(40);
        scene.markAsFinished();
    }

    public static void biomass(SceneBuilder builder, SceneBuildingUtil util) {
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        scene.title("biomass", "Powering machines with biomass");
        scene.configureBasePlate(0, 0, 7);
        scene.showBasePlate();
        BlockPos generator = util.grid().at(3, 1, 3);
        scene.world().setBlock(generator, ModBlocks.BIOMASS_GENERATOR.get().defaultBlockState()
                .setValue(DirectionalKineticBlock.FACING, Direction.EAST), false);
        scene.world().setBlock(util.grid().at(3, 2, 3), AllBlocks.CHUTE.getDefaultState(), false);
        scene.world().setBlock(util.grid().at(3, 3, 3), Blocks.BARREL.defaultBlockState(), false);
        scene.world().setBlocks(util.select().fromTo(4, 1, 3, 5, 1, 3), AllBlocks.SHAFT.getDefaultState()
                .setValue(BlockStateProperties.AXIS, Direction.Axis.X), false);
        scene.world().showSection(util.select().fromTo(3, 1, 3, 5, 1, 3), Direction.DOWN);
        scene.idle(20);
        scene.world().showSection(util.select().fromTo(3, 2, 3, 3, 3, 3), Direction.DOWN);
        scene.overlay().showControls(util.vector().topOf(util.grid().at(3, 3, 3)), Pointing.DOWN, 80)
                .withItem(new ItemStack(Items.OAK_LOG));
        scene.overlay().showText(100).text("Feed biomass through a Chute or Funnel. The generator has one inventory slot holding up to 64 items.")
                .independent(80);
        scene.idle(110);
        scene.addKeyframe();
        scene.world().setKineticSpeed(util.select().fromTo(3, 1, 3, 5, 1, 3), 64);
        scene.overlay().showText(100).text("Connect shafts along the generator axis. While fuel burns, it generates 64 RPM with a total capacity of 512 SU.")
                .independent(80);
        scene.idle(110);
        scene.addKeyframe();
        scene.overlay().showText(100).text("Burnable logs, planks, saplings, leaves, flowers and villager-plantable seeds are accepted. Coal, charcoal and lava are rejected.")
                .independent(80);
        scene.idle(110);
        scene.world().setKineticSpeed(util.select().fromTo(3, 1, 3, 5, 1, 3), 0);
        scene.overlay().showText(100).text("Fuel burns even without a load. The generator stops when empty and restarts when fuel arrives; redstone does not control it.")
                .independent(80);
        scene.idle(55);
        scene.world().setKineticSpeed(util.select().fromTo(3, 1, 3, 5, 1, 3), 64);
        scene.idle(55);
        scene.markAsFinished();
    }
}
