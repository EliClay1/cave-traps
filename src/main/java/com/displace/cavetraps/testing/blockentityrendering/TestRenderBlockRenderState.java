package com.displace.cavetraps.testing.blockentityrendering;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TestRenderBlockRenderState extends BlockEntityRenderState {
    public int value;
}
