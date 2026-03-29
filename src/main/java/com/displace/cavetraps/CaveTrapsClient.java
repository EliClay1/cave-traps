package com.displace.cavetraps;

import com.displace.cavetraps.block.ModBlocks;
import com.displace.cavetraps.blockentities.ModBlockEntities;
import com.displace.cavetraps.blockentities.rendering.FallingTrapBER;
import com.displace.cavetraps.entities.ModEntities;
import com.displace.cavetraps.entities.rendering.FallingTrapER;
import com.displace.cavetraps.entities.rendering.FallingTrapEntityRenderState;
import com.displace.cavetraps.rendering.CamoBlockStateModel;
import com.displace.cavetraps.testing.TestBER;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

import java.util.ArrayList;
import java.util.List;

// This class will not load on dedicated servers. Accessing client side code from here is safe.
@Mod(value = CaveTraps.MODID, dist = Dist.CLIENT)
// You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
@EventBusSubscriber(modid = CaveTraps.MODID, value = Dist.CLIENT)
public class CaveTrapsClient {
    public CaveTrapsClient(ModContainer container) {
        // Allows NeoForge to create a config screen for this mod's configs.
        // The config screen is accessed by going to the Mods screen > clicking on your mod > clicking on config.
        // Do not forget to add translations for your config options to the en_us.json file.
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        // Some client setup code
        CaveTraps.LOGGER.info("HELLO FROM CLIENT SETUP");
        CaveTraps.LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
    }

    @SubscribeEvent
    public static void registerBlockEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {

        // block entities
        event.registerBlockEntityRenderer(ModBlockEntities.TEST_BLOCK_ENTITY.get(), TestBER::new);
        event.registerBlockEntityRenderer(ModBlockEntities.FALLING_TRAP_BLOCK_ENTITY.get(), FallingTrapBER::new);

        // block entities with GeckoLib
        event.registerBlockEntityRenderer(ModBlockEntities.EXPLOSIVE_TRAP_BLOCK_ENTITY.get(),
                context -> new GeoBlockRenderer<>(ModBlockEntities.EXPLOSIVE_TRAP_BLOCK_ENTITY.get()));

        // entities
        event.registerEntityRenderer(ModEntities.FALLING_TRAP_ENTITY.get(), FallingTrapER::new);
    }

    @SubscribeEvent
    public static void registerEntityRenderers(ModelEvent.ModifyBakingResult event) {
        Block fallingTrapBlock = ModBlocks.FALLING_TRAP_BLOCK.get();
        List<BlockState> statesToModify  = new ArrayList<>();

        for (BlockState state : event.getBakingResult().blockStateModels().keySet()) {
            if (state.is(fallingTrapBlock)) {
                statesToModify.add(state);
            }
        }

        for (BlockState state : statesToModify) {
            event.getBakingResult().blockStateModels().computeIfPresent(state, (keyState, existingModel) -> new CamoBlockStateModel(existingModel));
        }
    }
}
