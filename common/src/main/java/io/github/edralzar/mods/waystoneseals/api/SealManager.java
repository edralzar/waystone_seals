package io.github.edralzar.mods.waystoneseals.api;

import io.github.edralzar.mods.waystoneseals.Seals;
import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.waystones.api.IWaystone;
import net.blay09.mods.waystones.api.WaystoneTeleportEvent;
import net.blay09.mods.waystones.core.WaystoneManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class SealManager extends SavedData {

    private static final String DATA_NAME = Seals.MOD_ID + ".Seals";
    private static final String TAG_SEALS_BY_WAYSTONE = "SealsByWaystone";
    private static final String TAG_SEALS_OTHER = "SealsOther";

    private final Map<UUID, Seal> sealsByWaystone = new HashMap<>();
    private final Map<UUID, Seal> otherSeals = new HashMap<>();
    private final WaystoneManager waystoneManager;

    public SealManager(WaystoneManager waystoneManager) {
        Balm.getEvents().onEvent(WaystoneTeleportEvent.Pre.class, this::evaluatePre);
        Balm.getEvents().onEvent(WaystoneTeleportEvent.Post.class, this::evaluatePost);

        this.waystoneManager = waystoneManager;
    }

    private void evaluatePre(WaystoneTeleportEvent.Pre pre) {
        getSealForWaystone(pre.getContext().getTargetWaystone().getWaystoneUid())
                .ifPresent(s -> {
                    if (isEntityAllowed(s, pre.getContext().getEntity())) {
                        s.onPreAllowedTeleport(pre);
                    }
                    else {
                        s.onPreDeniedTeleport(pre);
                    }
                });
    }
    private void evaluatePost(WaystoneTeleportEvent.Post post) {
        getSealForWaystone(post.getContext().getTargetWaystone().getWaystoneUid())
                .ifPresent(s -> {
                    if (isEntityAllowed(s, post.getContext().getEntity())) {
                        s.onPostAllowedTeleport(post);
                    }
                    else {
                        s.onPostDeniedTeleport(post);
                    }
                });
    }

    public static boolean isEntityAllowed(Seal seal, Entity teleportedEntity) {
        if (!(teleportedEntity instanceof Player player)) {
            return false;
        }
        if (player.isCreative()) return true;
        if (player.getMainHandItem().is(Items.DANDELION)) return false;
        return seal.getOwner().equals(player.getUUID()) || seal.getAllowList().contains(player.getUUID());
    }

    public void applySealTo(IWaystone waystone, @Nullable Seal seal) {
        if (seal == Seal.INVALID) return;
        if (seal == null) {
            removeSealFrom(waystone);
            return;
        }
        otherSeals.remove(seal.getSealUUID());
        sealsByWaystone.put(waystone.getWaystoneUid(), seal);
        setDirty();
    }

    public boolean canRemoveSealFrom(IWaystone waystone, Player player) {
        if (!this.sealsByWaystone.containsKey(waystone.getWaystoneUid())) {
            return true;
        }
        return waystone.isOwner(player) && sealsByWaystone.get(waystone.getWaystoneUid()).getOwner().equals(player.getUUID());
    }

    @Nullable
    public Seal removeSealFrom(IWaystone waystone) {
        Seal s = sealsByWaystone.remove(waystone.getWaystoneUid());
        if (s != null) {
            otherSeals.put(s.getSealUUID(), s);
            setDirty();
            return s;
        }
        setDirty();
        return null;
    }

    public void registerSeal(Seal seal) {
        if (seal == Seal.INVALID) return;
        otherSeals.put(seal.getSealUUID(), seal);
        setDirty();
    }

    public void deleteSeal(Seal seal) {
        otherSeals.remove(seal.getSealUUID());
        this.sealsByWaystone.entrySet().removeIf(e -> e.getValue().getSealUUID().equals(seal.getSealUUID()));
        setDirty();
    }

    public Optional<Seal> getSealForWaystone(UUID waystoneUid) {
        return Optional.ofNullable(sealsByWaystone.get(waystoneUid));
//
//        //FIXME temp pending an item to actually configure and set seals
//        return Optional.of(new AbstractSeal(UUID.randomUUID(), UUID.randomUUID(), List.of()) {
//            @Override
//            protected void writeInternals(CompoundTag sealTag) { }
//
//            @Override
//            public void onPreDeniedTeleport(WaystoneTeleportEvent.Pre teleportEvent) {
//                if (teleportEvent.getContext().getEntity() instanceof LivingEntity living) {
//                    living.addEffect(new MobEffectInstance(MobEffects.WITHER, 6 * 20, 1));
//                }
//            }
//
//            @Override
//            public void onPostDeniedTeleport(WaystoneTeleportEvent.Post teleportEvent) {
//                if (teleportEvent.getContext().getEntity() instanceof LivingEntity living) {
//                    living.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 3 * 20, 1));
//                }
//            }
//
//            @Override
//            public boolean isValid() {
//                return true;
//            }
//        });
    }

    public Optional<Seal> getSealOther(UUID sealUuid) {
        return Optional.ofNullable(this.otherSeals.get(sealUuid));
    }

    public static SealManager read(CompoundTag tagCompound, WaystoneManager waystoneManager) {
        SealManager sealManager = new SealManager(waystoneManager);
        ListTag tagList = tagCompound.getList(TAG_SEALS_BY_WAYSTONE, Tag.TAG_COMPOUND);
        for (Tag tag : tagList) {
            CompoundTag compound = (CompoundTag) tag;
            UUID waystoneId = compound.getUUID("waystoneId");
            Seal seal = AbstractSeal.read(compound);
            if (seal == AbstractSeal.INVALID) continue;
            sealManager.sealsByWaystone.put(waystoneId, seal);
        }
        ListTag otherSealsList = tagCompound.getList(TAG_SEALS_OTHER, Tag.TAG_COMPOUND);
        for (Tag tag : otherSealsList) {
            CompoundTag compound = (CompoundTag) tag;
            Seal seal = AbstractSeal.read(compound);
            if (seal == AbstractSeal.INVALID) continue;
            sealManager.otherSeals.put(seal.getSealUUID(), seal);
        }
        return sealManager;
    }

    @Override
    public CompoundTag save(CompoundTag tagCompound) {
        ListTag tagList = new ListTag();
        sealsByWaystone.forEach((wid, seal) -> {
            //ignore waystones that have been destroyed, effectively deleting the associated seal
            if (this.waystoneManager.getWaystoneById(wid).isEmpty()) return;
            CompoundTag sealEntry = new CompoundTag();
            sealEntry.putUUID("waystoneId", wid);
            if (seal instanceof AbstractSeal as) {
                as.write(sealEntry);
                tagList.add(sealEntry);
            }
        });
        tagCompound.put(TAG_SEALS_BY_WAYSTONE, tagList);

        ListTag otherSealsList = new ListTag();
        this.otherSeals.forEach((sid, seal) -> {
            CompoundTag sealEntry = new CompoundTag();
            if (seal instanceof AbstractSeal as) {
                as.write(sealEntry);
                otherSealsList.add(sealEntry);
            }
        });
        tagCompound.put(TAG_SEALS_OTHER, otherSealsList);
        return tagCompound;
    }

    public static SealManager get(@Nullable MinecraftServer server) {
        if (server == null) {
            server = Balm.getHooks().getServer();
        }
        final WaystoneManager waystoneManager = WaystoneManager.get(server);
        ServerLevel overworld = server.getLevel(Level.OVERWORLD);
        return Objects.requireNonNull(overworld).getDataStorage().computeIfAbsent(
                tag -> SealManager.read(tag, waystoneManager),
                () -> new SealManager(waystoneManager),
                DATA_NAME);
    }

}
