package com.displace.cavetraps.testing.blockentityrendering;

import com.displace.cavetraps.testing.TestRenderBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class TestRenderBER implements BlockEntityRenderer<TestRenderBlockEntity, TestRenderBlockRenderState> {
    // yes, this is getting confusing

    // creates the render state. Fairly self-explanatory.
    @Override
    public TestRenderBlockRenderState createRenderState() {
        return new TestRenderBlockRenderState();
    }

    @Override
    public void extractRenderState(TestRenderBlockEntity blockEntity, TestRenderBlockRenderState renderState, float
            partialTick, Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, renderState, partialTick, cameraPosition, breakProgress);
    }

    @Override
    public void submit(TestRenderBlockRenderState testRenderBlockRenderState, PoseStack poseStack, SubmitNodeCollector
            submitNodeCollector, CameraRenderState cameraRenderState) {

    }
}
