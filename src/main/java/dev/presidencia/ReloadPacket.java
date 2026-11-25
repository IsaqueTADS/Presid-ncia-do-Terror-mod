package dev.presidencia;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
public record ReloadPacket() implements CustomPayload {
 public static final Id<ReloadPacket> ID=new Id<>(Terror.id("reload"));
 public static final PacketCodec<RegistryByteBuf,ReloadPacket> CODEC=PacketCodec.unit(new ReloadPacket());
 public Id<? extends CustomPayload> getId(){return ID;}
}
