package io.github.edralzar.mods.waystoneseals.api;

import net.blay09.mods.waystones.api.WaystoneTeleportEvent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;

import java.util.List;
import java.util.UUID;

public class SealOfDenial extends AbstractSeal {

    public SealOfDenial(UUID id, UUID owner, List<UUID> allowlist) {
        super(id, owner, allowlist);
    }

    @Override
    public void onPreDeniedTeleport(WaystoneTeleportEvent.Pre teleportEvent) {
        teleportEvent.setCanceled(true);
        if (teleportEvent.getContext().getEntity() instanceof Player p) {
            p.sendSystemMessage(Component.translatable("chat.seal.denied"));
            p.level().playSound(null, p, SoundEvents.GLASS_HIT, SoundSource.PLAYERS, 1f, 1f);
        }
    }

    @Override
    protected void writeInternals(CompoundTag tag) { }

    static Seal read(CompoundTag tag) {
        return new SealOfDenial(readId(tag), readOwner(tag), readAllowList(tag));
    }

    @Override
    public boolean isValid() {
        return true;
    }
}
