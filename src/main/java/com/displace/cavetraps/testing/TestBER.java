package com.displace.cavetraps.testing;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class TestBER implements BlockEntityRenderer<TestBlockEntity, TestRenderState> {
    // yes, this is getting confusing

    private final ItemModelResolver itemModelResolver;

    public TestBER(BlockEntityRendererProvider.Context context) {
        this.itemModelResolver = context.itemModelResolver();
    }

    @Override
    public TestRenderState createRenderState() {
        return new TestRenderState();
    }

    @Override
    public void extractRenderState(TestBlockEntity blockEntity, TestRenderState renderState, float partialTick,
                                   Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, renderState, partialTick, cameraPosition, breakProgress);
        renderState.angle = blockEntity.getAngle();
        ItemStack stack = blockEntity.getStack();

        if (!stack.isEmpty() && blockEntity.getLevel() != null) {
            int seed = (int) blockEntity.getBlockPos().asLong();
            this.itemModelResolver.updateForTopItem(renderState.item, stack, ItemDisplayContext.FIXED,
                    blockEntity.getLevel(), null, seed);

            BlockPos lightPos = blockEntity.getBlockPos().above();
            renderState.customLight = LevelRenderer.getLightColor(blockEntity.getLevel(), lightPos);
        } else {
            renderState.item.clear();
            renderState.customLight = renderState.lightCoords;
        }
    }

    @Override
    public void submit(TestRenderState renderState, PoseStack poseStack, SubmitNodeCollector submitNodeCollector,
                       CameraRenderState cameraRenderState) {

        if (renderState.item.isEmpty()) {
            return;
        }

        poseStack.pushPose();
        // Move to block center and up a bit
        poseStack.translate(0.5F, 1.9F, 0.5F);
        // Spin around Y axis
        poseStack.mulPose(Axis.YP.rotationDegrees(renderState.angle));
        // Lay flat, like on the campfire[web:2]
//        poseStack.mulPose(Axis.XP.rotationDegrees());
        // Offset and scale down the item a bit
        poseStack.translate(-0.1875F, -0.1875F, 0.0F);
        poseStack.scale(0.5F, 0.5F, 0.5F);

        // Submit the item’s baked model using its render state
        renderState.item.submit(
                poseStack,
                submitNodeCollector,
                renderState.customLight,           // filled by super.extractRenderState
                OverlayTexture.NO_OVERLAY,
                0                            // layer index; 0 is fine for simple items
        );

        poseStack.popPose();
    }

}
