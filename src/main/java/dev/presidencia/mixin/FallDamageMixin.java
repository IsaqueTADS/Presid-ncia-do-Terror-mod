package dev.presidencia.mixin;
import dev.presidencia.DirtArmor;
import net.minecraft.entity.LivingEntity;import net.minecraft.entity.damage.DamageSource;import net.minecraft.registry.tag.DamageTypeTags;
import org.spongepowered.asm.mixin.Mixin;import org.spongepowered.asm.mixin.injection.At;import org.spongepowered.asm.mixin.injection.ModifyVariable;
@Mixin(LivingEntity.class)
public abstract class FallDamageMixin {
 @ModifyVariable(method="damage",at=@At("HEAD"),argsOnly=true,ordinal=0)
 private float presidencia$softLanding(float amount,DamageSource source,float original){return source.isIn(DamageTypeTags.IS_FALL)&&DirtArmor.fullSet((LivingEntity)(Object)this)?amount*.5f:amount;}
}
