package com.displace.cavetraps.item;

import com.google.common.base.Suppliers;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import org.jspecify.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class ExplosiveTrapBlockItem extends BlockItem implements GeoItem {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public ExplosiveTrapBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        // This is where the renderer initialization and creation is happening at.
        consumer.accept(new GeoRenderProvider() {
            private final Supplier<GeoItemRenderer<ExplosiveTrapBlockItem>> renderer = Suppliers.memoize(() -> new GeoItemRenderer<>(ExplosiveTrapBlockItem.this));

            @Override
            public @Nullable GeoItemRenderer<ExplosiveTrapBlockItem> getGeoItemRenderer() {
                return this.renderer.get();
            }
        });
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {

    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}
