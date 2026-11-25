package dev.presidencia;
import net.minecraft.entity.*;
import net.minecraft.entity.projectile.ExplosiveProjectileEntity;
import net.minecraft.util.hit.*;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.server.world.ServerWorld;
public class Rocket extends ExplosiveProjectileEntity {
 public static final float MAX_DAMAGE=12;
 public Rocket(EntityType<? extends Rocket> type,World world){super(type,world);accelerationPower=0;}
 public static Rocket launch(ServerWorld w,LivingEntity owner,Vec3d pos,Vec3d direction){var r=new Rocket(Terror.ROCKET,w);r.setOwner(owner);r.setPosition(pos);r.setVelocity(direction.normalize().multiply(1.2));w.spawnEntity(r);return r;}
 @Override protected float getDrag(){return 1;}
 @Override public void tick(){super.tick();if(!getWorld().isClient&&age>100)discard();}
 @Override protected boolean canHit(Entity e){return e!=getOwner()&&super.canHit(e);}
 @Override protected void onCollision(HitResult hit){
  if(!getWorld().isClient&&!isRemoved()&&hit.getType()!=HitResult.Type.MISS){
   setPosition(hit.getPos());
   // One explosion damage path: do not additionally apply direct-hit damage.
   var behavior=new net.minecraft.world.explosion.ExplosionBehavior(){@Override public float calculateDamage(net.minecraft.world.explosion.Explosion explosion,Entity entity){return Math.min(MAX_DAMAGE,super.calculateDamage(explosion,entity));}};
   var health=new java.util.HashMap<LivingEntity,Float>();for(var e:getWorld().getEntitiesByClass(LivingEntity.class,getBoundingBox().expand(5),e->e.isAlive()))health.put(e,e.getHealth());
   getWorld().createExplosion(this,null,behavior,getX(),getY(),getZ(),2f,false,TerrorConfig.get().destroyBlocks?World.ExplosionSourceType.TNT:World.ExplosionSourceType.NONE,net.minecraft.particle.ParticleTypes.EXPLOSION,net.minecraft.particle.ParticleTypes.EXPLOSION_EMITTER,net.minecraft.registry.Registries.SOUND_EVENT.getEntry(TerrorSounds.get("silent")));
   ModAudio.play(getWorld(),getPos(),net.minecraft.sound.SoundEvents.ENTITY_GENERIC_EXPLODE.value(),ModAudio.WEAPON,1,.9f);
   health.forEach((e,before)->DamageNumbers.show(e,before-e.getHealth(),e.getPos().add(0,e.getHeight()*.7,0),false,false));discard();
  }
 }
}
