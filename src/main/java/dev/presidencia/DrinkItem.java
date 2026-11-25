package dev.presidencia;
import net.minecraft.item.*;
import net.minecraft.entity.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.minecraft.util.*;
public final class DrinkItem extends Item {
 public DrinkItem(){super(new Settings().maxCount(16));}
 @Override public TypedActionResult<ItemStack> use(World w,PlayerEntity p,Hand hand){p.setCurrentHand(hand);return TypedActionResult.consume(p.getStackInHand(hand));}
 @Override public int getMaxUseTime(ItemStack s,LivingEntity e){return 32;}
 @Override public UseAction getUseAction(ItemStack s){return UseAction.DRINK;}
 @Override public ItemStack finishUsing(ItemStack s,World w,LivingEntity entity){
  if(!w.isClient&&entity instanceof PlayerEntity p&&!p.isCreative()){s.decrement(1);if(s.isEmpty())return new ItemStack(Items.GLASS_BOTTLE);if(!p.getInventory().insertStack(new ItemStack(Items.GLASS_BOTTLE)))p.dropItem(new ItemStack(Items.GLASS_BOTTLE),false);}
  return s;
 }
}
