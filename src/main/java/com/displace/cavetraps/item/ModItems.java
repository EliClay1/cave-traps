package com.displace.cavetraps.item;

import com.displace.cavetraps.CaveTraps;
import com.displace.cavetraps.block.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(CaveTraps.MODID);


    public static final DeferredItem<Item> FALLING_TRAP_BLOCK_ITEM = ITEMS.register("falling_trap_block", registryName -> (
            new BlockItem(ModBlocks.FALLING_TRAP_BLOCK.get(), new Item.Properties().setId(ResourceKey.create(Registries.ITEM, registryName)))
    ));

    public static final DeferredItem<Item> TEST_BLOCK_ITEM = ITEMS.register("test_block", registryName -> (
            new BlockItem(ModBlocks.TEST_BLOCK.get(), new Item.Properties().setId(ResourceKey.create(Registries.ITEM, registryName)))
    ));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
