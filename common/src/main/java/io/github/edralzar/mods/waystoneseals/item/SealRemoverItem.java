package io.github.edralzar.mods.waystoneseals.item;

import io.github.edralzar.mods.waystoneseals.api.Seal;
import io.github.edralzar.mods.waystoneseals.api.SealManager;
import net.blay09.mods.waystones.api.IWaystone;
import net.blay09.mods.waystones.block.entity.WaystoneBlockEntityBase;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SealRemoverItem extends Item {

    public SealRemoverItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> list, TooltipFlag flag) {
        super.appendHoverText(stack, world, list, flag);

        var textComponent = Component.translatable("tooltip.seal.rod");
        textComponent.withStyle(ChatFormatting.GRAY);
        list.add(textComponent);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        if (player == null) {
            return InteractionResult.PASS;
        }

        Level world = context.getLevel();
        BlockEntity tileEntity = world.getBlockEntity(context.getClickedPos());
        if (tileEntity instanceof WaystoneBlockEntityBase wb) {
            player.swing(InteractionHand.MAIN_HAND);
            IWaystone waystone = wb.getWaystone();
            if (!world.isClientSide) {
                SealManager sealManager = SealManager.get(player.getServer());
                if (sealManager.canRemoveSealFrom(waystone, player) || player.isCreative()) {
                    Seal removedSeal = sealManager.removeSealFrom(waystone);
                    ItemStack removedSealItem = SealItem.materialize(removedSeal); //FIXME pop with ingredients
                    if (!removedSealItem.isEmpty()) {
                        Block.popResourceFromFace(world, context.getClickedPos(), context.getClickedFace(), removedSealItem);
                    }
                }
                return InteractionResult.sidedSuccess(world.isClientSide());
            }
        }
        return InteractionResult.PASS;
    }
}
