package dev.presidencia.client;
import dev.presidencia.*;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.Entity;
import org.lwjgl.glfw.GLFW;
public final class TerrorHud {
 private static boolean days;
 public static void initialize(){
  var key=KeyBindingHelper.registerKeyBinding(new KeyBinding("key.presidencia_terror.days",InputUtil.Type.KEYSYM,GLFW.GLFW_KEY_N,"category.presidencia_terror"));
  ClientTickEvents.END_CLIENT_TICK.register(c->{while(key.wasPressed())if(c.world!=null&&c.currentScreen==null)days=!days;});
  HudRenderCallback.EVENT.register((d,t)->{
   var c=net.minecraft.client.MinecraftClient.getInstance();if(c.world==null||c.player==null||c.options.hudHidden)return;
   if(days)d.drawTextWithShadow(c.textRenderer,"Day "+Math.floorDiv(c.world.getTimeOfDay(),24000L),8,8,0xffffffff);
   var stack=c.player.getMainHandStack();int width=d.getScaledWindowWidth();
   if(stack.getItem() instanceof Gun gun&&Gun.reloadEnd(stack)>0){float progress=Math.clamp(1-(Gun.reloadEnd(stack)-c.world.getTime()-t.getTickDelta(false))/(float)gun.reloadTicks(),0,1);int x=width/2-40,y=d.getScaledWindowHeight()/2+20;d.fill(x,y,x+80,y+4,0xaa222222);d.fill(x,y,x+(int)(80*progress),y+4,0xffffce45);}
   President lula=null,bolsonaro=null;double ld=4096,bd=4096;
   for(Entity entity:c.world.getEntities())if(entity instanceof President p&&p.isAlive()){double dist=p.squaredDistanceTo(c.player);if(p.lula()&&dist<ld){lula=p;ld=dist;}else if(!p.lula()&&dist<bd){bolsonaro=p;bd=dist;}}
   int bar=Math.min(170,(width-40)/2);President[] bosses={lula,bolsonaro};
   for(int i=0;i<2;i++){President p=bosses[i];if(p==null)continue;int x=width/2+(i==0?-bar-5:5),y=30;String label=(p.lula()?"Lula":"Bolsonaro")+" "+(int)Math.ceil(p.getHealth())+" / "+(int)p.getMaxHealth();d.drawTextWithShadow(c.textRenderer,label,x,y-11,0xffffffff);d.fill(x,y,x+bar,y+7,0xbb171717);d.fill(x+1,y+1,x+1+(int)((bar-2)*p.getHealth()/p.getMaxHealth()),y+6,p.lula()?0xffd75349:0xff56aa69);}
  });
 }
}
