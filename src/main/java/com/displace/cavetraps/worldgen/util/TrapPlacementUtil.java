package com.displace.cavetraps.worldgen.util;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import static com.displace.cavetraps.worldgen.util.CaveScanUtil.*;

public class TrapPlacementUtil {

    public static boolean canReplaceForTrap(WorldGenLevel level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        if (state.getDestroySpeed(level, pos) < 0 || isLiquid(state) || state.hasBlockEntity()) return false;

        // natural blocks found in cave generation.
        return state.is(BlockTags.BASE_STONE_OVERWORLD)
                || state.is(BlockTags.DIRT)
                || state.is(BlockTags.SAND)
                || state.is(Blocks.GRAVEL)
                || state.is(Blocks.TUFF)
                || state.is(Blocks.DEEPSLATE);
    }

    public static boolean canPlaceEmbeddedFloorTrap(WorldGenLevel level, BlockPos pos) {
        return isCaveFloor(level, pos) && canReplaceForTrap(level, pos);
    }

    public static void setTrapBlock(WorldGenLevel level, BlockPos pos, BlockState state) {
        level.setBlock(pos, state, Block.UPDATE_CLIENTS);
    }

    public static void setIfReplaceable(WorldGenLevel level, BlockPos pos, BlockState state) {
        if (canReplaceForTrap(level, pos)) setTrapBlock(level, pos, state);
    }

    public static boolean hasClearanceAbove(WorldGenLevel level, BlockPos pos, int height) {
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        for (int i = 1; i <= height; i++) {
            mutablePos.setWithOffset(pos, 0, i, 0);
            if (!isAirLike(level, mutablePos)) return false;
        }
        return true;
    }
}
