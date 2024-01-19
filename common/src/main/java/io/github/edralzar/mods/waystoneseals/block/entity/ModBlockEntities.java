package io.github.edralzar.mods.waystoneseals.block.entity;

import io.github.edralzar.mods.waystoneseals.Seals;
import io.github.edralzar.mods.waystoneseals.block.ModBlocks;
import net.blay09.mods.balm.api.DeferredObject;
import net.blay09.mods.balm.api.block.BalmBlockEntities;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class ModBlockEntities {
    public static DeferredObject<BlockEntityType<SealCraftingTableBlockEntity>> sealCraftingTable;

    public static void initialize(BalmBlockEntities blockEntities) {
        sealCraftingTable = blockEntities.registerBlockEntity(id("seal_crafting_table"), SealCraftingTableBlockEntity::new, () -> new Block[]{ModBlocks.sealCraftingTable});
    }

    private static ResourceLocation id(String name) {
        return new ResourceLocation(Seals.MOD_ID, name);
    }

}
