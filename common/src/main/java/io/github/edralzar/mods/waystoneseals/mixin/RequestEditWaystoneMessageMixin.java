package io.github.edralzar.mods.waystoneseals.mixin;

import io.github.edralzar.mods.waystoneseals.Seals;
import io.github.edralzar.mods.waystoneseals.tag.ModItemTags;
import net.blay09.mods.waystones.network.message.RequestEditWaystoneMessage;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RequestEditWaystoneMessage.class)
public class RequestEditWaystoneMessageMixin {
    
    @Inject(at = @At("HEAD"), method = "handle", cancellable = true)
    private static void onHandle(ServerPlayer player, RequestEditWaystoneMessage message, CallbackInfo ci) {
        Seals.LOG.warn("RequestEditWaystoneMessageMixin");
        if (player.getMainHandItem().is(ModItemTags.USEABLE_ON_WAYSTONES)) {
            ci.cancel();
        }
    }
}