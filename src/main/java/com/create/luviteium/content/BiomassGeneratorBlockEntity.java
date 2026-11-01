package com.create.luviteium.content;

import com.create.luviteium.registry.ModBlockEntities;
import com.simibubi.create.content.kinetics.base.GeneratingKineticBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
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
        public int getSlotLimit(int slot) {
            return 64;
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
        boolean wasBurning = burnTime > 0;
        if (wasBurning) burnTime--;
        if (burnTime == 0) {
            ItemStack fuel = inventory.getStackInSlot(0);
            if (!fuel.isEmpty() && isValidFuel(fuel)) {
                totalBurnTime = burnTimeFor(fuel);
                burnTime = totalBurnTime;
                inventory.extractItem(0, 1, false);
            }
        }
        // Refuelling must not detach and reattach a live kinetic network.
        if (wasBurning != (burnTime > 0)) {
            updateGeneratedRotation();
            sendData();
        }
        if (wasBurning || burnTime > 0) setChanged();
    }

    @Override
    public float getGeneratedSpeed() {
        return burnTime > 0 ? GENERATED_RPM : 0;
    }

    public boolean isBurning() {
        return burnTime > 0;
    }

    @Override
    public float calculateAddedStressCapacity() {
        // Create multiplies this per-RPM value by the generated speed.
        lastCapacityProvided = (float) STRESS_CAPACITY / GENERATED_RPM;
        return lastCapacityProvided;
    }

    @Override
    public float calculateStressApplied() {
        lastStressApplied = 0;
        return 0;
    }

    @Override
    public void destroy() {
        if (level != null && !level.isClientSide) {
            ItemStack remaining = inventory.extractItem(0, 64, false);
            net.minecraft.world.Containers.dropItemStack(level, worldPosition.getX() + 0.5,
                    worldPosition.getY() + 0.5, worldPosition.getZ() + 0.5, remaining);
        }
        super.destroy();
    }

    public ItemStackHandler getItemHandler(Direction side) {
        return inventory;
    }

    /**
     * Fuel governance is purely data-driven through existing vanilla item tags:
     * the whitelist and burn values below are the only fuel source of truth, so
     * other mods can opt into the generator by matching these tags, and new
     * vanilla entries are picked up automatically.
     */
    private static final List<TagKey<Item>> FUEL_TAGS = List.of(
            ItemTags.LOGS_THAT_BURN,        // 150 ticks: logs, wood, and stripped variants
            ItemTags.PLANKS,                // 38 ticks
            ItemTags.SAPLINGS,              // 75 ticks
            ItemTags.LEAVES,                // 15 ticks
            ItemTags.FLOWERS,               // 15 ticks
            ItemTags.VILLAGER_PLANTABLE_SEEDS); // 15 ticks: seeds, carrots, potatoes

    private static final int[] FUEL_TICKS = {150, 38, 75, 15, 15, 15};

    public static boolean isValidFuel(ItemStack stack) {
        return !stack.isEmpty() && burnTimeFor(stack) > 0;
    }

    private static int burnTimeFor(ItemStack stack) {
        for (int i = 0; i < FUEL_TAGS.size(); i++) {
            if (stack.is(FUEL_TAGS.get(i))) {
                return FUEL_TICKS[i];
            }
        }
        return 0;
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
