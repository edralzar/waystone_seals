package io.github.edralzar.mods.waystoneseals.client;

import io.github.edralzar.mods.waystoneseals.Seals;
import net.blay09.mods.balm.api.client.BalmClient;
import net.fabricmc.api.ClientModInitializer;

public class FabricSealsClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        BalmClient.initialize(Seals.MOD_ID, SealsClient::initialize);
    }
}
