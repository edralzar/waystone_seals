package io.github.edralzar.mods.waystoneseals.client;

import io.github.edralzar.mods.waystoneseals.client.rendering.ModRenderers;
import net.blay09.mods.balm.api.client.BalmClient;

public class SealsClient {

    public static void initialize() {
        ModRenderers.initialize(BalmClient.getRenderers());
    }
}
