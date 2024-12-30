package net.hyper_pigeon.map_album.networking;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public record MapStatePayload(NbtCompound imageNBT) implements CustomPayload {

    public static final CustomPayload.Id<MapStatePayload> PACKET_ID = new CustomPayload.Id<>(MapAlbumNetworkingConstants.SEND_MAP_STATE);
    public static final PacketCodec<RegistryByteBuf, MapStatePayload> PACKET_CODEC = CustomPayload.codecOf(MapStatePayload::write,MapStatePayload::new);

    private MapStatePayload(PacketByteBuf buf) {
        this(buf.readNbt());
    }

    public MapStatePayload(NbtCompound imageNBT) {
        this.imageNBT = imageNBT;
    }

    private void write(RegistryByteBuf registryByteBuf) {
        registryByteBuf.writeNbt(imageNBT);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }
}
