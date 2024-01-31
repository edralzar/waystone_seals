package io.github.edralzar.mods.waystoneseals.client.rendering;

import net.blay09.mods.balm.api.client.rendering.BalmRenderers;
import net.blay09.mods.waystones.block.entity.ModBlockEntities;

public class ModRenderers {

    public static void initialize(BalmRenderers renderers) {
        if (renderers != null && ModBlockEntities.warpPlate != null) {
            renderers.registerBlockEntityRenderer(ModBlockEntities.warpPlate::get, WarpPlateSealRenderer::new);
        }
    }
}
