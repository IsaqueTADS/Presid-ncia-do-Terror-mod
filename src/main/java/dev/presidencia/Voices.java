package dev.presidencia;
import java.util.*;import net.minecraft.server.MinecraftServer;import net.minecraft.server.world.ServerWorld;import net.minecraft.sound.SoundCategory;
public final class Voices {
 private static final Map<MinecraftServer,Boolean> SPEAKER=new WeakHashMap<>();
 private static final Map<MinecraftServer,Long> BUSY=new WeakHashMap<>(),NEXT=new WeakHashMap<>();private static final Map<MinecraftServer,String> LAST=new WeakHashMap<>();
 public static void clear(MinecraftServer s){BUSY.remove(s);NEXT.remove(s);LAST.remove(s);SPEAKER.remove(s);}
 public static boolean play(President p,String name,boolean priority){var s=p.getServer();long now=s.getOverworld().getTime();
  if(!priority&&now<BUSY.getOrDefault(s,0L))return false;
  ModAudio.play(p.getWorld(),p.getPos().add(0,Math.min(2.5,p.getHeight()*.7),0),TerrorSounds.get(name),ModAudio.VOICE,TerrorConfig.get().voiceVolume,1,priority);BUSY.put(s,now+160);LAST.put(s,name);SPEAKER.put(s,p.lula());if(priority)NEXT.put(s,now+20L*TerrorConfig.get().voiceMinSeconds);return true;
 }
 public static void ambient(President p){var s=p.getServer();long now=s.getOverworld().getTime();if(now<NEXT.getOrDefault(s,0L))return;if(java.util.Objects.equals(SPEAKER.get(s),p.lula())&&p.getWorld().getEntitiesByClass(President.class,p.getBoundingBox().expand(32),other->other!=p&&other.isAlive()&&other.lula()!=p.lula()).size()>0)return;String[] pool=p.lula()?new String[]{"lula_food","lula_beer","lula_taunt"}:new String[]{"bolsonaro_phone","bolsonaro_laugh","bolsonaro_taunt"};String previous=LAST.get(s);for(int i=0;i<pool.length;i++){String name=pool[(p.age/20+i)%pool.length];if(!name.equals(previous)&&play(p,name,false)){var c=TerrorConfig.get();NEXT.put(s,now+20L*(c.voiceMinSeconds+p.getRandom().nextInt(c.voiceMaxSeconds-c.voiceMinSeconds+1)));break;}}
 }
}
