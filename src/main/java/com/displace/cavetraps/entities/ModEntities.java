package com.displace.cavetraps.entities;

import com.displace.cavetraps.CaveTraps;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModEntities {
    public static final DeferredRegister.Entities ENTITY_TYPES = DeferredRegister.createEntities(CaveTraps.MODID);



    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}
