package net.hyper_pigeon.map_album;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.hyper_pigeon.map_album.items.AlbumItem;
import net.hyper_pigeon.map_album.screens.inventory.AlbumInventoryScreenHandler;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

public class MapAlbum implements ModInitializer {

    public static final AlbumItem ALBUM_ITEM = new AlbumItem(new Item.Settings().maxCount(1));
    public static final ScreenHandlerType<AlbumInventoryScreenHandler> ALBUM_INVENTORY_SCREEN_HANDLER = Registry.register(Registries.SCREEN_HANDLER, Identifier.of("map_album", "album_inventory"), new ScreenHandlerType<>(AlbumInventoryScreenHandler::new, FeatureSet.empty()));

    @Override
    public void onInitialize() {
        Registry.register(Registries.ITEM,Identifier.of("map_album", "album"), ALBUM_ITEM);
        ItemGroupEvents
                .modifyEntriesEvent(ItemGroups.TOOLS)
                .register((itemGroup) -> itemGroup.add(ALBUM_ITEM));
    }
}
