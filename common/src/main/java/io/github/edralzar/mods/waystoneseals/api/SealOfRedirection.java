package io.github.edralzar.mods.waystoneseals.api;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.waystones.api.IWaystone;
import net.blay09.mods.waystones.api.TeleportDestination;
import net.blay09.mods.waystones.api.WaystoneTeleportEvent;
import net.blay09.mods.waystones.core.Waystone;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;

import java.util.List;
import java.util.UUID;

public class SealOfRedirection extends AbstractSeal {

    private final IWaystone redirectTarget;
    private final TeleportDestination redirectDestination;

    public SealOfRedirection(UUID id, UUID owner, List<UUID> allowlist, IWaystone redirectTarget) {
        super(id, owner, allowlist);
        MinecraftServer server = Balm.getHooks().getServer();
        ServerLevel level = server.getLevel(redirectTarget.getDimension());
        this.redirectTarget = redirectTarget;
        this.redirectDestination = new TeleportDestination(level, redirectTarget.getPos().getCenter(), Direction.NORTH);
    }

    @Override
    public void onPreDeniedTeleport(WaystoneTeleportEvent.Pre teleportEvent) {
        teleportEvent.getContext().setDestination(this.redirectDestination);
    }

    @Override
    protected void writeInternals(CompoundTag tag) {
        Waystone.write(this.redirectTarget, tag);
    }

    static SealOfRedirection read(CompoundTag tag) {
        return new SealOfRedirection(
                readId(tag),
                readOwner(tag),
                readAllowList(tag),
                Waystone.read(tag)
        );
    }

    @Override
    public boolean isValid() {
        return this.redirectTarget.isValid();
    }
}
