package com.displace.cavetraps.entities.rendering;

import com.displace.cavetraps.CaveTraps;
import com.displace.cavetraps.entities.FallingTrapEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class FallingTrapER extends EntityRenderer<FallingTrapEntity, FallingTrapEntityRenderState> {

    public FallingTrapER(EntityRendererProvider.Context context) {
        super(context);
//        CaveTraps.LOGGER.info("[FallingTrapER] constructed");
    }

    @Override
    public FallingTrapEntityRenderState createRenderState() {
//        CaveTraps.LOGGER.info("[FallingTrapER] createRenderState()");
        return new FallingTrapEntityRenderState();

    }

    @Override
    public void extractRenderState(FallingTrapEntity entity, FallingTrapEntityRenderState reusedState, float partialTick) {
        // rendering is failing here.


        CaveTraps.LOGGER.info("[FallingTrapER] extractRenderState camo={}", entity.getCamoState());

        super.extractRenderState(entity, reusedState, partialTick);
//        reusedState.camoState = entity.getCamoState();
        reusedState.camoState = Blocks.SAND.defaultBlockState();
        reusedState.customLight = reusedState.lightCoords;
    }

    @Override
    public void submit(FallingTrapEntityRenderState renderState, PoseStack poseStack, SubmitNodeCollector nodeCollector, CameraRenderState cameraRenderState) {
        BlockState camo = renderState.camoState;
        if (camo == null) {
            return;
        }

        poseStack.pushPose();
        int overlay = OverlayTexture.NO_OVERLAY;
        int light = renderState.customLight;
        nodeCollector.submitBlock(poseStack, camo, light, overlay, 0);
        poseStack.popPose();
    }
}
