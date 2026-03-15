package com.displace.cavetraps.entities;

import com.displace.cavetraps.CaveTraps;
import com.displace.cavetraps.block.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.lang.reflect.Field;

public class FallingTrapEntity extends FallingBlockEntity {
    private static final EntityDataAccessor<BlockState> DATA_CAMO_STATE = SynchedEntityData.defineId(FallingTrapEntity.class, EntityDataSerializers.BLOCK_STATE);
    private static final EntityDataAccessor<Boolean> DATA_BOOLEAN_STATE = SynchedEntityData.defineId(FallingTrapEntity.class, EntityDataSerializers.BOOLEAN);

    public FallingTrapEntity(EntityType<? extends FallingBlockEntity> entityType, Level level) {
        super(entityType, level);
    }

    // assigns the default state value.
    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_CAMO_STATE, ModBlocks.FALLING_TRAP_BLOCK.get().defaultBlockState());
        builder.define(DATA_BOOLEAN_STATE, false);
    }

    public static FallingTrapEntity spawn(EntityType<? extends FallingBlockEntity> entityType, Level level, BlockPos pos, BlockState originalState, BlockState camoState, CompoundTag customData) {
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

        if (customData != null && !customData.isEmpty()) {
            trapEntity.blockData = customData;
        }

        // using reflection to modify the field.
        try {
            Field stateField = FallingBlockEntity.class.getDeclaredField("blockState");
            stateField.setAccessible(true);
            stateField.set(trapEntity, originalState);
        } catch (Exception e) {
            CaveTraps.LOGGER.error("Failed to set internal block state for FallingTrapEntity", e);
        }

        // supposedly this is how it works with normal falling blocks...
        level.setBlock(pos, originalState.getFluidState().createLegacyBlock(), 3);
        trapEntity.setCamoState(camoState);
        level.addFreshEntity(trapEntity);
        return trapEntity;
    }

    public BlockState getCamoState() {
        return this.getEntityData().get(DATA_CAMO_STATE);
    }

    public void setCamoState(BlockState camoState) {
        this.getEntityData().set(DATA_CAMO_STATE, camoState);
    }

    public boolean getBooleanState() {
        return this.getEntityData().get(DATA_BOOLEAN_STATE);
    }

    public void setBooleanState(boolean booleanState) {
        this.getEntityData().set(DATA_BOOLEAN_STATE, booleanState);
    }

    // network syncing
    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.store("camo_state", BlockState.CODEC, this.getCamoState());
        output.putBoolean("has_been_camouflaged", this.getBooleanState());
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        input.read("camo_state", BlockState.CODEC).ifPresent(this::setCamoState);
        this.setBooleanState(input.getBooleanOr("has_been_camouflaged", false));
    }
}
