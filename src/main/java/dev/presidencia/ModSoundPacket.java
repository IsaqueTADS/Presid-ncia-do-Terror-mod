package dev.presidencia;
import net.minecraft.network.RegistryByteBuf;import net.minecraft.network.codec.PacketCodec;import net.minecraft.network.packet.CustomPayload;import net.minecraft.util.Identifier;
public record ModSoundPacket(Identifier sound,int channel,double x,double y,double z,float volume,float pitch,boolean priority) implements CustomPayload {
 public static final Id<ModSoundPacket> ID=new Id<>(Terror.id("audio"));
 public static final PacketCodec<RegistryByteBuf,ModSoundPacket> CODEC=new PacketCodec<>(){
  public void encode(RegistryByteBuf b,ModSoundPacket p){b.writeIdentifier(p.sound);b.writeVarInt(p.channel);b.writeDouble(p.x);b.writeDouble(p.y);b.writeDouble(p.z);b.writeFloat(p.volume);b.writeFloat(p.pitch);b.writeBoolean(p.priority);}
  public ModSoundPacket decode(RegistryByteBuf b){return new ModSoundPacket(b.readIdentifier(),b.readVarInt(),b.readDouble(),b.readDouble(),b.readDouble(),b.readFloat(),b.readFloat(),b.readBoolean());}
 };
 public Id<? extends CustomPayload> getId(){return ID;}
}
