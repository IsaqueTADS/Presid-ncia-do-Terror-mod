package dev.presidencia.client;
import dev.presidencia.*;import net.minecraft.client.MinecraftClient;import net.minecraft.client.sound.*;import net.minecraft.sound.SoundCategory;import net.minecraft.util.math.Vec3d;
public final class ClientAudio {
 private static final java.util.List<Playing> ACTIVE=new java.util.ArrayList<>();
 public static void initialize(){AudioSettings.load();net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents.CLIENT_STOPPING.register(c->AudioSettings.save());net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.registerGlobalReceiver(ModSoundPacket.ID,(packet,context)->context.client().execute(()->play(packet,false)));net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.END_CLIENT_TICK.register(c->ACTIVE.removeIf(s->s.isDone()||(s.age>10&&!c.getSoundManager().isPlaying(s))));net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents.DISCONNECT.register((handler,c)->stop());}
 public static void stopTests(){var c=MinecraftClient.getInstance();for(var s:ACTIVE)if(s.test)c.getSoundManager().stop(s);ACTIVE.removeIf(s->s.test);}
 public static void stop(){var c=MinecraftClient.getInstance();for(var s:ACTIVE)c.getSoundManager().stop(s);ACTIVE.clear();}
 public static void play(ModSoundPacket packet,boolean test){var c=MinecraftClient.getInstance();if(c.world==null||c.player==null||packet.channel()<0||packet.channel()>2)return;if(packet.priority()&&packet.channel()==0){for(var s:ACTIVE)if(s.packet.channel()==0)c.getSoundManager().stop(s);ACTIVE.removeIf(s->s.packet.channel()==0);}var sound=new Playing(packet,test);if(ACTIVE.size()>=96)c.getSoundManager().stop(ACTIVE.removeFirst());ACTIVE.add(sound);c.getSoundManager().play(sound);}
 public static void test(int channel){var c=MinecraftClient.getInstance();if(c.player==null)return;String sound=channel==0?"presidencia_terror:lula_beer":channel==1?"presidencia_terror:trex_voice":"minecraft:entity.generic.explode";play(new ModSoundPacket(net.minecraft.util.Identifier.of(sound),channel,c.player.getX(),c.player.getY(),c.player.getZ(),1,1,true),true);}
 private static final class Playing extends AbstractSoundInstance implements TickableSoundInstance {
  final ModSoundPacket packet;final boolean test;final net.minecraft.client.world.ClientWorld world;int age;boolean done;
  Playing(ModSoundPacket p,boolean test){super(p.sound(),SoundCategory.MASTER,net.minecraft.util.math.random.Random.create());packet=p;this.test=test;world=MinecraftClient.getInstance().world;x=p.x();y=p.y();z=p.z();pitch=Math.clamp(p.pitch(),.5f,2);attenuationType=SoundInstance.AttenuationType.NONE;relative=test;update();}
  private void update(){var c=MinecraftClient.getInstance();if(c.player==null||c.world!=world){done=true;return;}double distance=test?0:c.player.getEyePos().distanceTo(new Vec3d(x,y,z));volume=(float)Math.clamp(packet.volume()*AudioSettings.get().gain(packet.channel())*ModAudio.attenuation(packet.channel(),distance),0,1);if(test){x=0;y=0;z=0;}}
  public boolean isDone(){return done;}
  public void tick(){age++;if(age>1200)done=true;update();}
 }
}
