package io.github.edralzar.mods.waystoneseals;

import io.github.edralzar.mods.waystoneseals.block.ModBlocks;
import io.github.edralzar.mods.waystoneseals.block.entity.ModBlockEntities;
import io.github.edralzar.mods.waystoneseals.item.ModItems;
import net.blay09.mods.balm.api.Balm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Seals {
    public static final String MOD_ID = "waystoneseals";
    public static final String MOD_NAME = "Waystone Seals";
    public static final Logger LOG = LoggerFactory.getLogger(MOD_NAME);

    public static void initialize() {
        ModBlocks.initialize(Balm.getBlocks());
        ModBlockEntities.initialize(Balm.getBlockEntities());
        ModItems.initialize(Balm.getItems());
//        ModRecipes.initialize(Balm.getRecipes());
    }
}
