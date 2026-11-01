package com.create.luviteium.client;

import com.create.luviteium.content.BiomassGeneratorBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.DirectionalKineticBlock;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import net.createmod.catnip.render.CachedBuffers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;

/** Create's own shaft ends, driven by synchronized kinetic speed (including zero). */
public final class BiomassGeneratorRenderer extends KineticBlockEntityRenderer<BiomassGeneratorBlockEntity> {
    public BiomassGeneratorRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void renderSafe(BiomassGeneratorBlockEntity be, float partialTicks, PoseStack pose,
                              MultiBufferSource buffers, int light, int overlay) {
        Direction facing = be.getBlockState().getValue(DirectionalKineticBlock.FACING);
        for (Direction end : new Direction[]{facing, facing.getOpposite()}) {
            pose.pushPose();
            // Expose four pixels beyond the casing, clearing the two-pixel socket collar.
            pose.translate(end.getStepX() * 0.25, end.getStepY() * 0.25, end.getStepZ() * 0.25);
            var shaft = CachedBuffers.partialFacing(AllPartialModels.SHAFT_HALF, be.getBlockState(), end);
            renderRotatingBuffer(be, shaft, pose, buffers.getBuffer(RenderType.solid()), light);
            pose.popPose();

            // Small shaft-mounted drive wheel. It follows actual network speed, including stalls.
            pose.pushPose();
            pose.translate(0.5 + end.getStepX() * 0.64, 0.5 + end.getStepY() * 0.64,
                    0.5 + end.getStepZ() * 0.64);
            pose.scale(0.48f, 0.48f, 0.48f);
            pose.translate(-0.5, -0.5, -0.5);
            var wheel = CachedBuffers.partialFacingVertical(AllPartialModels.SHAFTLESS_COGWHEEL,
                    be.getBlockState(), end);
            renderRotatingBuffer(be, wheel, pose, buffers.getBuffer(RenderType.solid()), light);
            pose.popPose();
        }
    }
}
