package com.displace.cavetraps.client.renderer.state;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;

public class FallingTrapRenderState extends BlockEntityRenderState {
    public BlockState camoState;
    public BlockPos pos;
    public BlockAndTintGetter level;
}
