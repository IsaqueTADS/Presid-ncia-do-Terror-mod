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
  p.getWorld().playSound(null,p.getBlockPos(),SoundEvents.BLOCK_IRON_TRAPDOOR_OPEN,SoundCategory.PLAYERS,.6f,1.3f);
 }
 @Override public void inventoryTick(ItemStack s,World w,Entity e,int slot,boolean selected){
  if(w.isClient||!(e instanceof PlayerEntity p)||reloadEnd(s)==0)return;
  if(!selected||p.getMainHandStack()!=s||!p.isAlive()||ACTIVE.get(p.getUuid())!=s){cancel(s);return;}
  if(w.getTime()<reloadEnd(s))return;
  int add=capacity()-rounds(s);if(!p.isCreative()){add=Math.min(add,p.getInventory().count(ammo()));int remaining=add;for(int i=0;i<p.getInventory().size()&&remaining>0;i++){var a=p.getInventory().getStack(i);if(a.isOf(ammo())){int take=Math.min(remaining,a.getCount());a.decrement(take);remaining-=take;}}}
  rounds(s,rounds(s)+add);cancel(s);ACTIVE.remove(p.getUuid());w.playSound(null,p.getBlockPos(),SoundEvents.BLOCK_IRON_TRAPDOOR_CLOSE,SoundCategory.PLAYERS,.7f,1.2f);
 }
 private static void cancel(ItemStack s){var n=data(s);n.remove("ReloadEnd");put(s,n);}
 @Override public TypedActionResult<ItemStack> use(World w,PlayerEntity p,Hand h){
  var s=p.getStackInHand(h);if(h!=Hand.MAIN_HAND)return TypedActionResult.fail(s);
  if(w.isClient)return TypedActionResult.success(s);
  if(reloadEnd(s)>0||p.getItemCooldownManager().isCoolingDown(this))return TypedActionResult.fail(s);
  if(rounds(s)==0){reload(p,s);return TypedActionResult.consume(s);}
  if(!p.isCreative())rounds(s,rounds(s)-1);
  var n=data(s);n.putLong("FiredAt",w.getTime());put(s,n);
  p.getItemCooldownManager().set(this,bazooka?20:8);
  if(bazooka)Rocket.launch((ServerWorld)w,p,p.getEyePos(),p.getRotationVec(1));
  else {
   Vec3d start=p.getEyePos(),end=start.add(p.getRotationVec(1).multiply(64));
   var wall=w.raycast(new RaycastContext(start,end,RaycastContext.ShapeType.COLLIDER,RaycastContext.FluidHandling.NONE,p));end=wall.getPos();
   var hit=ProjectileUtilBridge.hit(p,start,end);
   if(hit!=null){hit.getEntity().damage(w.getDamageSources().playerAttack(p),6);end=hit.getPos();}
   Vec3d segment=end.subtract(start);int marks=Math.max(1,(int)(segment.length()*5));
   for(int i=1;i<=marks;i++){Vec3d at=start.add(segment.multiply((double)i/marks));((ServerWorld)w).spawnParticles(new net.minecraft.particle.DustParticleEffect(new org.joml.Vector3f(1f,.8f,.25f),.55f),at.x,at.y,at.z,1,0,0,0,0);}
   ((ServerWorld)w).spawnParticles(net.minecraft.particle.ParticleTypes.CRIT,end.x,end.y,end.z,5,.07,.07,.07,.03);
   if(hit!=null||wall.getType()!=HitResult.Type.MISS)w.playSound(null,end.x,end.y,end.z,TerrorSounds.get("impact"),SoundCategory.PLAYERS,.5f,1.4f);
   ((ServerWorld)w).spawnParticles(net.minecraft.particle.ParticleTypes.CRIT,start.x,start.y,start.z,8,.1,.1,.1,.2);
  }
  w.playSound(null,p.getBlockPos(),bazooka?SoundEvents.ENTITY_FIREWORK_ROCKET_LAUNCH:SoundEvents.ENTITY_GENERIC_EXPLODE.value(),SoundCategory.PLAYERS,bazooka?1f:.5f,bazooka?.8f:1.8f);
  return TypedActionResult.consume(s);
 }
 @Override public boolean isItemBarVisible(ItemStack s){return true;}
 @Override public int getItemBarStep(ItemStack s){return Math.round(13f*rounds(s)/capacity());}
 @Override public int getItemBarColor(ItemStack s){return 0xe8c547;}
}
