package com.create.luviteium.content;

import com.create.luviteium.registry.ModBlockEntities;
import com.create.luviteium.registry.ModFluids;
import com.create.luviteium.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.ItemStackHandler;

public class CastingTableBlockEntity extends BlockEntity {
    public static final int CAPACITY = 144;
    public static final int TICKS_PER_CAST = 20;
    private static final int MOLD_SLOT = 0;
    private static final int OUTPUT_SLOT = 1;

    private final FluidTank fluidTank = new FluidTank(CAPACITY,
            stack -> stack.getFluid() == ModFluids.MOLTEN_LUVITEIUM.get()) {
        @Override
        protected void onContentsChanged() {
            setChanged();
        }
    };

    private final ItemStackHandler inventory = new ItemStackHandler(2) {
        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return slot == MOLD_SLOT && stack.is(ModItems.INGOT_MOLD.get());
        }

        @Override
        protected int getStackLimit(int slot, ItemStack stack) {
            return slot == MOLD_SLOT ? 1 : super.getStackLimit(slot, stack);
        }

        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };

    private int progress;

    public CastingTableBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CASTING_TABLE.get(), pos, state);
    }

    public static void serverTick(net.minecraft.world.level.Level level, BlockPos pos, BlockState state, CastingTableBlockEntity table) {
        if (table.canCastIngot()) {
            table.progress++;
            if (table.progress >= TICKS_PER_CAST) {
                table.completeIngotCast();
            }
        } else {
            table.progress = 0;
        }
    }

    private boolean canCastIngot() {
        return inventory.getStackInSlot(MOLD_SLOT).is(ModItems.INGOT_MOLD.get())
                && fluidTank.getFluidAmount() >= CAPACITY
                && (inventory.getStackInSlot(OUTPUT_SLOT).isEmpty()
                || inventory.getStackInSlot(OUTPUT_SLOT).is(ModItems.HOT_LUVITEIUM_INGOT.get()));
    }

    private void completeIngotCast() {
        fluidTank.drain(CAPACITY, IFluidHandler.FluidAction.EXECUTE);
        ItemStack output = inventory.getStackInSlot(OUTPUT_SLOT);
        if (output.isEmpty()) {
            inventory.setStackInSlot(OUTPUT_SLOT, new ItemStack(ModItems.HOT_LUVITEIUM_INGOT.get()));
        } else {
            output.grow(1);
        }
        progress = 0;
        setChanged();
    }

    public ItemStackHandler getItemHandler(Direction side) {
        return inventory;
    }

    public FluidTank getFluidHandler(Direction side) {
        return fluidTank;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("Inventory", inventory.serializeNBT(registries));
        tag.put("Fluid", fluidTank.writeToNBT(registries, new CompoundTag()));
        tag.putInt("Progress", progress);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        inventory.deserializeNBT(registries, tag.getCompound("Inventory"));
        fluidTank.readFromNBT(registries, tag.getCompound("Fluid"));
        progress = tag.getInt("Progress");
    }
}
