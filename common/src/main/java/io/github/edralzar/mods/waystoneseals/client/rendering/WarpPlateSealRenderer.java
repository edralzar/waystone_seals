package io.github.edralzar.mods.waystoneseals.client.rendering;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.edralzar.mods.waystoneseals.api.SealManager;
import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.waystones.block.entity.WarpPlateBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

public class WarpPlateSealRenderer implements BlockEntityRenderer<WarpPlateBlockEntity> {

    public WarpPlateSealRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(WarpPlateBlockEntity entity, float tickDelta, PoseStack matrix, MultiBufferSource vertexConsumers, int light, int overlay) {
        SealManager sealManager = SealManager.get(Balm.getHooks().getServer());
        sealManager.getSealForWaystone(entity.getWaystone().getWaystoneUid())
                .ifPresent(seal ->  RenderSealUtils.renderSealOnPlate(entity, matrix, vertexConsumers, light, overlay));
    }
}
