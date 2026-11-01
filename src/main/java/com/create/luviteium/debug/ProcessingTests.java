package com.create.luviteium.debug;

import com.create.luviteium.content.CastingTableBlockEntity;
import com.create.luviteium.registry.ModBlocks;
import com.create.luviteium.registry.ModFluids;
import com.create.luviteium.registry.ModItems;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.kinetics.fan.processing.AllFanProcessingTypes;
import com.simibubi.create.content.kinetics.mixer.MixingRecipe;
import com.simibubi.create.content.kinetics.press.PressingRecipe;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.content.processing.basin.BasinRecipe;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.content.processing.recipe.HeatCondition;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

@GameTestHolder("luviteium")
@PrefixGameTestTemplate(false)
public final class ProcessingTests {
    @GameTest(template = "empty")
    public static void heatedBasinProducesMoltenMetal(GameTestHelper h) {
        var recipe = (MixingRecipe) h.getLevel().getRecipeManager()
                .byKey(ResourceLocation.fromNamespaceAndPath("luviteium", "mixing/raw_luviteium"))
                .orElseThrow().value();
        h.assertTrue(recipe.getRequiredHeat() == HeatCondition.HEATED, "Mixing must require heat");
        BlockPos basinPos = new BlockPos(2, 2, 3);
        h.setBlock(basinPos, AllBlocks.BASIN.get());
        BasinBlockEntity basin = h.getBlockEntity(basinPos);
        basin.getInputInventory().insertItem(0, ModItems.RAW_LUVITEIUM.toStack(), false);
        h.assertFalse(BasinRecipe.match(basin, recipe), "Cold Basin cannot process raw ore");
        h.setBlock(basinPos.below(), AllBlocks.BLAZE_BURNER.getDefaultState()
                .setValue(BlazeBurnerBlock.HEAT_LEVEL, BlazeBurnerBlock.HeatLevel.KINDLED));
        basin.tick();
        h.assertTrue(BasinRecipe.match(basin, recipe), "Heated Basin matches mixing recipe");
        h.assertTrue(BasinRecipe.apply(basin, recipe), "Create's Basin processing must succeed");
        h.assertTrue(basin.getInputInventory().getStackInSlot(0).isEmpty(), "Raw ore consumed");
        var fluid = h.getLevel().getCapability(Capabilities.FluidHandler.BLOCK, h.absolutePos(basinPos), null);
        h.assertTrue(fluid != null, "Basin exposes output fluid to pipes");
        var molten = fluid.drain(1000, IFluidHandler.FluidAction.EXECUTE);
        h.assertTrue(molten.getFluid() == ModFluids.MOLTEN_LUVITEIUM.get() && molten.getAmount() == 144,
                "One raw ore must produce exactly 144 mB molten fluid, no item output");
        h.succeed();
    }

    @GameTest(template = "empty")
    public static void everyMoldCastsHotThenFanCools(GameTestHelper h) {
        Item[][] products = {
                {ModItems.INGOT_MOLD.get(), ModItems.HOT_LUVITEIUM_INGOT.get(), ModItems.LUVITEIUM_INGOT.get()},
                {ModItems.PICKAXE_HEAD_MOLD.get(), ModItems.HOT_LUVITEIUM_PICKAXE_HEAD.get(), ModItems.LUVITEIUM_PICKAXE_HEAD.get()},
                {ModItems.AXE_HEAD_MOLD.get(), ModItems.HOT_LUVITEIUM_AXE_HEAD.get(), ModItems.LUVITEIUM_AXE_HEAD.get()},
                {ModItems.SHOVEL_HEAD_MOLD.get(), ModItems.HOT_LUVITEIUM_SHOVEL_HEAD.get(), ModItems.LUVITEIUM_SHOVEL_HEAD.get()},
                {ModItems.HOE_HEAD_MOLD.get(), ModItems.HOT_LUVITEIUM_HOE_HEAD.get(), ModItems.LUVITEIUM_HOE_HEAD.get()},
                {ModItems.SWORD_BLADE_MOLD.get(), ModItems.HOT_LUVITEIUM_SWORD_BLADE.get(), ModItems.LUVITEIUM_SWORD_BLADE.get()},
                {ModItems.HELMET_MOLD.get(), ModItems.HOT_LUVITEIUM_HELMET.get(), ModItems.LUVITEIUM_HELMET.get()},
                {ModItems.CHESTPLATE_MOLD.get(), ModItems.HOT_LUVITEIUM_CHESTPLATE.get(), ModItems.LUVITEIUM_CHESTPLATE.get()},
                {ModItems.LEGGINGS_MOLD.get(), ModItems.HOT_LUVITEIUM_LEGGINGS.get(), ModItems.LUVITEIUM_LEGGINGS.get()},
                {ModItems.BOOTS_MOLD.get(), ModItems.HOT_LUVITEIUM_BOOTS.get(), ModItems.LUVITEIUM_BOOTS.get()}
        };
        int[] amounts = {144, 288, 288, 288, 288, 288, 432, 576, 720, 432};
        BlockPos pos = new BlockPos(3, 1, 3);
        h.setBlock(pos, ModBlocks.CASTING_TABLE.get());
        CastingTableBlockEntity machine = h.getBlockEntity(pos);
        for (int i = 0; i < products.length; i++) {
            Item[] product = products[i];
            var inventory = machine.getItemHandler(null);
            var tank = machine.getFluidHandler(null);
            inventory.insertItem(0, new ItemStack(product[0]), false);
            h.assertTrue(tank.getCapacity() == amounts[i], "Exact capacity for " + product[0]);
            tank.fill(new FluidStack(ModFluids.MOLTEN_LUVITEIUM.get(), amounts[i] - 1), IFluidHandler.FluidAction.EXECUTE);
            for (int tick = 0; tick < 120; tick++) step(machine);
            h.assertTrue(inventory.getStackInSlot(1).isEmpty(), "Incomplete charge must wait");
            h.assertTrue(tank.fill(new FluidStack(ModFluids.MOLTEN_LUVITEIUM.get(), 1000), IFluidHandler.FluidAction.EXECUTE) == 1,
                    "Only the missing amount is accepted");
            int duration = amounts[i] / 144 * 20;
            for (int tick = 0; tick < duration - 1; tick++) step(machine);
            h.assertTrue(inventory.getStackInSlot(1).isEmpty(), "No early hot cast");
            step(machine);
            ItemStack hot = inventory.extractItem(1, 64, false);
            h.assertTrue(hot.is(product[1]) && hot.getCount() == 1 && tank.isEmpty(), "One hot cast consumes exact charge");
            h.assertFalse(hot.getItem() instanceof ArmorItem, "Hot armor cannot be equipped");
            h.assertTrue(AllFanProcessingTypes.SPLASHING.canProcess(hot, h.getLevel()), "Fan washing accepts every hot product");
            var cooled = AllFanProcessingTypes.SPLASHING.process(hot, h.getLevel());
            h.assertTrue(cooled != null && cooled.size() == 1 && cooled.getFirst().is(product[2])
                    && cooled.getFirst().getCount() == 1, "Fan washing returns the corresponding cooled product");
            h.assertTrue(inventory.extractItem(0, 1, false).is(product[0]), "Mold is reused, not consumed");
        }
        h.succeed();
    }

    private static void step(CastingTableBlockEntity machine) {
        CastingTableBlockEntity.serverTick(machine.getLevel(), machine.getBlockPos(), machine.getBlockState(), machine);
    }

    @GameTest(template = "empty")
    public static void toolsNeedOneCooledHeadAndSticks(GameTestHelper h) {
        String[] names = {"pickaxe", "axe", "shovel", "hoe", "sword"};
        Item[] heads = {ModItems.LUVITEIUM_PICKAXE_HEAD.get(), ModItems.LUVITEIUM_AXE_HEAD.get(),
                ModItems.LUVITEIUM_SHOVEL_HEAD.get(), ModItems.LUVITEIUM_HOE_HEAD.get(), ModItems.LUVITEIUM_SWORD_BLADE.get()};
        Item[] hotHeads = {ModItems.HOT_LUVITEIUM_PICKAXE_HEAD.get(), ModItems.HOT_LUVITEIUM_AXE_HEAD.get(),
                ModItems.HOT_LUVITEIUM_SHOVEL_HEAD.get(), ModItems.HOT_LUVITEIUM_HOE_HEAD.get(), ModItems.HOT_LUVITEIUM_SWORD_BLADE.get()};
        for (int i = 0; i < names.length; i++) {
            var recipe = (net.minecraft.world.item.crafting.ShapedRecipe) h.getLevel().getRecipeManager()
                    .byKey(ResourceLocation.fromNamespaceAndPath("luviteium", "luviteium_" + names[i])).orElseThrow().value();
            var ingredients = new java.util.ArrayList<ItemStack>();
            ingredients.add(new ItemStack(heads[i]));
            ingredients.add(new ItemStack(net.minecraft.world.item.Items.STICK));
            if (i != 4) ingredients.add(new ItemStack(net.minecraft.world.item.Items.STICK));
            var input = net.minecraft.world.item.crafting.CraftingInput.of(1, ingredients.size(), ingredients);
            h.assertTrue(recipe.matches(input, h.getLevel()), "One cooled head and sticks must assemble " + names[i]);
            var output = recipe.assemble(input, h.getLevel().registryAccess());
            h.assertTrue(net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(output.getItem())
                    .equals(ResourceLocation.fromNamespaceAndPath("luviteium", "luviteium_" + names[i]))
                    && output.getCount() == 1, "Correct assembled tool");
            ingredients.set(0, new ItemStack(hotHeads[i]));
            h.assertFalse(recipe.matches(net.minecraft.world.item.crafting.CraftingInput.of(1, ingredients.size(), ingredients), h.getLevel()),
                    "Hot heads must be cooled before assembly");
        }
        h.succeed();
    }

    @GameTest(template = "empty")
    public static void mechanicalPressConvertsIngotToPanel(GameTestHelper h) {
        PressingRecipe recipe = (PressingRecipe) h.getLevel().getRecipeManager()
                .byKey(ResourceLocation.fromNamespaceAndPath("luviteium", "pressing/luviteium_panel"))
                .orElseThrow(() -> new AssertionError("Missing Luviteium panel pressing recipe"))
                .value();
        h.assertTrue(recipe.matches(new SingleRecipeInput(ModItems.LUVITEIUM_INGOT.toStack()), h.getLevel()),
                "Mechanical Press must accept a cooled Luviteium Ingot");
        var results = recipe.getRollableResultsAsItemStacks();
        h.assertTrue(results.size() == 1 && results.getFirst().is(ModItems.LUVITEIUM_PANEL.get())
                        && results.getFirst().getCount() == 1,
                "Mechanical Press must output exactly one Luviteium Panel");
        h.assertFalse(recipe.matches(new SingleRecipeInput(ModItems.HOT_LUVITEIUM_INGOT.toStack()), h.getLevel()),
                "Mechanical Press must reject a hot ingot");
        h.succeed();
    }
}
