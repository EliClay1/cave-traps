package com.displace.cavetraps.client.renderer;

import com.displace.cavetraps.client.renderer.state.FallingTrapRenderState;
import com.displace.cavetraps.entity.FallingTrapBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.entity.state.FallingBlockRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class FallingTrapBlockRenderer implements BlockEntityRenderer<FallingTrapBlockEntity, FallingTrapRenderState> {

    public FallingTrapBlockRenderer(BlockEntityRendererProvider.Context context) {

    }

    @Override
    public FallingTrapRenderState createRenderState() {
        return new FallingTrapRenderState();
    }

    @Override
    public void extractRenderState(FallingTrapBlockEntity blockEntity, FallingTrapRenderState renderState, float partialTick, Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, renderState, partialTick, cameraPosition, breakProgress);
        BlockState camo = blockEntity.getCamoState();
        renderState.pos = blockEntity.getBlockPos();
        renderState.level = blockEntity.getLevel();
        renderState.camoState = (camo != null) ? camo : Blocks.AIR.defaultBlockState();
    }

    @Override
    public void submit(FallingTrapRenderState fallingTrapRenderState, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState cameraRenderState) {
        BlockState stateToRender = fallingTrapRenderState.camoState;
        if (stateToRender == null || stateToRender.isAir()) {
            return;
        }
        // currently a deprecated system. This needs to be updated.
        poseStack.pushPose();
        try {
            var buffers = Minecraft.getInstance().renderBuffers().bufferSource();
            Minecraft.getInstance().getBlockRenderer().renderSingleBlock(
                    stateToRender,
                    poseStack,
                    buffers,
                    15728880,
                    0
            );
        } catch (Exception ignored) {
        }
        poseStack.popPose();
    }
}
