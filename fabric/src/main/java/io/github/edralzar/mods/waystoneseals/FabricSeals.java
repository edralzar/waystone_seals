package io.github.edralzar.mods.waystoneseals;

import net.blay09.mods.balm.api.Balm;
import net.fabricmc.api.ModInitializer;

public class FabricSeals implements ModInitializer {
    
    @Override
    public void onInitialize() {
        CommonClass.init();
        Balm.initialize(Seals.MOD_ID, Seals::initialize);
    }
}
