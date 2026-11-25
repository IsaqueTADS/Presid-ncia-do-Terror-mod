package dev.presidencia;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.hit.EntityHitResult;
final class ProjectileUtilBridge {
 static EntityHitResult hit(Entity owner,Vec3d start,Vec3d end){return ProjectileUtil.raycast(owner,start,end,owner.getBoundingBox().stretch(end.subtract(start)).expand(1),e->e.isAlive()&&!e.isSpectator()&&e.canHit(),start.squaredDistanceTo(end));}
}
