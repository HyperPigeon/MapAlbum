package net.hyper_pigeon.map_album.items;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.hyper_pigeon.map_album.networking.MapAlbumNetworkingConstants;
import net.hyper_pigeon.map_album.screens.inventory.AlbumInventoryScreenHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class AlbumItem extends Item{

    public AlbumItem(Settings settings) {
        super(settings);
    }

    public NbtCompound writeMapStatesToNbt(NbtCompound albumNbt, World world){
        var mapStacks = DefaultedList.ofSize(54, ItemStack.EMPTY);
        Inventories.readNbt(albumNbt,mapStacks);
        NbtList nbtList = new NbtList();
        for(int i = 0; i < mapStacks.size(); ++i) {
            ItemStack itemStack = mapStacks.get(i);
            if (itemStack.isOf(Items.FILLED_MAP)) {
                int id = FilledMapItem.getMapId(itemStack);
                NbtCompound nbtCompound = new NbtCompound();
                nbtCompound.putInt("id", id);
                nbtCompound.putString("Name", itemStack.getName().getString());
                nbtCompound.put("MapState", FilledMapItem.getMapState(id, world).writeNbt(new NbtCompound()));
                nbtList.add(nbtCompound);
            }
        }

        NbtCompound nbtCompound = new NbtCompound();
        nbtCompound.put("MapInformation", nbtList);
        return nbtCompound;
    }


    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        var stack = user.getStackInHand(hand);
        if (!world.isClient) {
            if(user.isSneaking()) {
                PacketByteBuf packetByteBuf = PacketByteBufs.create();
                packetByteBuf.writeNbt(writeMapStatesToNbt(stack.getOrCreateNbt(),world));
                ServerPlayNetworking.send((ServerPlayerEntity) user, MapAlbumNetworkingConstants.SEND_MAP_STATE,packetByteBuf);
            }
            else {
                this.openMenu((ServerPlayerEntity) user, new NamedScreenHandlerFactory() {
                    @Override
                    public Text getDisplayName() {
                        return stack.getName();
                    }

                    @Override
                    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
                        return new AlbumInventoryScreenHandler(syncId, playerInventory, stack);
                    }
                });
            }
        }
        return TypedActionResult.success(user.getStackInHand(hand));
    }

    public void openMenu(ServerPlayerEntity player, NamedScreenHandlerFactory menu) {
        var menuProvider = new NamedScreenHandlerFactory() {
            @Nullable
            @Override
            public ScreenHandler createMenu(int id, PlayerInventory inventory, PlayerEntity player) {
                return menu.createMenu(id, inventory, player);
            }
            @Override
            public Text getDisplayName() {
                return menu.getDisplayName();
            }
        };
        player.openHandledScreen(menuProvider);
    }
}
