package io.github.edralzar.mods.waystoneseals.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import io.github.edralzar.mods.waystoneseals.item.ModItems;
import net.blay09.mods.waystones.block.entity.WaystoneBlockEntityBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
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

        for (int i = 0; i < 4; i++) {
            float angle = i % 2 == 0 ? 0f : 90f; // For alternating faces (0, 90, 0, 90)
            float offsetX, offsetZ;

            switch (i) {
                case 0: // face: default
                    offsetX = 0.5f;
                    offsetZ = 1f + stoneOffset;
                    break;
                case 1: // face: 90
                    offsetX = 1f + stoneOffset;
                    offsetZ = 0.5f;
                    break;
                case 2: // face: 180
                    offsetX = 0.5f;
                    offsetZ = 0f - stoneOffset;
                    break;
                case 3: // face: 270
                    offsetX = 0f - stoneOffset;
                    offsetZ = 0.5f;
                    break;
                default:
                    offsetX = 0f;
                    offsetZ = 0f;
            }

            poseStack.pushPose();
            poseStack.translate(offsetX, offsetY, offsetZ);
            poseStack.mulPose(Axis.YN.rotationDegrees(angle));
            poseStack.scale(scale, scale, scale);
            Minecraft.getInstance().getItemRenderer().renderStatic(SEAL_ITEM_STACK, ItemDisplayContext.FIXED, combinedLightIn, combinedOverlayIn, poseStack, buffer, tileEntity.getLevel(), 0);
            poseStack.popPose();
        }
    }


}
