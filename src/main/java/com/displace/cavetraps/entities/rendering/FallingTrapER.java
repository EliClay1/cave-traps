package com.displace.cavetraps.entities.rendering;

import com.displace.cavetraps.entities.FallingTrapEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;

public class FallingTrapER extends EntityRenderer<FallingTrapEntity, FallingTrapEntityRenderState> {

    public FallingTrapER(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public FallingTrapEntityRenderState createRenderState() {
        return new FallingTrapEntityRenderState();
    }

    @Override
    public void extractRenderState(FallingTrapEntity entity, FallingTrapEntityRenderState reusedState, float partialTick) {
        super.extractRenderState(entity, reusedState, partialTick);
        reusedState.camoState = entity.getCamoState();
    }

    @Override
    public void submit(FallingTrapEntityRenderState renderState, PoseStack poseStack, SubmitNodeCollector nodeCollector, CameraRenderState cameraRenderState) {
        BlockState camo = renderState.camoState;
        if (camo == null) {
            return;
        }

        poseStack.pushPose();
        int overlay = OverlayTexture.NO_OVERLAY;
        nodeCollector.submitBlock(poseStack, camo, renderState.customLight, overlay, 0);
        poseStack.popPose();
    }
}
