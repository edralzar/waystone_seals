package io.github.edralzar.mods.waystoneseals.tag;

import io.github.edralzar.mods.waystoneseals.Seals;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class ModItemTags {

    public static final TagKey<Item> USEABLE_ON_WAYSTONES = TagKey.create(Registries.ITEM, new ResourceLocation(Seals.MOD_ID, "useable_on_waystones"));

}
