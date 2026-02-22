package com.displace.cavetraps.blockentities.rendering;

import com.displace.cavetraps.blockentities.FallingTrapBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.model.data.ModelData;
import org.jspecify.annotations.Nullable;

import java.util.Random;

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
        BlockState camo = blockEntity.getCamoState();
        renderState.camoState = (camo != null) ? camo : Blocks.AIR.defaultBlockState();
        if (blockEntity.getLevel() != null) {
            BlockPos lightPos = blockEntity.getBlockPos().above();
            renderState.customLight = LevelRenderer.getLightColor(blockEntity.getLevel(), lightPos);
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
