package io.github.edralzar.mods.waystoneseals.item;

import io.github.edralzar.mods.waystoneseals.api.AbstractSeal;
import io.github.edralzar.mods.waystoneseals.api.Seal;
import io.github.edralzar.mods.waystoneseals.api.SealManager;
import io.github.edralzar.mods.waystoneseals.api.SealOfDenial;
import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.waystones.api.IWaystone;
import net.blay09.mods.waystones.block.entity.WaystoneBlockEntityBase;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class SealItem extends Item {

    public SealItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean isFoil(ItemStack itemStack) {
        Seal seal = getSeal(null, itemStack);
        return seal != null && seal.isValid() && seal.isOwned();
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> list, TooltipFlag flag) {
        Seal seal = getSeal(null, stack);
        if (seal.isValid() && seal.isOwned()) {
            var owner = Balm.getHooks().getServer().getPlayerList().getPlayer(seal.getOwner());
            if (owner == null || !seal.isOwnedBy(Balm.getProxy().getClientPlayer())) {
                list.add(Component.translatable("tooltip.seal.not_owned"));
            } else {
                list.add(Component.translatable("tooltip.seal.owned"));
            }
        }
        else {
            list.add(Component.translatable("tooltip.seal.unbound"));
        }
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        if (player == null) {
            return InteractionResult.PASS;
        }

        InteractionResult result = InteractionResult.sidedSuccess(context.getLevel().isClientSide());

        ItemStack heldItem = player.getItemInHand(context.getHand());
        Level world = context.getLevel();
        BlockEntity tileEntity = world.getBlockEntity(context.getClickedPos());
        if (tileEntity instanceof WaystoneBlockEntityBase wb) {
            player.swing(InteractionHand.MAIN_HAND);
            IWaystone waystone = wb.getWaystone();
            if (!world.isClientSide) {
                Seal boundSeal = getSeal(player.getServer(), heldItem);
                if (boundSeal == AbstractSeal.INVALID) {
                    if (!waystone.hasOwner() || !waystone.isOwner(player)) {
                        player.sendSystemMessage(Component.translatable("chat.seal.not_owned.waystone"));
                        return result;
                    }
                    boundSeal = createSeal(player);
                    if (boundSeal.isValid()) {
                        SealManager sealManager = SealManager.get(player.getServer());
                        sealManager.registerSeal(boundSeal);
                    }
                }
                //FIXME for debug. mutualize permission check(s)
                if (boundSeal == AbstractSeal.INVALID /*|| !boundSeal.getOwner().equals(waystone.getOwnerUid())*/) {
                    player.sendSystemMessage(Component.translatable("chat.seal.not_owned"));
                    return result;
                }

                SealManager sealManager = SealManager.get(player.getServer());
                sealManager.applySealTo(waystone, boundSeal);
                var chatComponent = Component.translatable("chat.seal.bound", waystone.getName(), player.getName());
                chatComponent.withStyle(ChatFormatting.YELLOW);
                player.displayClientMessage(chatComponent, true);
                heldItem.shrink(1);

                world.playSound(null, context.getClickedPos(), SoundEvents.PLAYER_LEVELUP, SoundSource.BLOCKS, 0.2f, 2f);
                return result;
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    public void onDestroyed(ItemEntity itemEntity) {
        super.onDestroyed(itemEntity);
        Seal s = getSeal(itemEntity.getServer(), itemEntity.getItem());
        if (s.isValid()) {
            SealManager.get(itemEntity.getServer()).deleteSeal(s);
        }
    }

    protected Seal createSeal(Player player) {
        //FIXME don't have any configuration by default once the Sealing GUI is done
//        return new SealOfEffects(UUID.randomUUID(), player.getUUID(), new ArrayList<>(),
//                ItemStack.EMPTY, ItemStack.EMPTY, Items.OBSIDIAN.getDefaultInstance(), ItemStack.EMPTY);
        return new SealOfDenial(UUID.randomUUID(), player.getUUID(), new ArrayList<>());
    }

    public Seal getSeal(MinecraftServer server, ItemStack itemStack) {
        CompoundTag compound = itemStack.getTag();
        if (compound != null && compound.contains("sealId", Tag.TAG_INT_ARRAY)) {
            UUID sealId = NbtUtils.loadUUID(Objects.requireNonNull(compound.get("sealId")));
            return SealManager.get(server).getSealOther(sealId).orElse(AbstractSeal.INVALID);
        }

        return AbstractSeal.INVALID;
    }

    public void setSeal(ItemStack itemStack, @Nullable Seal seal) {
        CompoundTag tagCompound = itemStack.getTag();
        if (tagCompound == null) {
            tagCompound = new CompoundTag();
            itemStack.setTag(tagCompound);
        }

        if (seal != null) {
            tagCompound.put("sealId", NbtUtils.createUUID(seal.getSealUUID()));
        } else {
            tagCompound.remove("sealId");
        }
    }

    public static ItemStack materialize(Seal seal) {
        if (!(seal instanceof AbstractSeal)) return ItemStack.EMPTY;
        if (seal instanceof SealOfDenial) {
            ItemStack stack = new ItemStack(ModItems.seal, 1);
            ModItems.seal.setSeal(stack, seal);
            return stack;
        }
        return new ItemStack(ModItems.blankSeal, 1);
    }
}
