package dev.presidencia;
import net.minecraft.entity.*;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import net.minecraft.sound.*;
import java.util.UUID;

public class President extends PathAwareEntity {
 private static final TrackedData<Boolean> MOUNTED=DataTracker.registerData(President.class,TrackedDataHandlerRegistry.BOOLEAN);
 private static final TrackedData<Integer> SHOT=DataTracker.registerData(President.class,TrackedDataHandlerRegistry.INTEGER);
 private int shotCooldown=300,meleeCooldown,lostSight;
 private UUID aggressor;
 public President(EntityType<? extends President> type,World world){super(type,world);setPersistent();}
 public boolean lula(){return getType()==Terror.LULA;}
 public boolean mounted(){return dataTracker.get(MOUNTED);}
 public int shotIn(){return dataTracker.get(SHOT);}
 public static DefaultAttributeContainer.Builder attributes(){return createMobAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH,80).add(EntityAttributes.GENERIC_MOVEMENT_SPEED,.27).add(EntityAttributes.GENERIC_ATTACK_DAMAGE,6).add(EntityAttributes.GENERIC_FOLLOW_RANGE,64).add(EntityAttributes.GENERIC_STEP_HEIGHT,1);}
 @Override protected void initGoals(){goalSelector.add(0,new SwimGoal(this));goalSelector.add(6,new WanderAroundFarGoal(this,.7));goalSelector.add(7,new LookAroundGoal(this));}
 @Override protected void initDataTracker(DataTracker.Builder b){super.initDataTracker(b);b.add(MOUNTED,false);b.add(SHOT,-1);}
 @Override public EntityDimensions getBaseDimensions(EntityPose pose){return mounted()?EntityDimensions.changing(1.8f,lula()?4.8f:5.4f):super.getBaseDimensions(pose);}
 @Override public void onTrackedDataSet(TrackedData<?> key){super.onTrackedDataSet(key);if(key.equals(MOUNTED))calculateDimensions();}
 @Override public boolean damage(DamageSource source,float amount){boolean hurt=super.damage(source,amount);if(hurt&&!getWorld().isClient&&isAlive()&&source.getAttacker() instanceof LivingEntity attacker&&attacker!=this){if(!mounted()){dataTracker.set(MOUNTED,true);shotCooldown=300;getWorld().playSound(null,getBlockPos(),SoundEvents.ENTITY_RAVAGER_ROAR,SoundCategory.HOSTILE,.7f,lula()?1.4f:.7f);}setTarget(attacker);aggressor=attacker.getUuid();lostSight=0;}return hurt;}
 @Override public void tick(){
  super.tick();if(getWorld().isClient||!isAlive())return;
  if(getTarget()==null&&aggressor!=null&&getWorld() instanceof ServerWorld w){var e=w.getEntity(aggressor);if(e instanceof LivingEntity l)setTarget(l);else aggressor=null;}
  var target=getTarget();if(target==null)return;
  if(!target.isAlive()||target.isSpectator()||target.getWorld()!=getWorld()||squaredDistanceTo(target)>80*80||(!canSee(target)&&++lostSight>200)){setTarget(null);aggressor=null;getNavigation().stop();dataTracker.set(SHOT,-1);return;}
  if(canSee(target))lostSight=0;
  getLookControl().lookAt(target,30,30);getNavigation().startMovingTo(target,1.2);getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED).setBaseValue(mounted()?.32:.27);
  if(meleeCooldown>0)meleeCooldown--;
  if(mounted()&&squaredDistanceTo(target)<9&&meleeCooldown==0&&canSee(target)){tryAttack(target);meleeCooldown=30;}
  if(mounted()&&!lula()){
   if(shotCooldown>0)shotCooldown--;
   dataTracker.set(SHOT,shotCooldown<=40?shotCooldown:shotCooldown>260?-(300-shotCooldown)-2:-1);
   if(shotCooldown==40)getWorld().playSound(null,getBlockPos(),SoundEvents.BLOCK_NOTE_BLOCK_PLING.value(),SoundCategory.HOSTILE,1,.6f);
   if(shotCooldown==0&&canSee(target)){
    var direction=target.getEyePos().subtract(getEyePos()).normalize();
    Rocket.launch((ServerWorld)getWorld(),this,getEyePos(),direction);shotCooldown=300;
    getWorld().playSound(null,getBlockPos(),SoundEvents.ENTITY_FIREWORK_ROCKET_LAUNCH,SoundCategory.HOSTILE,1,.7f);
   }
  }
 }
 @Override public void writeCustomDataToNbt(NbtCompound n){super.writeCustomDataToNbt(n);n.putBoolean("Mounted",mounted());n.putInt("ShotCooldown",shotCooldown);if(aggressor!=null)n.putUuid("Aggressor",aggressor);}
 @Override public void readCustomDataFromNbt(NbtCompound n){super.readCustomDataFromNbt(n);dataTracker.set(MOUNTED,n.getBoolean("Mounted"));shotCooldown=Math.max(0,Math.min(300,n.getInt("ShotCooldown")));aggressor=n.containsUuid("Aggressor")?n.getUuid("Aggressor"):null;}
 @Override public boolean cannotDespawn(){return true;}
}
