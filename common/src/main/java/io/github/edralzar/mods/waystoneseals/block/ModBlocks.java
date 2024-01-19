package io.github.edralzar.mods.waystoneseals.block;

import io.github.edralzar.mods.waystoneseals.Seals;
import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.block.BalmBlocks;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class ModBlocks {

    public static Block sealCraftingTable;

    public static void initialize(BalmBlocks blocks) {
        blocks.register(() -> sealCraftingTable = new SealCraftingTableBlock(defaultProperties()), () -> itemBlock(sealCraftingTable), id("seal_crafting_table"));
    }

    private static BlockItem itemBlock(Block block) {
        return new BlockItem(block, Balm.getItems().itemProperties());
    }

    private static ResourceLocation id(String name) {
        return new ResourceLocation(Seals.MOD_ID, name);
    }

    private static BlockBehaviour.Properties defaultProperties() {
        return Balm.getBlocks().blockProperties().sound(SoundType.STONE).strength(5f, 2000f);
    }
}
