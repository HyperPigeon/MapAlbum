package net.hyper_pigeon.map_album.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.hyper_pigeon.map_album.MapAlbum;
import net.hyper_pigeon.map_album.networking.MapAlbumNetworkingConstants;
import net.hyper_pigeon.map_album.screens.album.AlbumScreen;
import net.hyper_pigeon.map_album.screens.inventory.AlbumInventoryScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
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
        ClientPlayNetworking.registerGlobalReceiver(MapAlbumNetworkingConstants.SEND_MAP_STATE,(client, handler, buf, responseSender) -> {
            NbtCompound nbtCompound = buf.readNbt();
            client.execute(() -> {
                NbtList nbtList = nbtCompound.getList("MapInformation", NbtElement.COMPOUND_TYPE);
                ArrayList<Pair<Integer, MapState>> pairArrayList = new ArrayList<>();
                for(int i = 0; i < nbtList.size(); ++i) {
                    NbtCompound mapInfoCompound = nbtList.getCompound(i);
                    pairArrayList.add(new Pair<Integer, MapState>(mapInfoCompound.getInt("id"),
                            MapState.fromNbt(mapInfoCompound.getCompound("MapState"))));
                }
                MinecraftClient mc = MinecraftClient.getInstance();
                if (!(mc.currentScreen instanceof AlbumScreen)) {
                    mc.setScreen(new AlbumScreen(Text.of("Album"), pairArrayList));
                }
            });
        });
    }
}
