package com.displace.cavetraps.blockentities;

import com.displace.cavetraps.CaveTraps;
import com.displace.cavetraps.block.FallingTrapBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.model.data.ModelData;
import net.neoforged.neoforge.model.data.ModelProperty;
import org.jspecify.annotations.Nullable;

public class FallingTrapBlockEntity extends BlockEntity {
    public static final ModelProperty<BlockState> CAMO_PROPERTY = new ModelProperty<>();

    // this is still a test of sorts, so I am going to document the process with comments.

    // This is defining the default block state so that the block will always render some kind of default.
    // Realistically, this should be its own block state, but there isn't a texture yet.

    private BlockState camoState = Blocks.AIR.defaultBlockState();
    private boolean hasBeenCamouflaged = false;

    public FallingTrapBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.FALLING_TRAP_BLOCK_ENTITY.get(), pos, blockState);
    }

    public boolean getHasBeenTriggered() {
        return this.hasBeenCamouflaged;
    }

    public void setHasBeenTriggered(boolean triggered) {
        this.hasBeenCamouflaged = triggered;
        setChanged();
    }

    // These are setters and getter for the camo state so that other classes can access it.
    public BlockState getCamoState() {
        return this.camoState;
    }

    @Override
    public ModelData getModelData() {
        return ModelData.builder()
                .with(CAMO_PROPERTY, this.camoState)
                .build();
    }

    public void setCamoState(BlockState camo) {
        hasBeenCamouflaged = true;
        this.camoState = camo;

        if (level != null && !level.isClientSide()) {
            boolean hasCamo = camo != null && !camo.isAir();
            level.setBlock(getBlockPos(), getBlockState().setValue(FallingTrapBlock.HAS_CAMO, hasCamo), Block.UPDATE_CLIENTS);
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_ALL);
            setChanged();
        }
    }

    // this saves the block state data so it can be references by other classes.
    // It uses a CODEC because this is doing stuff on the network level.
    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.store("camo_state", BlockState.CODEC, this.camoState);
        output.putBoolean("has_been_camouflaged", this.hasBeenCamouflaged);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        input.read("camo_state", BlockState.CODEC).ifPresent(blockState -> this.camoState = blockState);
        this.hasBeenCamouflaged = input.getBooleanOr("has_been_camouflaged", false);
    }

    // Next up is network syncing. This is especially important because the server can see and process everything,
    // but the client doesn't know how to do that without these functions.
    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return this.saveWithFullMetadata(registries);
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection net, ValueInput valueInput) {
        super.onDataPacket(net, valueInput);

        if (level != null && level.isClientSide()) {
            requestModelDataUpdate();
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
    }
}
