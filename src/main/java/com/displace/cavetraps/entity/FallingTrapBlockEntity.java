package com.displace.cavetraps.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class FallingTrapBlockEntity extends BlockEntity {

    private BlockState camoState = Blocks.AIR.defaultBlockState();

    public FallingTrapBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.FALLING_TRAP_BE.get(), pos, blockState);
    }

    public BlockState getCamoState() {
        return camoState;
    }

    public void setCamoState(BlockState newCamoState) {
        this.camoState = newCamoState;
        setChanged();
        if (level != null) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }
}
