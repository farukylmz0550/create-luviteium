package com.create.luviteium.content;

import com.create.luviteium.registry.ModBlockEntities;
import com.simibubi.create.content.kinetics.base.GeneratingKineticBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;

import java.util.List;

public class BiomassGeneratorBlockEntity extends GeneratingKineticBlockEntity {
    public static final int GENERATED_RPM = 64;
    public static final int STRESS_CAPACITY = 512;

    private int burnTime;
    private int totalBurnTime;
    private final ItemStackHandler inventory = new ItemStackHandler(1) {
        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return isValidFuel(stack);
        }

        @Override
        protected int getStackLimit(int slot, ItemStack stack) {
            return 64;
        }

        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };

    public BiomassGeneratorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.BIOMASS_GENERATOR.get(), pos, state);
    }

    @Override
    public void tick() {
        super.tick();
        if (level == null || level.isClientSide) return;
        if (burnTime > 0) {
            burnTime--;
            if (burnTime == 0) updateGeneratedRotation();
        }
        if (burnTime == 0) {
            ItemStack fuel = inventory.getStackInSlot(0);
            if (!fuel.isEmpty() && isValidFuel(fuel)) {
                totalBurnTime = burnTimeFor(fuel);
                burnTime = totalBurnTime;
                fuel.shrink(1);
                setChanged();
                updateGeneratedRotation();
            }
        }
    }

    @Override
    public float getGeneratedSpeed() {
        return burnTime > 0 ? GENERATED_RPM : 0;
    }

    @Override
    public float calculateAddedStressCapacity() {
        return STRESS_CAPACITY;
    }

    public ItemStackHandler getItemHandler(Direction side) {
        return inventory;
    }

    public static boolean isValidFuel(ItemStack stack) {
        return !stack.isEmpty() && (stack.is(ItemTags.LOGS) || stack.is(ItemTags.PLANKS)
                || stack.is(ItemTags.LEAVES) || stack.is(ItemTags.SAPLINGS)
                || stack.is(ItemTags.FLOWERS) || stack.is(Items.STICK)
                || stack.is(Items.WHEAT_SEEDS) || stack.is(Items.BEETROOT_SEEDS)
                || stack.is(Items.MELON_SEEDS) || stack.is(Items.PUMPKIN_SEEDS)
                || stack.is(Items.WHEAT) || stack.is(Items.BEETROOT)
                || stack.is(Items.CARROT) || stack.is(Items.POTATO)
                || stack.is(Items.CACTUS) || stack.is(Items.BAMBOO)
                || stack.is(Items.SUGAR_CANE) || stack.is(Items.VINE)
                || stack.is(Items.MOSS_BLOCK) || stack.is(Items.DRIED_KELP_BLOCK)
                || stack.is(Items.HAY_BLOCK));
    }

    private static int burnTimeFor(ItemStack stack) {
        if (stack.is(Items.STICK)) return 19;
        if (stack.is(ItemTags.LEAVES) || stack.is(ItemTags.FLOWERS)
                || stack.is(Items.MOSS_BLOCK) || stack.is(Items.VINE)) return 15;
        if (stack.is(ItemTags.SAPLINGS)) return 75;
        if (stack.is(Items.DRIED_KELP_BLOCK)) return 300;
        if (stack.is(Items.HAY_BLOCK)) return 270;
        if (stack.is(ItemTags.PLANKS) || stack.is(Items.CACTUS) || stack.is(Items.BAMBOO)) return 38;
        if (stack.is(Items.WHEAT_SEEDS) || stack.is(Items.BEETROOT_SEEDS)
                || stack.is(Items.MELON_SEEDS) || stack.is(Items.PUMPKIN_SEEDS)) return 15;
        if (stack.is(Items.WHEAT) || stack.is(Items.BEETROOT) || stack.is(Items.CARROT)
                || stack.is(Items.POTATO) || stack.is(Items.SUGAR_CANE)) return 30;
        if (stack.is(ItemTags.LOGS)) return 150;
        return 15;
    }

    @Override
    protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(tag, registries, clientPacket);
        tag.put("Inventory", inventory.serializeNBT(registries));
        tag.putInt("BurnTime", burnTime);
        tag.putInt("TotalBurnTime", totalBurnTime);
    }

    @Override
    protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(tag, registries, clientPacket);
        inventory.deserializeNBT(registries, tag.getCompound("Inventory"));
        burnTime = tag.getInt("BurnTime");
        totalBurnTime = tag.getInt("TotalBurnTime");
    }

    @Override
    public void addBehaviours(List<com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour> behaviours) {
    }
}
