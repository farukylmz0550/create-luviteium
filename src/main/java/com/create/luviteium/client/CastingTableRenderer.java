package com.create.luviteium.client;

import com.create.luviteium.content.CastingTableBlockEntity;
import com.create.luviteium.registry.ModFluids;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import net.createmod.catnip.platform.NeoForgeCatnipServices;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;

/** Displays synchronized inventory/fluid; animation never creates a gameplay item. */
public final class CastingTableRenderer extends SafeBlockEntityRenderer<CastingTableBlockEntity> {
    public CastingTableRenderer(BlockEntityRendererProvider.Context context) {}

    @Override
    protected void renderSafe(CastingTableBlockEntity table, float partialTicks, PoseStack pose,
                              MultiBufferSource buffers, int light, int overlay) {
        float progress = table.getVisualProgress(partialTicks);
        float shake = table.isCasting() ? (float) Math.sin(progress * Math.PI * 8) * 0.008f : 0;
        pose.pushPose();
        pose.translate(shake, 0, 0);
        renderItem(table, table.getDisplayedMold(), 1.025f, 0.7f, pose, buffers, light, overlay);
        pose.popPose();
        renderMoldIndicators(table, pose, buffers, light, overlay);

        float fill = table.getVisualFill(partialTicks);
        if (fill > 0.01f) {
            NeoForgeCatnipServices.FLUID_RENDERER.renderFluidBox(
                    new FluidStack(ModFluids.MOLTEN_LUVITEIUM.get(), 144),
                    0.27f, 1.04f, 0.27f, 0.73f, 1.04f + fill * 0.035f, 0.27f + fill * 0.46f,
                    buffers, pose, light, false, true);
        }
        renderItem(table, table.getDisplayedOutput(), 1.10f + table.getEjectionLift(partialTicks),
                0.6f, pose, buffers, light, overlay);
    }

    private static void renderMoldIndicators(CastingTableBlockEntity table, PoseStack pose,
                                            MultiBufferSource buffers, int light, int overlay) {
        ItemStack mold = table.getDisplayedMold();
        if (mold.isEmpty()) return;
        // The actual installed mold remains readable even when molten metal covers the bed.
        // All four sides are labeled so pipes or adjacent machinery can obscure one side.
        for (Direction side : Direction.Plane.HORIZONTAL) {
            pose.pushPose();
            pose.translate(0.5 + side.getStepX() * 0.52, 0.58, 0.5 + side.getStepZ() * 0.52);
            pose.mulPose(Axis.YP.rotationDegrees(side.toYRot()));
            pose.scale(0.62f, 0.62f, 0.18f);
            Minecraft.getInstance().getItemRenderer().renderStatic(mold, ItemDisplayContext.FIXED,
                    light, overlay, pose, buffers, table.getLevel(), 0);
            pose.popPose();
        }
    }

    private static void renderItem(CastingTableBlockEntity table, ItemStack stack, float height, float scale,
                                   PoseStack pose, MultiBufferSource buffers, int light, int overlay) {
        if (stack.isEmpty()) return;
        pose.pushPose();
        pose.translate(0.5, height, 0.5);
        pose.mulPose(Axis.XP.rotationDegrees(90));
        pose.scale(scale, scale, scale);
        Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemDisplayContext.FIXED,
                light, overlay, pose, buffers, table.getLevel(), 0);
        pose.popPose();
    }
}
