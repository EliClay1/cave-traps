package com.displace.cavetraps.entities;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.Level;

public class FallingTrapEntity extends FallingBlockEntity {

    public FallingTrapEntity(EntityType<? extends FallingBlockEntity> entityType, Level level) {
        super(entityType, level);
    }
}
