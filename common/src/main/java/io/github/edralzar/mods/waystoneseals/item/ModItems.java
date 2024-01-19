package io.github.edralzar.mods.waystoneseals.item;


import io.github.edralzar.mods.waystoneseals.Seals;
import net.blay09.mods.balm.api.DeferredObject;
import net.blay09.mods.balm.api.item.BalmItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ModItems {
    public static DeferredObject<CreativeModeTab> creativeModeTab;

    public static BlankSealItem blankSeal;
    public static SealItem seal;
    public static Item sealRemover;

    public static void initialize(BalmItems items) {
        items.registerItem(() -> blankSeal = new BlankSealItem(items.itemProperties()), id("blank_seal"));
        items.registerItem(() -> seal = new SealItem(items.itemProperties()), id("seal"));
        items.registerItem(() -> sealRemover = new SealRemoverItem(items.itemProperties()), id("seal_remover"));

        creativeModeTab = items.registerCreativeModeTab(id(Seals.MOD_ID), () -> new ItemStack(ModItems.blankSeal));
    }

    private static ResourceLocation id(String name) {
        return new ResourceLocation(Seals.MOD_ID, name);
    }
    
}
