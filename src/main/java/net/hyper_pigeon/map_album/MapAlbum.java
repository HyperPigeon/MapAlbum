package net.hyper_pigeon.map_album;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.hyper_pigeon.map_album.items.AlbumItem;
import net.hyper_pigeon.map_album.screens.inventory.AlbumInventoryScreenHandler;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

public class MapAlbum implements ModInitializer {

    public static final AlbumItem ALBUM_ITEM = new AlbumItem(new Item.Settings().maxCount(1));
    public static final ScreenHandlerType<AlbumInventoryScreenHandler> ALBUM_INVENTORY_SCREEN_HANDLER = ScreenHandlerRegistry.registerSimple(new Identifier("map_album","album_inventory"), (syncId, playerInventory) -> new AlbumInventoryScreenHandler(syncId, playerInventory, ALBUM_ITEM.getDefaultStack().copy()));

    @Override
    public void onInitialize() {
        Registry.register(Registries.ITEM,new Identifier("map_album", "album"), ALBUM_ITEM);
        ItemGroupEvents
                .modifyEntriesEvent(ItemGroups.TOOLS)
                .register((itemGroup) -> itemGroup.add(ALBUM_ITEM));
    }
}
