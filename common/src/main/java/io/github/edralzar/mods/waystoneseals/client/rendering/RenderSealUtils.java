package io.github.edralzar.mods.waystoneseals.client.rendering;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import io.github.edralzar.mods.waystoneseals.item.ModItems;
import net.blay09.mods.waystones.block.WaystoneBlockBase;
import net.blay09.mods.waystones.block.entity.WaystoneBlockEntityBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;

public class RenderSealUtils {

    static final ItemStack SEAL_ITEM_STACK;

    static {
        SEAL_ITEM_STACK = new ItemStack(ModItems.seal, 1);
        SEAL_ITEM_STACK.enchant(Enchantments.UNBREAKING, 1);
    }

    public enum BlockOffsets {
        WAYSTONE(1.6f, -0.05f),
        SHARESTONE(1.83f, 0.02f);


        private final float offsetHorizontal;
        private final float offsetY;

        BlockOffsets(float offsetY, float offsetHorizontal) {
            this.offsetY = offsetY;
            this.offsetHorizontal = offsetHorizontal;
        }
    }

    public static void renderSeal(WaystoneBlockEntityBase tileEntity, PoseStack poseStack, MultiBufferSource buffer, int combinedLightIn,
                                  int combinedOverlayIn, BlockOffsets waystoneOffsets) {
        float scale = 0.33f;
        float offsetY = waystoneOffsets.offsetY;
        float stoneOffset = waystoneOffsets.offsetHorizontal;

        Direction facing = tileEntity.getBlockState().getValue(WaystoneBlockBase.FACING);
        float angle = facing.toYRot();
        float offsetX = 0.3f;
        float offsetZ = 1f + stoneOffset;

        poseStack.pushPose();
        poseStack.translate(offsetX, offsetY, offsetZ);
        poseStack.mulPose(Axis.YN.rotationDegrees(angle));
        long gameTime = tileEntity.getLevel().getGameTime();
        float bobbingZ = (float) Math.sin((gameTime + tileEntity.hashCode() % 250) / 10f) * 5f;
        float bobbingX = (float) Math.sin(gameTime / 8f) * 2f;
        poseStack.mulPose(Axis.ZN.rotationDegrees(bobbingZ));
        poseStack.mulPose(Axis.XN.rotationDegrees(bobbingX));
        poseStack.scale(scale, scale, scale);
        Minecraft.getInstance().getItemRenderer().renderStatic(SEAL_ITEM_STACK, ItemDisplayContext.FIXED, combinedLightIn, combinedOverlayIn, poseStack, buffer, tileEntity.getLevel(), 0);
        poseStack.popPose();
    }
    public static void renderSealOnPlate(WaystoneBlockEntityBase plateEntity, PoseStack poseStack, MultiBufferSource buffer, int combinedLightIn, int combinedOverlayIn) {
        float scale = 0.33f;
        float offsetY = 0.1f;

        poseStack.pushPose();
        poseStack.translate(0.1f, offsetY, 0.78f);
        poseStack.mulPose(Axis.XP.rotationDegrees(90f));
        poseStack.mulPose(Axis.ZP.rotationDegrees(150f));
        poseStack.scale(scale, scale, scale);
        Minecraft.getInstance().getItemRenderer().renderStatic(SEAL_ITEM_STACK, ItemDisplayContext.FIXED, combinedLightIn, combinedOverlayIn, poseStack, buffer, plateEntity.getLevel(), 0);
        poseStack.popPose();
    }

}
