package io.github.edralzar.mods.waystoneseals.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.edralzar.mods.waystoneseals.api.SealManager;
import io.github.edralzar.mods.waystoneseals.client.rendering.RenderSealUtils;
import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.waystones.block.entity.WaystoneBlockEntity;
import net.blay09.mods.waystones.client.render.WaystoneRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static io.github.edralzar.mods.waystoneseals.client.rendering.RenderSealUtils.renderSeal;

@Mixin(WaystoneRenderer.class)
public class WaystoneRendererMixin {

    @Inject(at = @At("TAIL"), method = "render(Lnet/blay09/mods/waystones/block/entity/WaystoneBlockEntity;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;II)V")
    private void onRender(WaystoneBlockEntity tileEntity, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int combinedLightIn, int combinedOverlayIn, CallbackInfo ci) {
        SealManager sealManager = SealManager.get(Balm.getHooks().getServer());
        sealManager.getSealForWaystone(tileEntity.getWaystone().getWaystoneUid())
                        .ifPresent(seal -> renderSeal(tileEntity, poseStack, buffer, combinedLightIn, combinedOverlayIn, RenderSealUtils.BlockOffsets.WAYSTONE));
    }

}
