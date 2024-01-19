package io.github.edralzar.mods.waystoneseals.block.entity;

import net.blay09.mods.balm.api.block.entity.CustomRenderBoundingBox;
import net.blay09.mods.balm.common.BalmBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class SealCraftingTableBlockEntity extends BalmBlockEntity implements CustomRenderBoundingBox {

    public SealCraftingTableBlockEntity(BlockPos worldPosition, BlockState state) {
        super(ModBlockEntities.sealCraftingTable.get(), worldPosition, state);
    }

    @Override
    public AABB getRenderBoundingBox() {
        return new AABB(worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), worldPosition.getX() + 1, worldPosition.getY() + 2, worldPosition.getZ() + 1);
    }

}
