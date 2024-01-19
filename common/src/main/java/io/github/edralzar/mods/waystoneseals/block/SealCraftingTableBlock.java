package io.github.edralzar.mods.waystoneseals.block;

import io.github.edralzar.mods.waystoneseals.block.entity.SealCraftingTableBlockEntity;
import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.menu.BalmMenuProvider;
import net.blay09.mods.waystones.core.WarpMode;
import net.blay09.mods.waystones.core.WaystoneProxy;
import net.blay09.mods.waystones.menu.WaystoneSelectionMenu;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public class SealCraftingTableBlock extends BaseEntityBlock {


    public SealCraftingTableBlock(Properties properties) {
        super(properties.pushReaction(PushReaction.BLOCK));
        this.registerDefaultState(this.stateDefinition.any());
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter world, List<Component> list, TooltipFlag flag) {
        //FIXME
        super.appendHoverText(stack, world, list, flag);

        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("UUID", IntArrayTag.TAG_INT_ARRAY)) {
            WaystoneProxy waystone = new WaystoneProxy(null, NbtUtils.loadUUID(Objects.requireNonNull(tag.get("UUID"))));
            if (waystone.isValid()) {
                addWaystoneNameToTooltip(list, waystone);
            }
        }
    }

    protected void addWaystoneNameToTooltip(List<Component> tooltip, WaystoneProxy waystone) {
        var component = Component.literal(waystone.getName());
        component.withStyle(ChatFormatting.AQUA);
        tooltip.add(component);
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult blockHitResult) {
        if (!world.isClientSide) {
            Balm.getNetworking().openGui(player, new BalmMenuProvider() {
                @Override
                public Component getDisplayName() {
                    return Component.translatable("block.waystones.portstone");
                }

                @Override
                public AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
                    return WaystoneSelectionMenu.createWaystoneSelection(i, player, WarpMode.PORTSTONE_TO_WAYSTONE, null); //FIXME
                }

                @Override
                public void writeScreenOpeningData(ServerPlayer player, FriendlyByteBuf buf) {
                    buf.writeByte(WarpMode.PORTSTONE_TO_WAYSTONE.ordinal());
                    buf.writeBlockPos(pos);
                }
            });
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
//        BlockEntity blockEntity = world.getBlockEntity(pos);
//
//        if (blockEntity instanceof SealCraftingTableBlockEntity) {
//            if (!world.isClientSide) {
//                CompoundTag tag = stack.getTag();
//                WaystoneProxy existingWaystone = null;
//                if (tag != null && tag.contains("UUID", IntArrayTag.TAG_INT_ARRAY)) {
//                    existingWaystone = new WaystoneProxy(world.getServer(), NbtUtils.loadUUID(Objects.requireNonNull(tag.get("UUID"))));
//                }
//
//                if (existingWaystone != null && existingWaystone.isValid() && existingWaystone.getBackingWaystone() instanceof Waystone) {
//                    ((SealCraftingTableBlockEntity) blockEntity).initializeFromExisting((ServerLevelAccessor) world,
//                            ((Waystone) existingWaystone.getBackingWaystone()),
//                            stack);
//                } else {
//                    ((SealCraftingTableBlockEntity) blockEntity).initializeWaystone((ServerLevelAccessor) world, placer, WaystoneOrigin.PLAYER);
//                }
//            }
//
//            if (placer instanceof Player) {
//                IWaystone waystone = ((SealBlockEntityBase) blockEntity).getWaystone();
//                PlayerWaystoneManager.activateWaystone(((Player) placer), waystone);
//
//                if (!world.isClientSide) {
//                    WaystoneSyncManager.sendActivatedWaystones(((Player) placer));
//                }
//            }
//        }
    }

    @Override
    public RenderShape getRenderShape(BlockState blockState) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SealCraftingTableBlockEntity(pos, state);
    }

    @Override
    public boolean isPathfindable(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, PathComputationType pathComputationType) {
        return false;
    }
}
