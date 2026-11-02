package com.create.luviteium.debug;

import com.create.luviteium.content.BiomassGeneratorBlockEntity;
import com.create.luviteium.content.CastingTableBlockEntity;
import com.create.luviteium.registry.ModBlocks;
import com.create.luviteium.registry.ModFluids;
import com.create.luviteium.registry.ModItems;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.util.FakePlayerFactory;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
import net.neoforged.neoforge.items.IItemHandler;

/** Runs only when the GameTest framework is invoked; never on ordinary startup. */
@GameTestHolder("luviteium")
@PrefixGameTestTemplate(false)
public final class RegressionTests {
    private static final BlockPos POS = new BlockPos(3, 1, 3);

    private static CastingTableBlockEntity table(GameTestHelper h) {
        h.setBlock(POS, ModBlocks.CASTING_TABLE.get());
        CastingTableBlockEntity be = h.getBlockEntity(POS);
        be.getItemHandler(null).insertItem(0, ModItems.INGOT_MOLD.toStack(), false);
        return be;
    }

    private static void step(CastingTableBlockEntity be, int ticks) {
        for (int i = 0; i < ticks; i++)
            CastingTableBlockEntity.serverTick(be.getLevel(), be.getBlockPos(), be.getBlockState(), be);
    }

    @GameTest(template = "empty")
    public static void castingAnimationSyncDoesNotAdvanceProduction(GameTestHelper h) {
        CastingTableBlockEntity server = table(h);
        server.getFluidHandler(null).fill(new FluidStack(ModFluids.MOLTEN_LUVITEIUM.get(), 72), IFluidHandler.FluidAction.EXECUTE);
        h.assertTrue(server.getUpdatePacket() != null, "Casting machine provides an update packet");
        CastingTableBlockEntity client = new CastingTableBlockEntity(server.getBlockPos(), server.getBlockState());
        client.loadWithComponents(server.getUpdateTag(h.getLevel().registryAccess()), h.getLevel().registryAccess());
        h.assertTrue(client.getFillRatio() == 0.5f && !client.getDisplayedMold().isEmpty(), "Initial chunk tag carries mold and partial fluid");
        for (int i = 0; i < 40; i++) CastingTableBlockEntity.clientTick(h.getLevel(), client.getBlockPos(), client.getBlockState(), client);
        h.assertTrue(Math.abs(client.getVisualFill(1) - 0.5f) < 0.001f, "Client filling animation converges");
        h.assertTrue(client.getProgressRatio() == 0 && client.getDisplayedOutput().isEmpty(), "Visual ticking cannot cast or duplicate an item");
        server.getFluidHandler(null).fill(new FluidStack(ModFluids.MOLTEN_LUVITEIUM.get(), 72), IFluidHandler.FluidAction.EXECUTE);
        step(server, 8);
        client.loadWithComponents(server.getUpdateTag(h.getLevel().registryAccess()), h.getLevel().registryAccess());
        h.assertTrue(client.getProgressRatio() == 0.4f && client.isCasting(), "Active progress is synchronized");
        server.getItemHandler(null).setStackInSlot(1, ModItems.HOT_LUVITEIUM_INGOT.toStack(64));
        client.loadWithComponents(server.getUpdateTag(h.getLevel().registryAccess()), h.getLevel().registryAccess());
        h.assertFalse(client.isCasting(), "Blocked output stops the working animation");
        server.getItemHandler(null).extractItem(1, 64, false);
        step(server, 12);
        client.loadWithComponents(server.getUpdateTag(h.getLevel().registryAccess()), h.getLevel().registryAccess());
        h.assertTrue(client.getDisplayedOutput().is(ModItems.HOT_LUVITEIUM_INGOT.get()) && client.getFillRatio() == 0,
                "Completion sync drains visual fluid and shows the real output");
        h.succeed();
    }

    @GameTest(template = "empty")
    public static void castingLimitsAndPersistence(GameTestHelper h) {
        CastingTableBlockEntity be = table(h);
        var fluid = be.getFluidHandler(null);
        h.assertTrue(fluid.fill(new FluidStack(ModFluids.MOLTEN_LUVITEIUM.get(), 1000),
                IFluidHandler.FluidAction.EXECUTE) == 144, "Tank must reject overfill");
        h.assertTrue(be.getItemHandler(null).extractItem(0, 1, false).isEmpty(), "Filled mold must be locked");
        step(be, 9);
        h.assertTrue(be.getItemHandler(null).getStackInSlot(1).isEmpty(), "No early output");
        CompoundTag saved = be.saveWithoutMetadata(h.getLevel().registryAccess());
        CastingTableBlockEntity restored = new CastingTableBlockEntity(be.getBlockPos(), be.getBlockState());
        restored.loadWithComponents(saved, h.getLevel().registryAccess());
        h.assertTrue(restored.getFluidHandler(null).getCapacity() == 144, "Tank capacity restored from mold");
        step(restored, 10);
        h.assertTrue(restored.getItemHandler(null).getStackInSlot(1).isEmpty(), "19 ticks must not finish");
        step(restored, 1);
        h.assertTrue(restored.getItemHandler(null).getStackInSlot(1).is(ModItems.HOT_LUVITEIUM_INGOT.get()), "20 ticks must cast");
        h.assertTrue(restored.getFluidHandler(null).isEmpty(), "Cast consumes exactly one charge");
        h.assertTrue(!restored.getItemHandler(null).extractItem(0, 1, false).isEmpty(), "Idle mold unlocks");
        h.succeed();
    }

    @GameTest(template = "empty")
    public static void blockedOutputPreservesProgress(GameTestHelper h) {
        CastingTableBlockEntity be = table(h);
        be.getFluidHandler(null).fill(new FluidStack(ModFluids.MOLTEN_LUVITEIUM.get(), 144), IFluidHandler.FluidAction.EXECUTE);
        step(be, 5);
        be.getItemHandler(null).setStackInSlot(1, ModItems.HOT_LUVITEIUM_INGOT.toStack(64));
        step(be, 40);
        h.assertTrue(be.getFluidHandler(null).getFluidAmount() == 144, "Blocked output must retain fluid");
        be.getItemHandler(null).extractItem(1, 1, false);
        step(be, 14);
        h.assertTrue(be.getItemHandler(null).getStackInSlot(1).getCount() == 63, "Preserve, not advance blocked progress");
        step(be, 1);
        h.assertTrue(be.getItemHandler(null).getStackInSlot(1).getCount() == 64, "Resume remaining 15 ticks");
        h.succeed();
    }

    @GameTest(template = "empty")
    public static void castingAutomationCannotExtractReusableMold(GameTestHelper h) {
        CastingTableBlockEntity be = table(h);
        be.getFluidHandler(null).fill(new FluidStack(ModFluids.MOLTEN_LUVITEIUM.get(), 144),
                IFluidHandler.FluidAction.EXECUTE);
        step(be, 20);

        IItemHandler automation = h.getLevel().getCapability(
                Capabilities.ItemHandler.BLOCK, h.absolutePos(POS), Direction.NORTH);
        h.assertTrue(automation != null, "Casting table exposes an automation item capability");
        h.assertTrue(automation.extractItem(0, 1, false).isEmpty(),
                "Automation must never extract the reusable mold slot");
        h.assertTrue(automation.extractItem(1, 1, false).is(ModItems.HOT_LUVITEIUM_INGOT.get()),
                "Automation can extract the completed hot output");
        h.assertTrue(be.getItemHandler(null).getStackInSlot(0).is(ModItems.INGOT_MOLD.get()),
                "Reusable mold remains installed after automated extraction");
        h.succeed();
    }

    @GameTest(template = "empty")
    public static void bucketTransfersAreAtomic(GameTestHelper h) {
        CastingTableBlockEntity be = table(h);
        var player = FakePlayerFactory.getMinecraft(h.getLevel());
        player.getAbilities().instabuild = false;
        be.getFluidHandler(null).fill(new FluidStack(ModFluids.MOLTEN_LUVITEIUM.get(), 100), IFluidHandler.FluidAction.EXECUTE);
        ItemStack full = ModItems.LUVITEIUM_BUCKET.toStack();
        h.assertFalse(be.pourBucket(player, full), "44 mB free cannot accept a 144 mB charge");
        h.assertTrue(full.getCount() == 1 && be.getFluidHandler(null).getFluidAmount() == 100, "Failed pour must change nothing");
        ItemStack empty = new ItemStack(Items.BUCKET);
        h.assertFalse(be.fillBucket(player, empty), "100 mB cannot fill a 144 mB charge");
        h.assertTrue(empty.getCount() == 1 && be.getFluidHandler(null).getFluidAmount() == 100, "Failed scoop must change nothing");
        be.getFluidHandler(null).drain(100, IFluidHandler.FluidAction.EXECUTE);
        h.assertTrue(be.pourBucket(player, full) && full.isEmpty(), "Successful pour consumes the charge");
        h.assertTrue(be.fillBucket(player, empty) && empty.isEmpty(), "Successful scoop consumes empty bucket");
        h.assertTrue(be.getFluidHandler(null).isEmpty(), "Round trip preserves fluid");
        h.succeed();
    }

    @GameTest(template = "empty")
    public static void gearAttributesAndRepair(GameTestHelper h) {
        ItemStack pickaxe = ModItems.LUVITEIUM_PICKAXE.toStack();
        h.assertTrue(pickaxe.getMaxDamage() == 1250, "Pickaxe durability");
        h.assertTrue(pickaxe.is(net.minecraft.tags.ItemTags.MINING_ENCHANTABLE), "Tool receives mining enchantments");
        double[] damage = {1};
        pickaxe.forEachModifier(EquipmentSlot.MAINHAND, (attribute, modifier) -> {
            if (attribute.equals(Attributes.ATTACK_DAMAGE)) damage[0] += modifier.amount();
        });
        h.assertTrue(damage[0] == 4.5, "Pickaxe must deal 4.5 total damage, got " + damage[0]);
        for (var armor : java.util.List.of(ModItems.LUVITEIUM_HELMET, ModItems.LUVITEIUM_CHESTPLATE,
                ModItems.LUVITEIUM_LEGGINGS, ModItems.LUVITEIUM_BOOTS)) {
            ItemStack stack = armor.toStack();
            h.assertTrue(stack.isDamageableItem() && stack.getMaxStackSize() == 1, "Armor must be damageable and unstackable");
            h.assertTrue(stack.is(net.minecraft.tags.ItemTags.ARMOR_ENCHANTABLE), "Armor receives armor enchantments");
            h.assertTrue(stack.getItem().isValidRepairItem(stack, ModItems.LUVITEIUM_INGOT.toStack()), "Ingot repairs armor");
        }
        h.assertTrue(pickaxe.getItem().isValidRepairItem(pickaxe, ModItems.LUVITEIUM_INGOT.toStack()), "Ingot repairs tools");
        h.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 1100)
    public static void generatorFullStackAndNetwork(GameTestHelper h) {
        h.setBlock(POS, ModBlocks.BIOMASS_GENERATOR.get());
        h.setBlock(POS.north(), AllBlocks.SHAFT.getDefaultState().setValue(BlockStateProperties.AXIS, Direction.Axis.Z));
        BiomassGeneratorBlockEntity generator = h.getBlockEntity(POS);
        IItemHandler inventory = h.getLevel().getCapability(Capabilities.ItemHandler.BLOCK, h.absolutePos(POS), Direction.UP);
        h.assertTrue(inventory != null && inventory.getSlots() == 1 && inventory.getSlotLimit(0) == 64, "Single capability slot");
        h.assertTrue(inventory.insertItem(0, new ItemStack(Items.COAL), false).getCount() == 1, "Reject coal");
        h.assertTrue(inventory.insertItem(0, new ItemStack(Items.OAK_LEAVES, 64), false).isEmpty(), "Accept biomass");
        h.runAfterDelay(5, () -> {
            h.assertTrue(generator.getGeneratedSpeed() == 64, "64 RPM source");
            h.assertTrue(generator.getOrCreateNetwork().calculateCapacity() == 512, "512 SU total, not 32768");
            KineticBlockEntity shaft = h.getBlockEntity(POS.north());
            h.assertTrue(Math.abs(shaft.getSpeed()) == 64, "Shaft receives rotation");
        });
        h.runAfterDelay(950, () -> h.assertTrue(generator.getGeneratedSpeed() == 64, "Full stack must still burn before 960 ticks"));
        h.runAfterDelay(965, () -> {
            h.assertTrue(generator.getGeneratedSpeed() == 0 && inventory.getStackInSlot(0).isEmpty(), "Stop after 960 fuel ticks");
            KineticBlockEntity shaft = h.getBlockEntity(POS.north());
            h.assertTrue(shaft.getSpeed() == 0, "Shaft stops after fuel exhaustion");
            inventory.insertItem(0, new ItemStack(Items.OAK_LOG), false);
        });
        h.runAfterDelay(970, () -> {
            h.assertTrue(generator.getGeneratedSpeed() == 64, "Automatically restart");
            h.succeed();
        });
    }

    @GameTest(template = "empty", timeoutTicks = 80)
    public static void chuteInsertionAndGeneratorDrops(GameTestHelper h) {
        h.setBlock(POS, ModBlocks.BIOMASS_GENERATOR.get());
        h.setBlock(POS.above(), AllBlocks.CHUTE.getDefaultState());
        IItemHandler chute = h.getLevel().getCapability(Capabilities.ItemHandler.BLOCK, h.absolutePos(POS.above()), Direction.UP);
        h.assertTrue(chute != null, "Chute exposes insertion");
        chute.insertItem(0, new ItemStack(Items.OAK_LOG, 4), false);
        h.runAfterDelay(40, () -> {
            BiomassGeneratorBlockEntity generator = h.getBlockEntity(POS);
            h.assertTrue(generator.getGeneratedSpeed() == 64, "Chute feeds generator");
            int remaining = generator.getItemHandler(null).getStackInSlot(0).getCount();
            h.assertTrue(remaining == 3, "One log consumed, three queued");
            h.setBlock(POS, Blocks.AIR);
            h.assertItemEntityCountIs(Items.OAK_LOG, POS, 2, remaining);
            h.succeed();
        });
    }
}
