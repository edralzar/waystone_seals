package io.github.edralzar.mods.waystoneseals.api;

import net.blay09.mods.waystones.api.WaystoneTeleportEvent;
import net.minecraft.nbt.CompoundTag;

import java.util.List;
import java.util.UUID;

public class SealOfDenial extends AbstractSeal {

    public SealOfDenial(UUID id, UUID owner, List<UUID> allowlist) {
        super(id, owner, allowlist);
    }

    @Override
    public void onPreDeniedTeleport(WaystoneTeleportEvent.Pre teleportEvent) {

    }

    @Override
    public void onPreAllowedTeleport(WaystoneTeleportEvent.Pre teleportEvent) {
        this.onPreDeniedTeleport(teleportEvent);
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
