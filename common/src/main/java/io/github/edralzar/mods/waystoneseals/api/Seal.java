package io.github.edralzar.mods.waystoneseals.api;

import net.blay09.mods.waystones.api.WaystoneTeleportEvent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

import java.util.List;
import java.util.UUID;

public interface Seal {

    UUID NO_OWNER = UUID.randomUUID();

    enum SealType {
        DENIAL,
        REDIRECTION,
        EFFECTS,

        UNKNOWN;

        public static SealType typeOf(Seal seal) {
            if (seal instanceof SealOfDenial) return DENIAL;
            if (seal instanceof SealOfRedirection) return REDIRECTION;
            if (seal instanceof SealOfEffects) return EFFECTS;

            return UNKNOWN;
        }
    }
    Seal INVALID = new AbstractSeal(NO_OWNER, UUID.randomUUID(), List.of()) {
        @Override
        protected void writeInternals(CompoundTag sealTag) { }

        @Override
        public boolean isValid() {
            return false;
        }
    };

    UUID getSealUUID();

    UUID getOwner();

    default boolean isOwned() {
        return getOwner() != NO_OWNER;
    }

    default boolean isOwnedBy(Player player) {
        return player.getUUID().equals(getOwner());
    }

    List<UUID> getAllowList();

    void onPreAllowedTeleport(WaystoneTeleportEvent.Pre teleportEvent);

    void onPostAllowedTeleport(WaystoneTeleportEvent.Post teleportEvent);

    void onPreDeniedTeleport(WaystoneTeleportEvent.Pre teleportEvent);
    void onPostDeniedTeleport(WaystoneTeleportEvent.Post teleportEvent);

    boolean isValid();
}
