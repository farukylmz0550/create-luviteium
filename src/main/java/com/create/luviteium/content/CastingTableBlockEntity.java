package com.create.luviteium.content;

import com.create.luviteium.registry.ModBlockEntities;
import com.create.luviteium.registry.ModFluids;
import com.create.luviteium.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.IItemHandler;

public class CastingTableBlockEntity extends BlockEntity {
    public static final int INGOT_AMOUNT = 144;
    public static final int PICKAXE_HEAD_AMOUNT = 288;
    private static final int MOLD_SLOT = 0;
    private static final int OUTPUT_SLOT = 1;

    private final FluidTank fluidTank = new FluidTank(0,
            stack -> stack.getFluid() == ModFluids.MOLTEN_LUVITEIUM.get()) {
        @Override
        protected void onContentsChanged() {
            markVisualChange();
        }
    };

    private final ItemStackHandler inventory = new ItemStackHandler(2) {
        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return slot == MOLD_SLOT && isSupportedMold(stack);
        }

        @Override
        protected int getStackLimit(int slot, ItemStack stack) {
            return slot == MOLD_SLOT ? 1 : super.getStackLimit(slot, stack);
        }

        @Override
        protected void onContentsChanged(int slot) {
            if (slot == MOLD_SLOT) {
                fluidTank.setCapacity(requiredAmount());
            }
            markVisualChange();
        }

        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            if (slot == MOLD_SLOT && (!fluidTank.isEmpty() || progress > 0)) {
                return ItemStack.EMPTY;
            }
            return super.extractItem(slot, amount, simulate);
        }
    };

    /**
     * Automation may insert into either slot, but a reusable mold is never an
     * automation output. Without this view, a Funnel can extract slot 0 in the
     * same tick that a cast completes and make the mold appear to drop/dupe.
     */
    private final IItemHandler automationInventory = new IItemHandler() {
        @Override
        public int getSlots() {
            return inventory.getSlots();
        }

        @Override
        public ItemStack getStackInSlot(int slot) {
            return inventory.getStackInSlot(slot);
        }

        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            return inventory.insertItem(slot, stack, simulate);
        }

        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            return slot == MOLD_SLOT ? ItemStack.EMPTY : inventory.extractItem(slot, amount, simulate);
        }

        @Override
        public int getSlotLimit(int slot) {
            return inventory.getSlotLimit(slot);
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return inventory.isItemValid(slot, stack);
        }
    };

    private int progress;
    private boolean visualSyncDirty;
    private float visualFill, previousVisualFill;
    private float visualProgress, previousVisualProgress;
    private int ejectTicks;
    private ItemStack recentCast = ItemStack.EMPTY;

    public CastingTableBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CASTING_TABLE.get(), pos, state);
    }

    public static void serverTick(net.minecraft.world.level.Level level, BlockPos pos, BlockState state, CastingTableBlockEntity table) {
        if (table.canCastIngot()) {
            table.progress++;
            table.setChanged();
            if (table.progress % 4 == 0) table.visualSyncDirty = true;
            if (table.progress >= table.requiredTicks()) {
                table.completeIngotCast();
            }
        }
        if (table.visualSyncDirty && level != null && !level.isClientSide) {
            table.visualSyncDirty = false;
            level.sendBlockUpdated(pos, state, state, net.minecraft.world.level.block.Block.UPDATE_CLIENTS);
        }
    }

    private void markVisualChange() {
        setChanged();
        visualSyncDirty = true;
    }

    public static void clientTick(Level level, BlockPos pos, BlockState state, CastingTableBlockEntity table) {
        table.previousVisualFill = table.visualFill;
        table.previousVisualProgress = table.visualProgress;
        table.visualFill += (table.getFillRatio() - table.visualFill) * 0.35f;
        table.visualProgress += (table.getProgressRatio() - table.visualProgress) * 0.35f;
        if (table.ejectTicks > 0 && --table.ejectTicks == 0) table.recentCast = ItemStack.EMPTY;
    }

    public float getFillRatio() {
        return fluidTank.getCapacity() == 0 ? 0 : (float) fluidTank.getFluidAmount() / fluidTank.getCapacity();
    }

    public float getProgressRatio() {
        return requiredTicks() == 0 ? 0 : (float) progress / requiredTicks();
    }

    public boolean isCasting() {
        return canCastIngot();
    }

    public float getVisualFill(float partialTicks) {
        return net.minecraft.util.Mth.lerp(partialTicks, previousVisualFill, visualFill);
    }

    public float getVisualProgress(float partialTicks) {
        return net.minecraft.util.Mth.lerp(partialTicks, previousVisualProgress, visualProgress);
    }

    public float getEjectionLift(float partialTicks) {
        if (ejectTicks == 0) return 0;
        float time = (12 - ejectTicks + partialTicks) / 12f;
        return net.minecraft.util.Mth.sin(time * net.minecraft.util.Mth.PI) * 0.18f;
    }

    public ItemStack getDisplayedMold() {
        return inventory.getStackInSlot(MOLD_SLOT).copyWithCount(1);
    }

    public ItemStack getDisplayedOutput() {
        ItemStack output = inventory.getStackInSlot(OUTPUT_SLOT);
        return (output.isEmpty() ? recentCast : output).copyWithCount(1);
    }

    @Override
    public boolean triggerEvent(int id, int parameter) {
        if (id != 1) return super.triggerEvent(id, parameter);
        if (level != null && level.isClientSide) {
            ejectTicks = 12;
            recentCast = new ItemStack(net.minecraft.world.item.Item.byId(parameter));
            for (int i = 0; i < 3; i++) {
                level.addParticle(net.minecraft.core.particles.ParticleTypes.SMOKE,
                        worldPosition.getX() + 0.35 + level.random.nextDouble() * 0.3,
                        worldPosition.getY() + 1.08, worldPosition.getZ() + 0.5,
                        0, 0.025, 0);
            }
        }
        return true;
    }

    private boolean canCastIngot() {
        ItemStack output = inventory.getStackInSlot(OUTPUT_SLOT);
        return requiredAmount() > 0
                && fluidTank.getFluidAmount() >= requiredAmount()
                && (output.isEmpty()
                || (output.is(outputItem()) && output.getCount() < output.getMaxStackSize()));
    }

    private void completeIngotCast() {
        fluidTank.drain(requiredAmount(), IFluidHandler.FluidAction.EXECUTE);
        ItemStack output = inventory.getStackInSlot(OUTPUT_SLOT);
        if (output.isEmpty()) {
            inventory.setStackInSlot(OUTPUT_SLOT, new ItemStack(outputItem()));
        } else {
            output.grow(1);
        }
        progress = 0;
        markVisualChange();
        if (level != null && !level.isClientSide) {
            level.blockEvent(worldPosition, getBlockState().getBlock(), 1,
                    net.minecraft.world.item.Item.getId(outputItem()));
        }
    }

    private int requiredAmount() {
        ItemStack mold = inventory.getStackInSlot(MOLD_SLOT);
        if (mold.is(ModItems.INGOT_MOLD.get())) {
            return INGOT_AMOUNT;
        }
        if (mold.is(ModItems.PICKAXE_HEAD_MOLD.get()) || mold.is(ModItems.AXE_HEAD_MOLD.get())
                || mold.is(ModItems.SHOVEL_HEAD_MOLD.get()) || mold.is(ModItems.HOE_HEAD_MOLD.get())
                || mold.is(ModItems.SWORD_BLADE_MOLD.get())) {
            return PICKAXE_HEAD_AMOUNT;
        }
        if (mold.is(ModItems.HELMET_MOLD.get()) || mold.is(ModItems.BOOTS_MOLD.get())) {
            return 432;
        }
        if (mold.is(ModItems.CHESTPLATE_MOLD.get())) {
            return 576;
        }
        if (mold.is(ModItems.LEGGINGS_MOLD.get())) {
            return 720;
        }
        return 0;
    }

    private int requiredTicks() {
        return requiredAmount() / INGOT_AMOUNT * 20;
    }

    private net.minecraft.world.item.Item outputItem() {
        ItemStack mold = inventory.getStackInSlot(MOLD_SLOT);
        if (mold.is(ModItems.PICKAXE_HEAD_MOLD.get())) return ModItems.HOT_LUVITEIUM_PICKAXE_HEAD.get();
        if (mold.is(ModItems.AXE_HEAD_MOLD.get())) return ModItems.HOT_LUVITEIUM_AXE_HEAD.get();
        if (mold.is(ModItems.SHOVEL_HEAD_MOLD.get())) return ModItems.HOT_LUVITEIUM_SHOVEL_HEAD.get();
        if (mold.is(ModItems.HOE_HEAD_MOLD.get())) return ModItems.HOT_LUVITEIUM_HOE_HEAD.get();
        if (mold.is(ModItems.SWORD_BLADE_MOLD.get())) return ModItems.HOT_LUVITEIUM_SWORD_BLADE.get();
        if (mold.is(ModItems.HELMET_MOLD.get())) return ModItems.HOT_LUVITEIUM_HELMET.get();
        if (mold.is(ModItems.CHESTPLATE_MOLD.get())) return ModItems.HOT_LUVITEIUM_CHESTPLATE.get();
        if (mold.is(ModItems.LEGGINGS_MOLD.get())) return ModItems.HOT_LUVITEIUM_LEGGINGS.get();
        if (mold.is(ModItems.BOOTS_MOLD.get())) return ModItems.HOT_LUVITEIUM_BOOTS.get();
        return ModItems.HOT_LUVITEIUM_INGOT.get();
    }

    private static boolean isSupportedMold(ItemStack stack) {
        return stack.is(ModItems.INGOT_MOLD.get()) || stack.is(ModItems.PICKAXE_HEAD_MOLD.get())
                || stack.is(ModItems.AXE_HEAD_MOLD.get()) || stack.is(ModItems.SHOVEL_HEAD_MOLD.get())
                || stack.is(ModItems.HOE_HEAD_MOLD.get()) || stack.is(ModItems.SWORD_BLADE_MOLD.get())
                || stack.is(ModItems.HELMET_MOLD.get()) || stack.is(ModItems.CHESTPLATE_MOLD.get())
                || stack.is(ModItems.LEGGINGS_MOLD.get()) || stack.is(ModItems.BOOTS_MOLD.get());
    }

    public ItemStackHandler getItemHandler(Direction side) {
        return inventory;
    }

    public IItemHandler getAutomationItemHandler(Direction side) {
        return automationInventory;
    }

    public FluidTank getFluidHandler(Direction side) {
        return fluidTank;
    }

    /**
     * Manual right-click interaction: insert a supported reusable mold into the empty mold slot,
     * or remove a finished cast product from the output slot into the player inventory.
     */
    public boolean interactWithPlayer(Player player, InteractionHand hand) {
        ItemStack held = player.getItemInHand(hand);
        if (held.isEmpty() && player.isShiftKeyDown()) {
            ItemStack removed = inventory.extractItem(MOLD_SLOT, 1, false);
            if (removed.isEmpty()) return false;
            if (!player.getInventory().add(removed)) player.drop(removed, false);
            return true;
        }
        ItemStack moldSlot = inventory.getStackInSlot(MOLD_SLOT);
        if (isSupportedMold(held)) {
            if (!moldSlot.isEmpty() || !fluidTank.isEmpty() || progress > 0) {
                return false;
            }
            ItemStack remaining = inventory.insertItem(MOLD_SLOT, held.copyWithCount(1), false);
            if (remaining.isEmpty()) {
                if (!player.getAbilities().instabuild) held.shrink(1);
                setChanged();
                return true;
            }
            return false;
        }
        ItemStack output = inventory.extractItem(OUTPUT_SLOT, inventory.getStackInSlot(OUTPUT_SLOT).getCount(), false);
        if (!output.isEmpty()) {
            if (!player.getInventory().add(output)) {
                player.drop(output, false);
            }
            setChanged();
            return true;
        }
        return false;
    }

    /**
     * Pour one bucket charge (one ingot's worth, 144 mB) of Molten Luviteium into the tank.
     * Returns false when the tank has no room for a full charge; the held full bucket stays.
     */
    public boolean pourBucket(Player player, ItemStack held) {
        FluidStack charge = new FluidStack(ModFluids.MOLTEN_LUVITEIUM.get(), INGOT_AMOUNT);
        if (fluidTank.fill(charge, IFluidHandler.FluidAction.SIMULATE) < INGOT_AMOUNT) {
            return false;
        }
        fluidTank.fill(charge, IFluidHandler.FluidAction.EXECUTE);
        setChanged();
        swapHeld(player, held, new ItemStack(Items.BUCKET));
        return true;
    }

    /**
     * Fill an empty bucket from the tank, one charge (144 mB) at a time.
     * Returns false when the tank holds less than a full charge.
     */
    public boolean fillBucket(Player player, ItemStack held) {
        FluidStack drained = fluidTank.drain(INGOT_AMOUNT, IFluidHandler.FluidAction.SIMULATE);
        if (drained.getAmount() < INGOT_AMOUNT) {
            return false;
        }
        fluidTank.drain(INGOT_AMOUNT, IFluidHandler.FluidAction.EXECUTE);
        setChanged();
        swapHeld(player, held, new ItemStack(ModItems.LUVITEIUM_BUCKET.get()));
        return true;
    }

    private static void swapHeld(Player player, ItemStack held, ItemStack result) {
        if (player.getAbilities().instabuild) return;
        held.shrink(1);
        if (!player.getInventory().add(result)) {
            player.drop(result, false);
        }
    }

    public void dropContents(Level level, BlockPos pos) {
        for (int slot = 0; slot < inventory.getSlots(); slot++) {
            ItemStack stack = inventory.getStackInSlot(slot);
            if (!stack.isEmpty()) {
                Containers.dropItemStack(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, stack);
            }
        }
        fluidTank.drain(Integer.MAX_VALUE, IFluidHandler.FluidAction.EXECUTE);
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
        fluidTank.setCapacity(requiredAmount());
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }

    @Override
    public net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket getUpdatePacket() {
        return net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket.create(this);
    }
}
