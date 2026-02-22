package com.displace.cavetraps.testing;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class TestRenderState extends BlockEntityRenderState {
    public float angle;
    public ItemStackRenderState item = new ItemStackRenderState();
    public int customLight;
}
