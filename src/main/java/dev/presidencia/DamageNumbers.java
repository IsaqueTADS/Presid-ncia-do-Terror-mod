package dev.presidencia;
import java.util.*;
import net.minecraft.entity.*;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
/** Vanilla text displays use the Minecraft font and server-authoritative health loss. */
public final class DamageNumbers {
 public static boolean creating;
 private record Number(DisplayEntity.TextDisplayEntity entity,long end){}
 private static final Map<MinecraftServer,List<Number>> ACTIVE=new WeakHashMap<>();
 public static void show(LivingEntity victim,float amount,Vec3d pos,boolean hs,boolean last){
  if(!(victim.getWorld() instanceof ServerWorld w)||amount<.01)return;
  var list=ACTIVE.computeIfAbsent(w.getServer(),s->new ArrayList<>());if(list.size()>=96){list.removeFirst().entity.discard();}
  var e=new DisplayEntity.TextDisplayEntity(EntityType.TEXT_DISPLAY,w);e.setBillboardMode(DisplayEntity.BillboardMode.CENTER);
  String value=String.format(java.util.Locale.ROOT,"%.1f",amount).replaceAll("\\.0$","");
  e.setText(Text.literal(value+(hs?" HS":"")).withColor(hs?0xff6868:last?0xffd050:0xffffff));e.setBackground(0);e.setDisplayFlags(DisplayEntity.TextDisplayEntity.SHADOW_FLAG);e.setPosition(pos);e.addCommandTag("presidencia_damage_number");creating=true;try{w.spawnEntity(e);}finally{creating=false;}list.add(new Number(e,w.getTime()+22));
 }
 public static void clear(MinecraftServer s){var list=ACTIVE.remove(s);if(list!=null)for(var n:list)n.entity.discard();}
 public static void tick(MinecraftServer server){var list=ACTIVE.get(server);if(list==null)return;list.removeIf(n->{if(n.entity.isRemoved()||n.entity.getWorld().getTime()>=n.end){n.entity.discard();return true;}n.entity.setPosition(n.entity.getPos().add(0,.025,0));return false;});}
}
