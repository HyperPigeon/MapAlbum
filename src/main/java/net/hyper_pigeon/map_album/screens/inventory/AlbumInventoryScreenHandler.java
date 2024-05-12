package net.hyper_pigeon.map_album.screens.inventory;

import net.hyper_pigeon.map_album.MapAlbum;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.collection.DefaultedList;

public class AlbumInventoryScreenHandler extends ScreenHandler {

    private final ItemStack album;
    public final Inventory albumInv = new SimpleInventory(54);

    public AlbumInventoryScreenHandler(int syncId, PlayerInventory playerInventory, ItemStack album) {
        super(MapAlbum.ALBUM_INVENTORY_SCREEN_HANDLER, syncId);
        this.album = album;
        int k;
        int j;
        this.readNBT();
        this.albumInv.onOpen(playerInventory.player);
        playerInventory.player.getWorld().playSoundFromEntity(null, playerInventory.player, SoundEvents.ITEM_BOOK_PAGE_TURN, SoundCategory.PLAYERS, 0.5f, playerInventory.player.getRandom().nextFloat() * 0.1f + 0.9f);
        for (j = 0; j < 6; ++j) {
            for (k = 0; k < 9; ++k) {
                this.addSlot(new Slot(this.albumInv, k + j * 9, 8 + k * 18, 18 + j * 18) {
                    @Override
                    public boolean canInsert(ItemStack stack) {
                        return stack.isOf(Items.FILLED_MAP);
                    }
                });
            }
        }

        for (j = 0; j < 3; ++j) {
            for (k = 0; k < 9; ++k) {
                this.addSlot(new Slot(playerInventory, k + j * 9 + 9, 8 + k * 18, 139 + j * 18));
            }
        }
        for (j = 0; j < 9; ++j) {
            this.addSlot(new Slot(playerInventory, j, 8 + j * 18, 197));
        }
    }

    private void readNBT() {
        var stacks = DefaultedList.ofSize(54, ItemStack.EMPTY);
        if (this.album.hasNbt() && this.album.getNbt() != null) {
            Inventories.readNbt(this.album.getNbt(), stacks);
            for (int i = 0; i < stacks.size(); i++) {
                this.albumInv.setStack(i, stacks.get(i));
            }
        }
    }


    private void writeNBT() {
        var stacks = DefaultedList.ofSize(54, ItemStack.EMPTY);
        for (int i = 0; i < stacks.size(); i++) {
            var item = this.albumInv.getStack(i);
            stacks.set(i, item);
        }
        Inventories.writeNbt(this.album.getOrCreateNbt(), stacks, true);
    }


    @Override
    public ItemStack transferSlot(PlayerEntity player, int index) {
        var itemStack = ItemStack.EMPTY;
        var slot = this.slots.get(index);
        if (slot.hasStack()) {
            var itemStack2 = slot.getStack();
            itemStack = itemStack2.copy();
            if (index < 54 ? !this.insertItem(itemStack2, 54, this.slots.size(), true) : !this.insertItem(itemStack2, 0, 54, false)) {
                return ItemStack.EMPTY;
            }
            if (itemStack2.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }
        }
        return itemStack;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        for (var hand : Hand.values()) {
            if (player.getStackInHand(hand) == this.album) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void close(PlayerEntity player) {
        super.close(player);
        this.albumInv.onClose(player);
        player.getWorld().playSoundFromEntity(null, player, SoundEvents.ITEM_BOOK_PAGE_TURN, SoundCategory.PLAYERS, 0.5f, player.getRandom().nextFloat() * 0.1f + 0.9f);
    }

    @Override
    public void sendContentUpdates() {
        this.writeNBT();
        super.sendContentUpdates();
    }

}
