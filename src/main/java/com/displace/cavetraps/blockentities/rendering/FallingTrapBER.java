package com.displace.cavetraps.blockentities.rendering;

import com.displace.cavetraps.block.FallingTrapBlock;
import com.displace.cavetraps.blockentities.FallingTrapBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class FallingTrapBER implements BlockEntityRenderer<FallingTrapBlockEntity, FallingTrapRenderState> {

    public FallingTrapBER(BlockEntityRendererProvider.Context context) {
        // to be used for later stuff.
    }

    @Override
    public FallingTrapRenderState createRenderState() {
        return new FallingTrapRenderState();
    }

    @Override
    public void extractRenderState(FallingTrapBlockEntity blockEntity, FallingTrapRenderState renderState, float partialTick, Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, renderState, partialTick, cameraPosition, breakProgress);
        BlockState blockState = blockEntity.getBlockState();

        // Only pull the camo state if the block is currently camouflaged
        if (blockState.hasProperty(FallingTrapBlock.HAS_CAMO) && blockState.getValue(FallingTrapBlock.HAS_CAMO)) {
            BlockState camo = blockEntity.getCamoState();
            renderState.camoState = camo != null ? camo : Blocks.AIR.defaultBlockState();
        } else {
            renderState.camoState = Blocks.AIR.defaultBlockState();
        }

        if (blockEntity.getLevel() != null) {
            renderState.customLight = LevelRenderer.getLightColor(blockEntity.getLevel(), blockEntity.getBlockPos());
        } else {
            renderState.customLight = renderState.lightCoords;
        }
    }

    @Override
    public void submit(FallingTrapRenderState fallingTrapRenderState, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState cameraRenderState) {
        BlockState camoState = fallingTrapRenderState.camoState;
        if (camoState == null) {
            return;
        }

        poseStack.pushPose();

        // replace the texture of the current block.
        int overlay = OverlayTexture.NO_OVERLAY;
        submitNodeCollector.submitBlock(poseStack, camoState, fallingTrapRenderState.customLight, overlay, 0);

        // the pose stack MUST be empty by the time this function ends.
        poseStack.popPose();
    }
}
