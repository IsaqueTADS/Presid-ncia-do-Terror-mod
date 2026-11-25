package dev.presidencia;
import net.minecraft.world.World;import net.minecraft.server.world.ServerWorld;import net.minecraft.util.math.Vec3d;import net.minecraft.sound.SoundEvent;import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
public final class ModAudio {
 public static final int VOICE=0,MOUNT=1,WEAPON=2;
 public static double attenuation(int channel,double distance){double near=channel==VOICE?24:4,far=channel==VOICE?64:40;return Math.clamp((far-distance)/(far-near),0,1);}
 public static void play(World world,Vec3d at,SoundEvent sound,int channel,float volume,float pitch){play(world,at,sound,channel,volume,pitch,false);}
 public static void play(World world,Vec3d at,SoundEvent sound,int channel,float volume,float pitch,boolean priority){if(!(world instanceof ServerWorld w))return;double range=channel==VOICE?64:40;var packet=new ModSoundPacket(sound.getId(),channel,at.x,at.y,at.z,volume,pitch,priority);for(var p:w.getPlayers())if(p.squaredDistanceTo(at)<=range*range&&p.networkHandler!=null&&ServerPlayNetworking.canSend(p,ModSoundPacket.ID))ServerPlayNetworking.send(p,packet);}
}
