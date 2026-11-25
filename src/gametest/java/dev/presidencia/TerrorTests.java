package dev.presidencia;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.test.*;
import net.minecraft.entity.*;
import net.minecraft.item.*;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Hand;
import net.minecraft.util.math.*;
import net.minecraft.world.GameMode;
public class TerrorTests implements FabricGameTest {
 @GameTest(templateName=EMPTY_STRUCTURE,tickLimit=30)
 public void bossDeathDropsRequestedLootExactlyOnce(TestContext c){
  var origin=Vec3d.ofCenter(c.getAbsolutePos(new BlockPos(3,3,3)));
  for(var type:java.util.List.of(Terror.LULA,Terror.BOLSONARO)){
   for(var item:c.getWorld().getEntitiesByClass(net.minecraft.entity.ItemEntity.class,new net.minecraft.util.math.Box(origin,origin).expand(10),e->true))item.discard();
   var e=type.create(c.getWorld());e.setPosition(origin);c.getWorld().spawnEntity(e);e.damage(c.getWorld().getDamageSources().genericKill(),10000);
   int meat=0,rockets=0,guns=0;
   for(var item:c.getWorld().getEntitiesByClass(net.minecraft.entity.ItemEntity.class,new net.minecraft.util.math.Box(origin,origin).expand(10),i->true)){
    var stack=item.getStack();if(stack.isOf(Items.COOKED_BEEF))meat+=stack.getCount();if(stack.isOf(Terror.MISSILE))rockets+=stack.getCount();if(stack.isOf(Terror.BAZOOKA))guns+=stack.getCount();
   }
   if(type==Terror.LULA)c.assertEquals(meat,320,"Five full stacks of cooked beef");else{c.assertEquals(guns,1,"One bazooka");c.assertEquals(rockets,64,"One stack of rockets");}
   e.damage(c.getWorld().getDamageSources().genericKill(),10000);e.discard();
  }c.complete();
 }
 @GameTest(templateName=EMPTY_STRUCTURE,tickLimit=20)
 public void alcoholRecipeRequiresWaterNotAnyPotion(TestContext c){
  var recipe=new AlcoholRecipe(net.minecraft.recipe.book.CraftingRecipeCategory.MISC);var water=new ItemStack(Items.POTION);water.set(net.minecraft.component.DataComponentTypes.POTION_CONTENTS,new net.minecraft.component.type.PotionContentsComponent(net.minecraft.potion.Potions.WATER));
  var input=net.minecraft.recipe.input.CraftingRecipeInput.create(2,1,java.util.List.of(water,new ItemStack(Items.SUGAR_CANE)));
  c.assertTrue(recipe.matches(input,c.getWorld()),"Water accepted");water.set(net.minecraft.component.DataComponentTypes.POTION_CONTENTS,new net.minecraft.component.type.PotionContentsComponent(net.minecraft.potion.Potions.HEALING));
  c.assertFalse(recipe.matches(net.minecraft.recipe.input.CraftingRecipeInput.create(2,1,java.util.List.of(water,new ItemStack(Items.SUGAR_CANE))),c.getWorld()),"Healing potion rejected");
  c.complete();
 }
 @GameTest(templateName=EMPTY_STRUCTURE,tickLimit=210)
 public void lulaDrinksThenSwaysInBothForms(TestContext c){
  var actors=new java.util.ArrayList<President>();
  for(boolean mounted:new boolean[]{false,true}){
   var e=Terror.LULA.create(c.getWorld());e.setAiDisabled(true);e.setNoGravity(true);e.setPosition(Vec3d.ofCenter(c.getAbsolutePos(new BlockPos(mounted?5:1,2,2))));
   var n=new NbtCompound();e.writeCustomDataToNbt(n);n.putBoolean("Mounted",mounted);e.readCustomDataFromNbt(n);c.getWorld().spawnEntity(e);actors.add(e);
   c.assertTrue(e.beginBeerMoment(),"Beer moment starts out of combat");c.assertEquals(e.actionClip(),"drink_beer","Drink action synchronized");
  }
  c.runAtTick(85,()->{for(var e:actors)c.assertEquals(e.actionClip(),"tipsy_sway","Drink transitions to short comic sway");});
  c.runAtTick(190,()->{for(var e:actors){c.assertFalse(e.actionClip().equals("drink_beer")||e.actionClip().equals("tipsy_sway"),"Acting ends, does not loop forever");c.assertEquals(e.getHealth(),500f,"Beer does not alter health");e.discard();}c.complete();});
 }
 @GameTest(templateName=EMPTY_STRUCTURE,tickLimit=20)
 public void beerMomentDoesNotInterruptCombatOrAffectBolsonaro(TestContext c){
  var p=c.createMockPlayer(GameMode.SURVIVAL);var lula=Terror.LULA.create(c.getWorld());lula.setTarget(p);c.assertFalse(lula.beginBeerMoment(),"No drinking during combat");
  var bolsonaro=Terror.BOLSONARO.create(c.getWorld());c.assertFalse(bolsonaro.beginBeerMoment(),"Gesture restricted to Lula");lula.discard();bolsonaro.discard();c.complete();
 }
 @GameTest(templateName=EMPTY_STRUCTURE,tickLimit=110)
 public void sleepingMountWakesBeforeChasing(TestContext c){
  var e=Terror.BOLSONARO.create(c.getWorld());e.setAiDisabled(true);e.setNoGravity(true);e.setPosition(Vec3d.ofBottomCenter(c.getAbsolutePos(new BlockPos(2,1,2))));
  var n=new NbtCompound();e.writeCustomDataToNbt(n);n.putBoolean("Mounted",true);n.putInt("RestState",3);e.readCustomDataFromNbt(n);c.getWorld().spawnEntity(e);
  c.assertEquals(e.actionClip(),"rest_sleep","Saved sleeping mount uses sleeping animation");
  var player=c.createMockPlayer(GameMode.SURVIVAL);player.setPosition(e.getPos().add(4,0,0));e.setTarget(player);
  c.runAtTick(3,()->c.assertEquals(e.actionClip(),"wake_up","Waking transition before pursuit"));
  c.runAtTick(36,()->c.assertEquals(e.actionClip(),"stand_up","Standing transition follows waking"));
  c.runAtTick(90,()->{c.assertFalse(e.resting(),"Rest cycle completes");c.assertTrue(e.getTarget()==player,"Target kept for pursuit");e.discard();c.complete();});
 }
 @GameTest(templateName=EMPTY_STRUCTURE,tickLimit=20)
 public void bazookaGripStaysInHandAndMuzzleFacesForward(TestContext c){
  for(float progress:new float[]{.3f,.5f,.75f,1}){
   var grip=BazookaPose.weapon(progress).transformPosition(new org.joml.Vector3f(BazookaPose.GRIP));
   var hand=BazookaPose.arm(progress).transformPosition(new org.joml.Vector3f(-11.5f,51,0));
   c.assertTrue(grip.distance(hand)<.0001f,"Weapon grip stays constrained to hand");
  }
  var forward=BazookaPose.weapon(1).transformDirection(new org.joml.Vector3f(0,0,-1)).normalize();c.assertTrue(forward.z<-.999f,"Muzzle points forwards, not backwards");c.complete();
 }
 @GameTest(templateName=EMPTY_STRUCTURE,tickLimit=20)
 public void oldWorldHealthMigratesToFiveHundred(TestContext c){
  var old=Terror.LULA.create(c.getWorld());old.getAttributeInstance(net.minecraft.entity.attribute.EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(80);old.setHealth(79);
  var n=new NbtCompound();old.writeCustomDataToNbt(n);var updated=Terror.LULA.create(c.getWorld());updated.readCustomDataFromNbt(n);
  c.assertEquals(updated.getMaxHealth(),500f,"Old saved attributes upgraded");c.assertEquals(updated.getHealth(),499f,"Existing one-point injury preserved");old.discard();updated.discard();c.complete();
 }
 @GameTest(templateName=EMPTY_STRUCTURE,tickLimit=40)
 public void formsPersistWithoutHealing(TestContext c){
  var p=c.createMockPlayer(GameMode.SURVIVAL);
  for(var type:java.util.List.of(Terror.LULA,Terror.BOLSONARO)){
   var e=type.create(c.getWorld());e.setNoGravity(true);c.getWorld().spawnEntity(e);
   c.assertFalse(e.mounted(),"Egg form starts on foot");e.damage(c.getWorld().getDamageSources().playerAttack(p),1);
   c.assertTrue(e.mounted(),"Hit transforms entity");c.assertEquals(e.getHealth(),499f,"Transformation preserves remaining health");
   var state=new NbtCompound();e.writeCustomDataToNbt(state);var restored=type.create(c.getWorld());restored.readCustomDataFromNbt(state);c.assertTrue(restored.mounted(),"Mounted state persists");c.assertEquals(restored.getHealth(),499f,"Health persists");e.discard();restored.discard();
  }c.complete();
 }
 @GameTest(templateName=EMPTY_STRUCTURE,tickLimit=90)
 public void partialReloadLoadsOnlyAvailableAmmo(TestContext c){
  var p=c.createMockPlayer(GameMode.SURVIVAL);var s=new ItemStack(Terror.REVOLVER);p.setStackInHand(Hand.MAIN_HAND,s);Gun.rounds(s,1);p.getInventory().insertStack(new ItemStack(Terror.BULLET,3));Terror.REVOLVER.reload(p,s);
  c.assertEquals(Gun.rounds(s),1,"Existing cartridge remains visible during reload");
  c.assertEquals(Gun.reloadEnd(s)-c.getWorld().getTime(),56L,"Reload scheduled for 56 ticks");
  var data=Gun.data(s);data.putLong("ReloadEnd",c.getWorld().getTime());Gun.put(s,data);
  Terror.REVOLVER.inventoryTick(s,c.getWorld(),p,0,true);c.assertEquals(Gun.rounds(s),4,"Only three available rounds inserted");c.assertEquals(p.getInventory().count(Terror.BULLET),0,"Ammo consumed exactly once");c.complete();
 }
 @GameTest(templateName=EMPTY_STRUCTURE,tickLimit=30)
 public void switchingWeaponCancelsReload(TestContext c){
  var p=c.createMockPlayer(GameMode.SURVIVAL);var s=new ItemStack(Terror.REVOLVER);p.setStackInHand(Hand.MAIN_HAND,s);p.getInventory().insertStack(new ItemStack(Terror.BULLET,6));Terror.REVOLVER.reload(p,s);Terror.REVOLVER.inventoryTick(s,c.getWorld(),p,0,false);c.assertEquals(Gun.reloadEnd(s),0L,"Reload cancelled");c.assertEquals(p.getInventory().count(Terror.BULLET),6,"Cancelled reload consumes nothing");c.complete();
 }
 @GameTest(templateName=EMPTY_STRUCTURE,tickLimit=40)
 public void revolverConsumesOneRoundAndRespectsCooldown(TestContext c){
  var p=c.createMockPlayer(GameMode.SURVIVAL);var s=new ItemStack(Terror.REVOLVER);p.setStackInHand(Hand.MAIN_HAND,s);Gun.rounds(s,2);Terror.REVOLVER.use(c.getWorld(),p,Hand.MAIN_HAND);c.assertEquals(Gun.rounds(s),1,"One cartridge per shot");Terror.REVOLVER.use(c.getWorld(),p,Hand.MAIN_HAND);c.assertEquals(Gun.rounds(s),1,"Spam blocked by cooldown");c.complete();
 }
 @GameTest(templateName=EMPTY_STRUCTURE,tickLimit=90)
 public void rocketHitsWallAndDestroysBlocks(TestContext c){
  var pos=c.getAbsolutePos(new BlockPos(2,2,2));c.getWorld().setBlockState(pos,Blocks.GLASS.getDefaultState());
  var owner=Terror.LULA.create(c.getWorld());owner.setPosition(Vec3d.ofCenter(pos).add(0,0,-3));owner.setNoGravity(true);c.getWorld().spawnEntity(owner);
  var rocket=Rocket.launch(c.getWorld(),owner,Vec3d.ofCenter(pos).add(0,0,-2),new Vec3d(0,0,1));
  c.runAtTick(15,()->{c.assertTrue(rocket.isRemoved(),"Rocket collides and is removed");c.assertTrue(c.getWorld().getBlockState(pos).isAir(),"Explosion destroys glass");owner.discard();c.complete();});
 }
 @GameTest(templateName=EMPTY_STRUCTURE,tickLimit=330)
 public void bolsonaroWaitsFifteenSecondsBeforeRocket(TestContext c){
  var p=c.createMockPlayer(GameMode.SURVIVAL);p.setPosition(Vec3d.ofCenter(c.getAbsolutePos(new BlockPos(6,3,6))));
  var e=Terror.BOLSONARO.create(c.getWorld());e.setPosition(Vec3d.ofCenter(c.getAbsolutePos(new BlockPos(1,3,1))));e.setNoGravity(true);e.setAiDisabled(true);c.getWorld().spawnEntity(e);e.damage(c.getWorld().getDamageSources().playerAttack(p),1);
  c.runAtTick(280,()->{var n=new NbtCompound();e.writeCustomDataToNbt(n);c.assertTrue(n.getInt("ShotCooldown")>0&&n.getInt("ShotCooldown")<40,"No early shot; countdown enters telegraph");});
  c.runAtTick(305,()->{var n=new NbtCompound();e.writeCustomDataToNbt(n);c.assertTrue(n.getInt("ShotCooldown")>280,"Shot fires at 300 ticks and cooldown resets");e.discard();c.complete();});
 }
 @GameTest(templateName=EMPTY_STRUCTURE,tickLimit=40)
 public void revolverCannotShootThroughWall(TestContext c){
  var p=c.createMockPlayer(GameMode.SURVIVAL);p.setPosition(Vec3d.ofBottomCenter(c.getAbsolutePos(new BlockPos(2,1,1))));p.setYaw(0);p.setPitch(0);
  var e=Terror.LULA.create(c.getWorld());e.setPosition(Vec3d.ofBottomCenter(c.getAbsolutePos(new BlockPos(2,1,6))));e.setNoGravity(true);e.setAiDisabled(true);c.getWorld().spawnEntity(e);
  for(int y=1;y<6;y++)c.setBlockState(2,y,4,Blocks.STONE);
  var s=new ItemStack(Terror.REVOLVER);p.setStackInHand(Hand.MAIN_HAND,s);Gun.rounds(s,2);Terror.REVOLVER.use(c.getWorld(),p,Hand.MAIN_HAND);c.assertEquals(e.getHealth(),500f,"Wall blocks hitscan");
  for(int y=1;y<6;y++)c.setBlockState(2,y,4,Blocks.AIR);
  p.getItemCooldownManager().set(Terror.REVOLVER,0);Terror.REVOLVER.use(c.getWorld(),p,Hand.MAIN_HAND);c.assertEquals(e.getHealth(),494f,"Clear hit deals six damage");e.discard();c.complete();
 }
}
