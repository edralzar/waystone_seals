package io.github.edralzar.mods.waystoneseals;

import io.github.edralzar.mods.waystoneseals.CommonClass;
import io.github.edralzar.mods.waystoneseals.Constants;
import net.minecraftforge.fml.common.Mod;

@Mod(Constants.MOD_ID)
public class ExampleMod {
    
    public ExampleMod() {
    
        // This method is invoked by the Forge mod loader when it is ready
        // to load your mod. You can access Forge and Common code in this
        // project.
    
        // Use Forge to bootstrap the Common mod.
        Constants.LOG.info("Hello Forge world!");
        CommonClass.init();
        
    }
}