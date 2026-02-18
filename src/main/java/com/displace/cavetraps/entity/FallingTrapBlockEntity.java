package com.displace.cavetraps.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.Nullable;

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

    // saving and loading.
    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.store("camo_state", BlockState.CODEC, this.camoState);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        input.read("camo_state", BlockState.CODEC).ifPresent(state -> this.camoState = state);
    }

    // Syncing with Network

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        BlockState.CODEC.encodeStart(NbtOps.INSTANCE, this.camoState)
                .result()
                .ifPresent(encodedState -> tag.put("camo_state", encodedState));
        return tag;
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
