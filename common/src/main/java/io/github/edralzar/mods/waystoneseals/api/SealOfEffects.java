package io.github.edralzar.mods.waystoneseals.api;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.waystones.api.IWaystone;
import net.blay09.mods.waystones.api.TeleportDestination;
import net.blay09.mods.waystones.api.WaystoneTeleportEvent;
import net.blay09.mods.waystones.api.WaystonesAPI;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.IntFunction;

public class SealOfEffects extends AbstractSeal {

    private ItemStack allowSlot1;
    private ItemStack allowSlot2;

    private ItemStack denySlot1;
    private ItemStack denySlot2;

    public SealOfEffects(UUID id, UUID owner, List<UUID> allowlist, ItemStack allowSlot1, ItemStack allowSlot2, ItemStack denySlot1, ItemStack denySlot2) {
        super(id, owner, allowlist);
        this.allowSlot1 = allowSlot1;
        this.allowSlot2 = allowSlot2;
        this.denySlot1 = denySlot1;
        this.denySlot2 = denySlot2;
    }

    private ItemStack getDenySlot(int i) {
        return switch (i) {
            case 1 -> denySlot1;
            case 2 -> denySlot2;
            default -> ItemStack.EMPTY;
        };
    }
    private ItemStack getAllowSlot(int i) {
        return switch (i) {
            case 1 -> allowSlot1;
            case 2 -> allowSlot2;
            default -> ItemStack.EMPTY;
        };
    }

    @Override
    public void onPostAllowedTeleport(WaystoneTeleportEvent.Post teleportEvent) {
        applyWarpPlateEffects(teleportEvent.getContext().getEntity(), 2, this::getAllowSlot);
    }

    private static void applyWarpPlateEffects(Entity entity, int slotSize, IntFunction<ItemStack> slotGetter) {
        int fireSeconds = 0;
        int poisonSeconds = 0;
        int blindSeconds = 0;
        int featherFallSeconds = 0;
        int fireResistanceSeconds = 0;
        int witherSeconds = 0;
        int potency = 1;
        List<ItemStack> curativeItems = new ArrayList<>();
        for (int i = 0; i < slotSize; i++) {
            ItemStack itemStack = slotGetter.apply(i);
            if (itemStack.getItem() == Items.BLAZE_POWDER) {
                fireSeconds += itemStack.getCount();
            } else if (itemStack.getItem() == Items.POISONOUS_POTATO) {
                poisonSeconds += itemStack.getCount();
            } else if (itemStack.getItem() == Items.INK_SAC) {
                blindSeconds += itemStack.getCount();
            } else if (itemStack.getItem() == Items.MILK_BUCKET || itemStack.getItem() == Items.HONEY_BLOCK) {
                curativeItems.add(itemStack);
            } else if (itemStack.getItem() == Items.DIAMOND) {
                potency = Math.min(4, potency + itemStack.getCount());
            } else if (itemStack.getItem() == Items.FEATHER) {
                featherFallSeconds = Math.min(8, featherFallSeconds + itemStack.getCount());
            } else if (itemStack.getItem() == Items.MAGMA_CREAM) {
                fireResistanceSeconds = Math.min(8, fireResistanceSeconds + itemStack.getCount());
            } else if (itemStack.getItem() == Items.WITHER_ROSE) {
                witherSeconds += itemStack.getCount();
            }
        }

        if (entity instanceof LivingEntity) {
            if (fireSeconds > 0) {
                entity.setSecondsOnFire(fireSeconds);
            }
            if (poisonSeconds > 0) {
                ((LivingEntity) entity).addEffect(new MobEffectInstance(MobEffects.POISON, poisonSeconds * 20, potency));
            }
            if (blindSeconds > 0) {
                ((LivingEntity) entity).addEffect(new MobEffectInstance(MobEffects.BLINDNESS, blindSeconds * 20, potency));
            }
            if (featherFallSeconds > 0) {
                ((LivingEntity) entity).addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, featherFallSeconds * 20, potency));
            }
            if (fireResistanceSeconds > 0) {
                ((LivingEntity) entity).addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, fireResistanceSeconds * 20, potency));
            }
            if (witherSeconds > 0) {
                ((LivingEntity) entity).addEffect(new MobEffectInstance(MobEffects.WITHER, witherSeconds * 20, potency));
            }
            for (ItemStack curativeItem : curativeItems) {
                Balm.getHooks().curePotionEffects((LivingEntity) entity, curativeItem);
            }
        }
    }

    @Override
    public void onPreDeniedTeleport(WaystoneTeleportEvent.Pre teleportEvent) {
        if (denySlot1.getItem() == Items.OBSIDIAN || denySlot2.getItem() == Items.OBSIDIAN) {
            teleportEvent.setCanceled(true);
            //TODO should blocked destinations with additional effect in the other slot apply the effect ?
            return;
        }

        Optional<IWaystone> redirect = WaystonesAPI.getBoundWaystone(this.denySlot1).or(() -> WaystonesAPI.getBoundWaystone(this.denySlot2));
        if (redirect.isPresent()) {
            IWaystone redirectTarget = redirect.get();
            MinecraftServer server = Balm.getHooks().getServer();
            ServerLevel level = server.getLevel(redirectTarget.getDimension());
            teleportEvent.getContext().setDestination(new TeleportDestination(level, redirectTarget.getPos().getCenter(), Direction.NORTH));
        }
    }

    @Override
    public void onPostDeniedTeleport(WaystoneTeleportEvent.Post teleportEvent) {
        applyWarpPlateEffects(teleportEvent.getContext().getEntity(), 2, this::getDenySlot);
    }

    @Override
    protected void writeInternals(CompoundTag tag) {
        //FIXME
    }

    static Seal read(CompoundTag tag) {
        UUID id = readId(tag);
        UUID owner = readOwner(tag);
        List<UUID> allowList = readAllowList(tag);
        //FIXME
        ItemStack denySlot1 = new ItemStack(Items.FIRE_CHARGE, 10);
        return new SealOfEffects(id, owner, allowList, ItemStack.EMPTY, ItemStack.EMPTY, denySlot1, ItemStack.EMPTY);
    }

    @Override
    public boolean isValid() {
        return true;
    }
}
