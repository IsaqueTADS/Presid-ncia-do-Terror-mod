package dev.presidencia.client;
import dev.presidencia.*;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.*;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.resource.*;
import net.minecraft.resource.*;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.*;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.*;
import net.minecraft.item.ItemStack;
import org.lwjgl.glfw.GLFW;

public class TerrorClient implements ClientModInitializer {
 private long lastShot=-1000;
 private float recoilRemaining;
 public void onInitializeClient(){
  TerrorHud.initialize();
  ClientAudio.initialize();
  net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents.CLIENT_STARTED.register(client->{var packs=client.getResourcePackManager();String old="file/presidencia-memes-local.zip";if(client.options.resourcePacks.remove(old)||packs.getEnabledIds().contains(old)){var enabled=new java.util.ArrayList<>(packs.getEnabledIds());enabled.remove(old);packs.setEnabledProfiles(enabled);client.options.write();client.reloadResources();}});
  var audioKey=KeyBindingHelper.registerKeyBinding(new KeyBinding("key.presidencia_terror.audio",InputUtil.Type.KEYSYM,GLFW.GLFW_KEY_F8,"category.presidencia_terror"));
  ClientTickEvents.END_CLIENT_TICK.register(c->{while(audioKey.wasPressed())if(c.player!=null&&c.currentScreen==null)c.setScreen(new AudioScreen());});
  var key=KeyBindingHelper.registerKeyBinding(new KeyBinding("key.presidencia_terror.reload",InputUtil.Type.KEYSYM,GLFW.GLFW_KEY_R,"category.presidencia_terror"));
  ClientTickEvents.END_CLIENT_TICK.register(client->{
   while(key.wasPressed())if(client.player!=null&&client.currentScreen==null&&client.player.getMainHandStack().getItem() instanceof Gun&&ClientPlayNetworking.canSend(ReloadPacket.ID))ClientPlayNetworking.send(new ReloadPacket());
   if(client.player!=null&&client.world!=null){var s=client.player.getMainHandStack();if(s.getItem() instanceof Gun gun){long shot=Gun.firedAt(s);if(shot!=lastShot&&client.world.getTime()-shot>=0&&client.world.getTime()-shot<5){recoilRemaining+=gun.bazooka?4:1.8f;lastShot=shot;};}else lastShot=-1000;}
   if(client.player!=null&&recoilRemaining>0){float step=recoilRemaining*.5f;client.player.setPitch(Math.max(-90,client.player.getPitch()-step));recoilRemaining-=step;if(recoilRemaining<.015f)recoilRemaining=0;}
  });
  for(var item:new net.minecraft.item.Item[]{Terror.REVOLVER,Terror.BAZOOKA,Terror.BULLET,Terror.MISSILE,Terror.BEER,Terror.ALCOHOL})BuiltinItemRendererRegistry.INSTANCE.register(item,TerrorClient::item);
  EntityRendererRegistry.register(Terror.LULA,PresidentRenderer::new);EntityRendererRegistry.register(Terror.BOLSONARO,PresidentRenderer::new);
  EntityRendererRegistry.register(Terror.ROCKET,RocketRenderer::new);
  ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new SimpleSynchronousResourceReloadListener(){public Identifier getFabricId(){return Terror.id("rig_reload");}public void reload(ResourceManager manager){BlockbenchModel.clear();}});
 }
 private static void item(ItemStack s,ModelTransformationMode mode,MatrixStack matrices,VertexConsumerProvider buffers,int light,int overlay){
  var client=net.minecraft.client.MinecraftClient.getInstance();float now=client.world==null?0:client.world.getTime()+client.getRenderTickCounter().getTickDelta(false);String name=s.isOf(Terror.BEER)||s.isOf(Terror.ALCOHOL)?"cerveja":s.isOf(Terror.BAZOOKA)?"bazuca":s.isOf(Terror.REVOLVER)?"revolver":s.isOf(Terror.BULLET)?"municao_revolver":"foguete_bazuca";
  String clip="idle";float t=now/20;int rounds=-1;
  if(s.getItem() instanceof Gun gun){rounds=Gun.rounds(s);if(Gun.reloadEnd(s)>0){clip="reload";t=Math.max(0,(now-(Gun.reloadEnd(s)-gun.reloadTicks()))/20);}else if(now-Gun.firedAt(s)>=0&&now-Gun.firedAt(s)<13){clip="fire";t=(now-Gun.firedAt(s))/20;}}
  matrices.push();matrices.translate(.5,.5,.5);matrices.multiply(net.minecraft.util.math.RotationAxis.POSITIVE_Y.rotationDegrees(mode==ModelTransformationMode.GUI?135:0));matrices.multiply(net.minecraft.util.math.RotationAxis.POSITIVE_X.rotationDegrees(mode==ModelTransformationMode.GUI?-18:0));float scale=name.equals("bazuca")?.022f:name.equals("revolver")?.035f:name.equals("municao_revolver")?.14f:name.equals("cerveja")?.045f:.065f;matrices.scale(scale,scale,scale);BlockbenchModel.get(name).draw(matrices,buffers,light,overlay,clip,t,rounds);matrices.pop();
 }
 private static class PresidentRenderer extends EntityRenderer<President>{
  PresidentRenderer(EntityRendererFactory.Context ctx){super(ctx);shadowRadius=1;}
  public Identifier getTexture(President e){return Terror.id("textures/rig/"+(e.lula()?"lula":"bolsonaro")+".png");}
  @Override public void render(President e,float yaw,float delta,MatrixStack ms,VertexConsumerProvider buffers,int light){
   String name=e.lula()?"lula":"bolsonaro";if(e.visualMounted())name+=e.lula()?"_jumento":"_trex";
   if(e.lula()&&(e.actionClip().equals("drink_beer")||e.actionClip().equals("tipsy_sway")))name+="_cerveja";
   double dx=e.getX()-e.prevX,dz=e.getZ()-e.prevZ;double speed=dx*dx+dz*dz;boolean moving=speed>.00015;
   String clip=e.visualMounted()?(moving?(speed>.012?"mounted_run":"mounted_walk"):"mount_idle"):(moving?(speed>.0324?"run":"walk"):"idle");String gait=clip;float gaitTime=(e.age+delta)/20;float t=gaitTime;
   if(!e.actionClip().isEmpty()){clip=e.actionClip();t=e.actionTime(delta);}
   ms.push();ms.multiply(net.minecraft.util.math.RotationAxis.POSITIVE_Y.rotationDegrees(180-yaw));ms.scale(1/16f,1/16f,1/16f);
   int action=e.shotIn();float raised=e.visualMounted()&&!e.lula()?(action>=0?Math.min(1,(40-action+delta)/30f):action<=-2?Math.max(0,1-(-action-12+delta)/30f):0):-1;
   // Progress itself is linear; BazookaPose eases reach, grip and draw separately.
   boolean overlay=moving&&!e.actionClip().isEmpty()&&!java.util.Set.of("lie_down","settle_sleep","rest_sleep","wake_up","stand_up","transform_cue","drink_beer","tipsy_sway").contains(clip);
   BlockbenchModel.get(name).draw(ms,buffers,light,OverlayTexture.getUv(0,e.hurtTime>0||e.deathTime>0),clip,t,-1,raised>0?raised:-1,overlay?gait:null,gaitTime);
   if(e.visualMounted()&&!e.lula()){
    // Separate prop preserves original character rig; telegraphed aim clears skull.
    ms.push();float aim=raised;
    ms.multiplyPositionMatrix(BlockbenchModel.get(name).attachment("body",clip,t,overlay?gait:null,gaitTime));ms.multiplyPositionMatrix(BazookaPose.weapon(aim));BlockbenchModel.get("bazuca").draw(ms,buffers,light,OverlayTexture.DEFAULT_UV,action<=-2&&action>=-14?"fire":null,Math.max(0,(-action-2+delta)/20f),-1);ms.pop();
   }ms.pop();super.render(e,yaw,delta,ms,buffers,light);
  }
 }
 private static class RocketRenderer extends EntityRenderer<Rocket>{
  RocketRenderer(EntityRendererFactory.Context ctx){super(ctx);}
  public Identifier getTexture(Rocket e){return Terror.id("textures/rig/foguete_bazuca.png");}
  public void render(Rocket e,float yaw,float delta,MatrixStack ms,VertexConsumerProvider buffers,int light){ms.push();var v=e.getVelocity();ms.multiply(net.minecraft.util.math.RotationAxis.POSITIVE_Y.rotation((float)Math.atan2(-v.x,-v.z)));ms.multiply(net.minecraft.util.math.RotationAxis.POSITIVE_X.rotation((float)Math.atan2(v.y,v.horizontalLength())));ms.scale(.06f,.06f,.06f);BlockbenchModel.get("foguete_bazuca").draw(ms,buffers,light,OverlayTexture.DEFAULT_UV,null,0,-1);ms.pop();}
 }
}
