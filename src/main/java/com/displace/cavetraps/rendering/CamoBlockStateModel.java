package com.displace.cavetraps.rendering;

import com.displace.cavetraps.blockentities.FallingTrapBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BlockModelPart;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.DelegateBlockStateModel;
import net.neoforged.neoforge.model.data.ModelData;

import java.util.List;

public class CamoBlockStateModel extends DelegateBlockStateModel {

    public CamoBlockStateModel(BlockStateModel originalModel) {
        super(originalModel);
    }

    @Override
    public void collectParts(BlockAndTintGetter level, BlockPos pos, BlockState state, RandomSource random, List<BlockModelPart> parts) {
        ModelData extraData = level.getModelData(pos);
        BlockState camoState = extraData.get(FallingTrapBlockEntity.CAMO_PROPERTY);

        if (camoState != null && !camoState.isAir()) {
            BlockStateModel camoModel = Minecraft.getInstance().getBlockRenderer().getBlockModel(camoState);
            camoModel.collectParts(level, pos, camoState, random, parts);
        } else {
            super.collectParts(level, pos, state, random, parts);
        }
    }

    @Override
    public TextureAtlasSprite particleIcon(BlockAndTintGetter level, BlockPos pos, BlockState state) {
        ModelData extraData = level.getModelData(pos);
        BlockState camoState = extraData.get(FallingTrapBlockEntity.CAMO_PROPERTY);

        if (camoState != null && !camoState.isAir()) {
            return Minecraft.getInstance().getBlockRenderer().getBlockModel(camoState).particleIcon(level, pos, camoState);
        }
        return super.particleIcon(level, pos, state);
    }
}
