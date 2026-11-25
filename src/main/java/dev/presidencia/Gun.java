package dev.presidencia;
import net.minecraft.item.*;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.*;
import net.minecraft.util.hit.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;
import net.minecraft.sound.*;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import java.util.List;
public class Gun extends Item {
 private static final java.util.Map<java.util.UUID,ItemStack> ACTIVE=new java.util.HashMap<>();
 public static void tickReloads(net.minecraft.server.MinecraftServer server){ACTIVE.entrySet().removeIf(e->{var p=server.getPlayerManager().getPlayer(e.getKey());if(p==null||!p.isAlive()||p.getMainHandStack()!=e.getValue()){cancel(e.getValue());return true;}return false;});}
 public final boolean bazooka;
 public Gun(boolean bazooka){super(new Settings().maxCount(1));this.bazooka=bazooka;}
 public int capacity(){return bazooka?1:6;}
 public int reloadTicks(){return bazooka?48:56;}
 public static NbtCompound data(ItemStack s){return s.getOrDefault(DataComponentTypes.CUSTOM_DATA,NbtComponent.DEFAULT).copyNbt();}
 public static void put(ItemStack s,NbtCompound n){s.set(DataComponentTypes.CUSTOM_DATA,NbtComponent.of(n));}
 public static int rounds(ItemStack s){return Math.max(0,Math.min(s.getItem() instanceof Gun g?g.capacity():0,data(s).getInt("Rounds")));}
 public static void rounds(ItemStack s,int amount){var n=data(s);n.putInt("Rounds",Math.clamp(amount,0,((Gun)s.getItem()).capacity()));put(s,n);}
 public static long reloadEnd(ItemStack s){return data(s).getLong("ReloadEnd");}
 public static long firedAt(ItemStack s){var n=data(s);return n.contains("FiredAt")?n.getLong("FiredAt"):-1000;}
 public Item ammo(){return bazooka?Terror.MISSILE:Terror.BULLET;}
 public void reload(PlayerEntity p,ItemStack s){
  if(p.getWorld().isClient||reloadEnd(s)>0||rounds(s)>=capacity())return;
  if(!p.isCreative()&&p.getInventory().count(ammo())==0)return;
  var n=data(s);n.putLong("ReloadEnd",p.getWorld().getTime()+reloadTicks());put(s,n);ACTIVE.put(p.getUuid(),s);
  ModAudio.play(p.getWorld(),p.getPos(),TerrorSounds.get("reload"),ModAudio.WEAPON,.8f,1);
 }
 @Override public void inventoryTick(ItemStack s,World w,Entity e,int slot,boolean selected){
  if(w.isClient||!(e instanceof PlayerEntity p)||reloadEnd(s)==0)return;
  if(!selected||p.getMainHandStack()!=s||!p.isAlive()||ACTIVE.get(p.getUuid())!=s){cancel(s);return;}
  if(w.getTime()<reloadEnd(s))return;
  int add=capacity()-rounds(s);if(!p.isCreative()){add=Math.min(add,p.getInventory().count(ammo()));int remaining=add;for(int i=0;i<p.getInventory().size()&&remaining>0;i++){var a=p.getInventory().getStack(i);if(a.isOf(ammo())){int take=Math.min(remaining,a.getCount());a.decrement(take);remaining-=take;}}}
  boolean full=rounds(s)==0&&add==capacity();rounds(s,rounds(s)+add);var cycle=data(s);cycle.putBoolean("FullCycle",full);cycle.putInt("CycleShots",0);put(s,cycle);cancel(s);ACTIVE.remove(p.getUuid());ModAudio.play(w,p.getPos(),SoundEvents.BLOCK_IRON_TRAPDOOR_CLOSE,ModAudio.WEAPON,.7f,1.2f);
 }
 private static void cancel(ItemStack s){var n=data(s);n.remove("ReloadEnd");put(s,n);}
 @Override public TypedActionResult<ItemStack> use(World w,PlayerEntity p,Hand h){
  var s=p.getStackInHand(h);if(h!=Hand.MAIN_HAND)return TypedActionResult.fail(s);
  if(w.isClient)return TypedActionResult.success(s);
  if(reloadEnd(s)>0||p.getItemCooldownManager().isCoolingDown(this))return TypedActionResult.fail(s);
  if(rounds(s)==0){reload(p,s);return TypedActionResult.consume(s);}
  boolean last=!bazooka&&lastBonus(s);
  if(!p.isCreative()){var cycle=data(s);cycle.putInt("CycleShots",cycle.getInt("CycleShots")+1);put(s,cycle);rounds(s,rounds(s)-1);}
  var n=data(s);n.putLong("FiredAt",w.getTime());put(s,n);
  p.getItemCooldownManager().set(this,bazooka?20:8);
  if(bazooka)Rocket.launch((ServerWorld)w,p,p.getEyePos(),p.getRotationVec(1));
  else {
   Vec3d start=p.getEyePos(),end=start.add(p.getRotationVec(1).multiply(64));
   var wall=w.raycast(new RaycastContext(start,end,RaycastContext.ShapeType.COLLIDER,RaycastContext.FluidHandling.NONE,p));end=wall.getPos();
   var hit=ProjectileUtilBridge.hit(p,start,end);
   if(hit!=null){
    if(hit.getEntity() instanceof LivingEntity victim){boolean hs=HeadRig.isHead(victim,start,hit.getPos().add(p.getRotationVec(1).multiply(.6)));float before=victim.getHealth();float damage=shotDamage(hs,last,victim instanceof President president&&president.lula());
     boolean accepted=victim.damage(w.getDamageSources().playerAttack(p),damage);
     if(accepted&&hs&&last&&victim instanceof President president&&president.lula()&&victim.isAlive()){victim.setHealth(Math.max(0,before-shotDamage(true,true,true)));if(victim.getHealth()<=0)victim.onDeath(w.getDamageSources().playerAttack(p));}
     if(accepted)DamageNumbers.show(victim,before-victim.getHealth(),hit.getPos(),hs,last);
    }else hit.getEntity().damage(w.getDamageSources().playerAttack(p),12);end=hit.getPos();}
   Vec3d segment=end.subtract(start);int marks=Math.max(1,(int)(segment.length()*5));
   for(int i=1;i<=marks;i++){Vec3d at=start.add(segment.multiply((double)i/marks));((ServerWorld)w).spawnParticles(new net.minecraft.particle.DustParticleEffect(new org.joml.Vector3f(1f,.8f,.25f),.55f),at.x,at.y,at.z,1,0,0,0,0);}
   ((ServerWorld)w).spawnParticles(net.minecraft.particle.ParticleTypes.CRIT,end.x,end.y,end.z,5,.07,.07,.07,.03);
   if(hit!=null||wall.getType()!=HitResult.Type.MISS)ModAudio.play(w,end,TerrorSounds.get("impact"),ModAudio.WEAPON,.7f,1.4f);
   ((ServerWorld)w).spawnParticles(net.minecraft.particle.ParticleTypes.CRIT,start.x,start.y,start.z,8,.1,.1,.1,.2);
  }
  ModAudio.play(w,p.getPos(),bazooka?SoundEvents.ENTITY_FIREWORK_ROCKET_LAUNCH:SoundEvents.ENTITY_GENERIC_EXPLODE.value(),ModAudio.WEAPON,bazooka?1f:.65f,bazooka?.8f:1.8f);
  return TypedActionResult.consume(s);
 }
 public static boolean lastBonus(ItemStack s){var n=data(s);return rounds(s)==1&&n.getBoolean("FullCycle")&&n.getInt("CycleShots")==5;}
 public static float shotDamage(boolean hs,boolean last,boolean lula){return hs&&last&&lula?22:(hs?17:12)*(last?1.5f:1);}
 private static String label(boolean hs,boolean last,boolean lula){return String.format(java.util.Locale.ROOT,"%.1f",shotDamage(hs,last,lula)).replaceAll("\\.0$","").replace('.',',');}
 @Override public void appendTooltip(ItemStack stack,Item.TooltipContext context,List<Text> lines,net.minecraft.item.tooltip.TooltipType type){
  if(bazooka){lines.add(Text.literal("Explosão: até "+(int)Rocket.MAX_DAMAGE+" de dano; varia com distância e cobertura."));lines.add(Text.literal("Explosões podem destruir blocos (configurável)."));}
  else{lines.add(Text.literal("Dano: "+label(false,false,false)+" | Headshot: "+label(true,false,false)));lines.add(Text.literal("6º disparo de tambor completo: +50% de dano"));lines.add(Text.literal("Último tiro: corpo "+label(false,true,false)+" | HS "+label(true,true,false)));lines.add(Text.literal("Exceção: último HS no Lula = "+label(true,true,true)));lines.add(Text.literal("Recarga parcial interrompe a sequência do bônus."));}
 }
 @Override public boolean isItemBarVisible(ItemStack s){return true;}
 @Override public int getItemBarStep(ItemStack s){return Math.round(13f*rounds(s)/capacity());}
 @Override public int getItemBarColor(ItemStack s){return 0xe8c547;}
}
