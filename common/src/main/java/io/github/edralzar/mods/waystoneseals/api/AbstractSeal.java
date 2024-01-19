package io.github.edralzar.mods.waystoneseals.api;

import net.blay09.mods.waystones.api.WaystoneTeleportEvent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class AbstractSeal implements Seal {

    static Seal read(CompoundTag tag) {
        return switch (SealType.valueOf(tag.getString("sealType"))) {
            case DENIAL -> SealOfDenial.read(tag);
            case REDIRECTION -> SealOfRedirection.read(tag);
            case EFFECTS -> SealOfEffects.read(tag);
            default -> INVALID;
        };
    }

    private final UUID id;
    private final UUID owner;
    private List<UUID> allowlist;

    public AbstractSeal(UUID id, UUID owner, List<UUID> allowlist) {
        this.id = id;
        this.owner = owner;
        this.allowlist = allowlist;
    }

    @Override
    public UUID getSealUUID() {
        return this.id;
    }

    @Override
    public UUID getOwner() {
        return owner;
    }

    @Override
    public List<UUID> getAllowList() {
        return allowlist;
    }

    @Override
    public void onPreAllowedTeleport(WaystoneTeleportEvent.Pre teleportEvent) {
        //NO-OP
    }

    @Override
    public void onPostAllowedTeleport(WaystoneTeleportEvent.Post teleportEvent) {
        //NO-OP
    }

    @Override
    public void onPreDeniedTeleport(WaystoneTeleportEvent.Pre teleportEvent) {
        //NO-OP
    }

    @Override
    public void onPostDeniedTeleport(WaystoneTeleportEvent.Post teleportEvent) {
        //NO-OP
    }

    protected void write(CompoundTag tag) {
        tag.putString("sealType", SealType.typeOf(this).name());
        tag.putUUID("sealId", getSealUUID());
        tag.putUUID("owner", getOwner());
        ListTag allow = new ListTag();
        getAllowList().forEach(uuid -> allow.add(NbtUtils.createUUID(uuid)));
        tag.putInt("alSize", allow.size());
        tag.put("allowList", allow);
        writeInternals(tag);
    }

    protected abstract void writeInternals(CompoundTag sealTag);

    static UUID readId(CompoundTag sealTag) {
        return sealTag.getUUID("sealId");
    }

    static UUID readOwner(CompoundTag sealTag) {
        return sealTag.getUUID("owner");
    }

    static List<UUID> readAllowList(CompoundTag sealTag) {
        int size = sealTag.getInt("alSize");
        ListTag al = sealTag.getList("allowList", size);
        List<UUID> result = new ArrayList<>(al.size());
        al.forEach(t -> result.add(NbtUtils.loadUUID(t)));
        return result;
    }

}
