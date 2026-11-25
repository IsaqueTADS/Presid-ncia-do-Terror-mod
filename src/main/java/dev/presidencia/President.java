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
import net.minecraft.util.math.BlockPos;
import net.minecraft.block.BlockState;
import java.util.UUID;

public class President extends PathAwareEntity {
 private static final TrackedData<Boolean> MOUNTED=DataTracker.registerData(President.class,TrackedDataHandlerRegistry.BOOLEAN);
 private static final TrackedData<Integer> SHOT=DataTracker.registerData(President.class,TrackedDataHandlerRegistry.INTEGER);
 private static final TrackedData<String> CLIP=DataTracker.registerData(President.class,TrackedDataHandlerRegistry.STRING);
 private static final TrackedData<Long> CLIP_START=DataTracker.registerData(President.class,TrackedDataHandlerRegistry.LONG);
 private int shotCooldown=300,meleeCooldown,lostSight,clipTicks,restState,restIdleTicks,moodIndex,nextMood=160,nextCombatVoice=180,nextBeer=600,voiceCooldown;
 public String actionClip(){return dataTracker.get(CLIP);}
 public float actionTime(float delta){return (getWorld().getTime()-dataTracker.get(CLIP_START)+delta)/20f;}
 public int restState(){return restState;}
 public boolean resting(){return restState>0;}
 public boolean visualMounted(){return mounted()&&!actionClip().equals("transform_cue");}
 private void cue(String clip,int ticks){dataTracker.set(CLIP,clip);dataTracker.set(CLIP_START,getWorld().getTime());clipTicks=ticks;}
 private void sound(String name,float volume,float pitch){if(name.startsWith("lula_")||name.startsWith("bolsonaro_"))return;getWorld().playSound(null,getX(),getY()+1,getZ(),TerrorSounds.get(name),SoundCategory.HOSTILE,volume,pitch);}
 private void stopRest(){if(restState<4){restState=lula()?5:4;cue(lula()?"stand_up":"wake_up",lula()?50:30);sound("swish",.5f,1);}}
 public boolean beginBeerMoment(){
  if(!lula()||getTarget()!=null||resting()||clipTicks>0||!isAlive())return false;
  nextBeer=age+900+getRandom().nextInt(901);getNavigation().stop();cue("drink_beer",80);return true;
 }
 private void tickAnimations(){
  if(voiceCooldown>0)voiceCooldown--;
  if(actionClip().equals("drink_beer")){long elapsed=getWorld().getTime()-dataTracker.get(CLIP_START);if(elapsed>0&&elapsed<80&&elapsed%20==0)getWorld().playSound(null,getBlockPos(),SoundEvents.ENTITY_GENERIC_DRINK,SoundCategory.HOSTILE,.75f,1);}

  if(clipTicks>0&&--clipTicks==0){
   String previous=actionClip();
   if(previous.equals("drink_beer")){cue("tipsy_sway",100);Voices.play(this,"lula_beer",false);voiceCooldown=140;return;}
   if(previous.equals("transform_cue")){cue("mount_reaction",24);return;}
   if(restState==1){restState=lula()?3:2;cue(lula()?"rest_sleep":"settle_sleep",lula()?Integer.MAX_VALUE:30);return;}
   if(restState==2){restState=3;cue("rest_sleep",Integer.MAX_VALUE);return;}
   if(restState==4){restState=5;cue("stand_up",50);return;}
   if(restState==5){restState=0;restIdleTicks=0;nextMood=age+160;}
   dataTracker.set(CLIP,"");
  }
  boolean moving=getVelocity().horizontalLengthSquared()>.0003;
  restIdleTicks=moving||getTarget()!=null?0:restIdleTicks+1;
  if(resting()){
   getNavigation().stop();
   if(restState<=3){var owner=StalkerDirector.companion(this);if(getTarget()!=null||(owner!=null&&squaredDistanceTo(owner)>18*18))stopRest();}
   return;
  }
  if(lula()&&age>=nextBeer&&getTarget()==null&&!moving&&clipTicks==0&&isOnGround()&&beginBeerMoment())return;
  if(mounted()&&clipTicks==0&&isOnGround()&&restIdleTicks>=200&&getTarget()==null){restState=1;cue("lie_down",50);getNavigation().stop();return;}
  if(getTarget()==null&&!moving&&clipTicks==0&&age>=nextMood){
   nextMood=age+180;
   String[] clips=mounted()?new String[]{"idle","look_around","mount_look_around","mouth_chatter",lula()?"bray":"roar","mount_reaction"}:new String[]{"look_around","startle",lula()?"steal_food":"protect_phone","idle"};
   String clip=clips[moodIndex++%clips.length];cue(clip,clip.equals("bray")||clip.equals("roar")?60:40);
   if(clip.equals("bray")||clip.equals("roar"))sound(lula()?"donkey_voice":"trex_voice",.8f,1);
   if(clip.equals("mouth_chatter"))sound(lula()?"lula_taunt":"bolsonaro_taunt",.8f,1);
   if(clip.equals("steal_food"))sound("lula_food",.8f,1);
   if(clip.equals("protect_phone"))sound("bolsonaro_phone",.8f,1);
  }
 }
 @Override protected void playStepSound(BlockPos pos,BlockState state){sound(mounted()?(lula()?"donkey_step":"trex_step"):"human_step",mounted()?.55f:.3f,1);}
 @Override protected SoundEvent getHurtSound(DamageSource source){return TerrorSounds.get("creature_hurt");}
 public void clearAggression(){setTarget(null);aggressor=null;dataTracker.set(SHOT,-1);getNavigation().stop();}
 public boolean managed;public int generation;public UUID followed;
 private UUID aggressor;
 public President(EntityType<? extends President> type,World world){super(type,world);setPersistent();nextBeer=600+getRandom().nextInt(601);}
 public boolean lula(){return getType()==Terror.LULA;}
 public boolean mounted(){return dataTracker.get(MOUNTED);}
 public int shotIn(){return dataTracker.get(SHOT);}
 public static DefaultAttributeContainer.Builder attributes(){return createMobAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH,500).add(EntityAttributes.GENERIC_MOVEMENT_SPEED,.27).add(EntityAttributes.GENERIC_ATTACK_DAMAGE,6).add(EntityAttributes.GENERIC_FOLLOW_RANGE,64).add(EntityAttributes.GENERIC_STEP_HEIGHT,1);}
 @Override protected void initGoals(){goalSelector.add(0,new SwimGoal(this));goalSelector.add(6,new WanderAroundFarGoal(this,.7){@Override public boolean canStart(){return !resting()&&clipTicks==0&&super.canStart();}@Override public boolean shouldContinue(){return !resting()&&clipTicks==0&&super.shouldContinue();}});goalSelector.add(7,new LookAroundGoal(this));}
 @Override protected void initDataTracker(DataTracker.Builder b){super.initDataTracker(b);b.add(MOUNTED,false);b.add(SHOT,-1);b.add(CLIP,"");b.add(CLIP_START,0L);}
 @Override public EntityDimensions getBaseDimensions(EntityPose pose){return mounted()?EntityDimensions.changing(1.8f,lula()?4.8f:5.4f):super.getBaseDimensions(pose);}
 @Override public void onTrackedDataSet(TrackedData<?> key){super.onTrackedDataSet(key);if(key.equals(MOUNTED))calculateDimensions();}
 @Override public boolean damage(DamageSource source,float amount){boolean hurt=super.damage(source,amount);
  if(hurt&&!getWorld().isClient){((ServerWorld)getWorld()).spawnParticles(new net.minecraft.particle.DustParticleEffect(new org.joml.Vector3f(.65f,.08f,.06f),.8f),getX(),getY()+getHeight()*.65,getZ(),12,.35,.4,.35,.04);}
 if(hurt&&!getWorld().isClient&&isAlive()&&source.getAttacker() instanceof LivingEntity attacker&&attacker!=this){if(!mounted()){dataTracker.set(MOUNTED,true);shotCooldown=300;cue("transform_cue",20);sound("transform",.8f,1);Voices.play(this,lula()?"lula_react":"bolsonaro_defend",true);}if(mounted()&&!actionClip().equals("transform_cue")&&voiceCooldown==0){Voices.play(this,lula()?"lula_react":"bolsonaro_taunt",false);voiceCooldown=160;}if(resting())stopRest();else if(!actionClip().equals("transform_cue"))cue("mount_reaction",24);setTarget(attacker);aggressor=attacker.getUuid();lostSight=0;}return hurt;}
 @Override public void tick(){
  super.tick();if(getWorld().isClient||!isAlive())return;tickAnimations();
  if(getTarget()==null&&aggressor!=null&&getWorld() instanceof ServerWorld w){var e=w.getEntity(aggressor);if(e instanceof LivingEntity l)setTarget(l);else aggressor=null;}
  var target=getTarget();if(target==null){var owner=StalkerDirector.companion(this);if(owner!=null){if(!resting()&&clipTicks==0){getLookControl().lookAt(owner,30,30);if(squaredDistanceTo(owner)>10*10)getNavigation().startMovingTo(owner,.7);else getNavigation().stop();}}if(getWorld().getClosestPlayer(this,24)!=null)Voices.ambient(this);return;}
  if(resting()||actionClip().equals("transform_cue")){getNavigation().stop();if(shotCooldown>0)shotCooldown--;return;}
  if(getWorld().getClosestPlayer(this,24)!=null)Voices.ambient(this);
  if(managed&&target.isAlive()&&target.getWorld()!=getWorld()){getNavigation().stop();return;}
  if(!target.isAlive()||target.isSpectator()||target.getWorld()!=getWorld()||(!managed&&(squaredDistanceTo(target)>80*80||(!canSee(target)&&++lostSight>200)))){setTarget(null);aggressor=null;getNavigation().stop();dataTracker.set(SHOT,-1);return;}
  if(canSee(target))lostSight=0;
  if(mounted()&&age>=nextCombatVoice&&clipTicks==0){nextCombatVoice=age+240;boolean vocal=(moodIndex++%2)==0;cue(vocal?(lula()?"bray":"roar"):"mouth_chatter",vocal?60:40);sound(vocal?(lula()?"donkey_voice":"trex_voice"):(lula()?"lula_taunt":"bolsonaro_taunt"),.8f,1);}
  getLookControl().lookAt(target,30,30);getNavigation().startMovingTo(target,1.2);getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED).setBaseValue(mounted()?.32:.27);
  if(meleeCooldown>0)meleeCooldown--;
  if(mounted()&&squaredDistanceTo(target)<9&&meleeCooldown==0&&canSee(target)){tryAttack(target);meleeCooldown=30;}
  if(mounted()&&!lula()){
   if(shotCooldown>0)shotCooldown--;
   dataTracker.set(SHOT,shotCooldown<=40?shotCooldown:shotCooldown>260?-(300-shotCooldown)-2:-1);
   if(shotCooldown==40){getWorld().playSound(null,getBlockPos(),SoundEvents.BLOCK_NOTE_BLOCK_PLING.value(),SoundCategory.HOSTILE,1,.6f);sound("swish",.6f,1);}
   if(shotCooldown==0&&canSee(target)){
    var direction=target.getEyePos().subtract(getEyePos()).normalize();
    Rocket.launch((ServerWorld)getWorld(),this,getEyePos(),direction);shotCooldown=300;
    getWorld().playSound(null,getBlockPos(),SoundEvents.ENTITY_FIREWORK_ROCKET_LAUNCH,SoundCategory.HOSTILE,1,.7f);
   }
  }
 }
 @Override public void writeCustomDataToNbt(NbtCompound n){super.writeCustomDataToNbt(n);n.putBoolean("ManagedPair",managed);n.putInt("PairGeneration",generation);if(followed!=null)n.putUuid("Followed",followed);n.putInt("BeerDelay",Math.max(0,nextBeer-age));n.putBoolean("Mounted",mounted());n.putInt("RestState",restState);n.putInt("RestIdleTicks",restIdleTicks);n.putString("ActionClip",actionClip());n.putInt("ClipTicks",clipTicks);n.putInt("ShotCooldown",shotCooldown);if(aggressor!=null)n.putUuid("Aggressor",aggressor);}
 @Override public void readCustomDataFromNbt(NbtCompound n){super.readCustomDataFromNbt(n);managed=n.getBoolean("ManagedPair");generation=n.getInt("PairGeneration");followed=n.containsUuid("Followed")?n.getUuid("Followed"):null;dataTracker.set(MOUNTED,n.getBoolean("Mounted"));shotCooldown=Math.max(0,Math.min(300,n.getInt("ShotCooldown")));aggressor=n.containsUuid("Aggressor")?n.getUuid("Aggressor"):null;
  float oldMax=getMaxHealth(),oldHealth=getHealth();getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(500);if(oldMax<500)setHealth(Math.min(500,oldHealth+500-oldMax));
  nextBeer=age+(n.contains("BeerDelay")?Math.clamp(n.getInt("BeerDelay"),20,1800):600);
  restState=Math.clamp(n.getInt("RestState"),0,5);restIdleTicks=Math.max(0,n.getInt("RestIdleTicks"));
  if(restState>0){restState=3;cue("rest_sleep",Integer.MAX_VALUE);}else if(n.getInt("ClipTicks")>0)cue(n.getString("ActionClip"),Math.min(100,n.getInt("ClipTicks")));}
 @Override public void onDeath(DamageSource source){
  if(!getWorld().isClient){Voices.play(this,lula()?"lula_beer":"bolsonaro_death",true);StalkerDirector.defeated(this);}
  super.onDeath(source);
 }
 @Override protected void dropLoot(DamageSource source,boolean causedByPlayer){
  super.dropLoot(source,causedByPlayer);
  if(lula()){for(int i=0;i<5;i++){var meat=new net.minecraft.item.ItemStack(net.minecraft.item.Items.COOKED_BEEF,64);meat.set(net.minecraft.component.DataComponentTypes.CUSTOM_NAME,net.minecraft.text.Text.literal("Picanha"));dropStack(meat);}for(int i=0;i<2;i++)dropStack(new net.minecraft.item.ItemStack(Terror.BEER,64));}
  else{dropStack(new net.minecraft.item.ItemStack(Terror.BAZOOKA));dropStack(new net.minecraft.item.ItemStack(Terror.MISSILE,64));}
 }
 @Override public boolean cannotDespawn(){return true;}
}
