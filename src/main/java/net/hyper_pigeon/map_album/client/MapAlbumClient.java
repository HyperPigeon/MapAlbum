package net.hyper_pigeon.map_album.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.hyper_pigeon.map_album.MapAlbum;
import net.hyper_pigeon.map_album.networking.MapStatePayload;
import net.hyper_pigeon.map_album.screens.album.AlbumScreen;
import net.hyper_pigeon.map_album.screens.inventory.AlbumInventoryScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.component.type.MapIdComponent;
import net.minecraft.item.map.MapState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.text.Text;
import net.minecraft.util.Pair;

import java.util.ArrayList;

@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public class MapAlbumClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        HandledScreens.register(MapAlbum.ALBUM_INVENTORY_SCREEN_HANDLER, AlbumInventoryScreen::new);
        ClientPlayNetworking.registerGlobalReceiver(MapStatePayload.PACKET_ID, (payload, context) -> {
            var client = context.client();
            NbtCompound nbtCompound = payload.imageNBT();
            NbtList nbtList = nbtCompound.getList("MapInformation", NbtElement.COMPOUND_TYPE);
            ArrayList<Pair<Pair<MapIdComponent, String>, MapState>> pairArrayList = new ArrayList<>();
            for(int i = 0; i < nbtList.size(); ++i) {
                NbtCompound mapInfoCompound = nbtList.getCompound(i);
                pairArrayList.add(new Pair<>(new Pair<>(new MapIdComponent(mapInfoCompound.getInt("id")), mapInfoCompound.getString("Name")),
                        MapState.fromNbt(mapInfoCompound.getCompound("MapState"), client.world.getRegistryManager())));
            }
            if (!(client.currentScreen instanceof AlbumScreen)) {
                client.setScreen(new AlbumScreen(Text.of("Album"), pairArrayList));
            }
        });
    }
}
