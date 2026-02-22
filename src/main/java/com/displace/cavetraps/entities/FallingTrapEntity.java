package com.displace.cavetraps.entities;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class FallingTrapEntity extends FallingBlockEntity {
    private BlockState camoState = Blocks.AIR.defaultBlockState();

    public FallingTrapEntity(EntityType<? extends FallingBlockEntity> entityType, Level level) {
        super(entityType, level);
    }

    public static FallingTrapEntity spawn(EntityType<? extends FallingBlockEntity> entityType, Level level, BlockPos pos, BlockState originalState, BlockState camoState) {
        FallingTrapEntity trapEntity = new FallingTrapEntity(entityType, level);

        // gets the coordinates of where the block should be created.
        double x = pos.getX() + 0.5D;
        double y = pos.getY();
        double z = pos.getZ() + 0.5D;
        trapEntity.setPos(x, y, z);
        trapEntity.xo = x;
        trapEntity.yo = y;
        trapEntity.zo = z;
        trapEntity.setStartPos(pos);

        // supposedly this is how it works with normal falling blocks...
        level.setBlock(pos, originalState.getFluidState().createLegacyBlock(), 3);

        trapEntity.setCamoState(camoState);
        level.addFreshEntity(trapEntity);
        return trapEntity;
    }

    public BlockState getCamoState() {
        return camoState;
    }

    public void setCamoState(BlockState camoState) {
        this.camoState = camoState;
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.store("camo_state", BlockState.CODEC, this.camoState);
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        input.read("camo_state", BlockState.CODEC).ifPresent(blockState -> this.camoState = blockState);
    }
}
