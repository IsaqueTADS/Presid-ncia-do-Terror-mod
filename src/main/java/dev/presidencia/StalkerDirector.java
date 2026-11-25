package dev.presidencia;
import java.util.*;import net.minecraft.nbt.NbtCompound;import net.minecraft.entity.*;import net.minecraft.server.*;import net.minecraft.server.world.ServerWorld;import net.minecraft.server.network.ServerPlayerEntity;import net.minecraft.util.math.*;import net.minecraft.world.TeleportTarget;
/** One persistent pair per server; generation tokens reject unloaded stale bodies on return. */
public final class StalkerDirector {
 private static final Map<MinecraftServer,Map<Boolean,President>> LIVE=new WeakHashMap<>();
 public static StalkerState state(MinecraftServer s){return s.getOverworld().getPersistentStateManager().getOrCreate(StalkerState.TYPE,"presidencia_pair");}
 private static NbtCompound record(MinecraftServer s,boolean lula){var state=state(s);String key=lula?"Lula":"Bolsonaro";if(!state.data.contains(key))state.data.put(key,new NbtCompound());return state.data.getCompound(key);}
 public static void reset(MinecraftServer s){for(boolean lula:new boolean[]{true,false}){var n=record(s,lula);int generation=n.getInt("Generation")+1;var p=LIVE.getOrDefault(s,Map.of()).get(lula);if(p!=null)p.discard();n.remove("Snapshot");n.remove("Followed");n.remove("NextRecovery");n.putBoolean("Defeated",false);n.putInt("Generation",generation);}LIVE.remove(s);state(s).data.putBoolean("Victory",false);state(s).markDirty();}
 public static void afterRespawn(ServerPlayerEntity player){var s=player.getServer();for(boolean lula:new boolean[]{true,false}){var n=record(s,lula);var p=LIVE.getOrDefault(s,Map.of()).get(lula);if(p!=null&&p.getTarget()!=null&&p.getTarget().getUuid().equals(player.getUuid())){p.clearAggression();checkpoint(p);}var snapshot=n.getCompound("Snapshot");if(snapshot.containsUuid("Aggressor")&&snapshot.getUuid("Aggressor").equals(player.getUuid()))snapshot.remove("Aggressor");}state(s).markDirty();}
 public static void initialize(){
  net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer,newPlayer,alive)->afterRespawn(newPlayer));
  net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback.EVENT.register((dispatcher,access,environment)->dispatcher.register(net.minecraft.server.command.CommandManager.literal("presidencia").requires(source->source.hasPermissionLevel(2)).then(net.minecraft.server.command.CommandManager.literal("reset").executes(ctx->{reset(ctx.getSource().getServer());ctx.getSource().sendFeedback(()->net.minecraft.text.Text.literal("Dupla reiniciada; reaparecerá perto de um jogador."),true);return 1;}))));
  net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents.SERVER_STARTED.register(s->TerrorConfig.load());
  net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents.SERVER_STOPPING.register(s->{for(var p:LIVE.getOrDefault(s,Map.of()).values())checkpoint(p);});
  net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents.SERVER_STOPPED.register(s->{LIVE.remove(s);Voices.clear(s);DamageNumbers.clear(s);});
  net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents.END_SERVER_TICK.register(StalkerDirector::tick);
  net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents.ENTITY_LOAD.register((e,w)->{if(e instanceof President p&&p.managed)claim(p);if(!DamageNumbers.creating&&e.getCommandTags().contains("presidencia_damage_number"))e.discard();});
 }
 private static void claim(President p){var s=p.getServer();var n=record(s,p.lula());var map=LIVE.computeIfAbsent(s,k->new HashMap<>());var current=map.get(p.lula());
  if(n.getBoolean("Defeated")||p.generation!=n.getInt("Generation")||(current!=null&&current!=p&&!current.isRemoved())){p.discard();return;}map.put(p.lula(),p);
 }
 public static void checkpoint(President p){if(!p.managed||p.isRemoved())return;var s=p.getServer();var n=record(s,p.lula());if(p.generation!=n.getInt("Generation"))return;n.put("Snapshot",p.writeNbt(new NbtCompound()));state(s).markDirty();}
 public static void defeated(President p){if(!p.managed)return;var s=p.getServer();record(s,p.lula()).putBoolean("Defeated",true);state(s).markDirty();if(record(s,true).getBoolean("Defeated")&&record(s,false).getBoolean("Defeated")){state(s).data.putBoolean("Victory",true);for(var player:s.getPlayerManager().getPlayerList()){player.sendMessage(net.minecraft.text.Text.literal("Fim do mandato — Lula e Bolsonaro derrotados!"),false);var advancement=s.getAdvancementLoader().get(Terror.id("fim_do_mandato"));if(advancement!=null)player.getAdvancementTracker().grantCriterion(advancement,"victory");}}}
 public static ServerPlayerEntity companion(President p){if(p.followed==null)return null;var t=p.getServer().getPlayerManager().getPlayer(p.followed);return t!=null&&t.isAlive()&&!t.isSpectator()&&t.getWorld()==p.getWorld()?t:null;}
 public static Vec3d safePosition(ServerWorld w,ServerPlayerEntity player,President p){
  for(int attempt=0;attempt<48;attempt++){double angle=attempt*Math.PI*2/16+(p.lula()?0:Math.PI);double distance=12+(attempt%3)*4;int x=(int)Math.floor(player.getX()+Math.cos(angle)*distance),z=(int)Math.floor(player.getZ()+Math.sin(angle)*distance);
   for(int dy=12;dy>=-12;dy--){BlockPos at=new BlockPos(x,player.getBlockY()+dy,z);if(!w.getWorldBorder().contains(at)||!w.getBlockState(at.down()).isSolidBlock(w,at.down())||!w.getFluidState(at.down()).isEmpty())continue;Vec3d pos=Vec3d.ofBottomCenter(at);var box=p.getDimensions(p.getPose()).getBoxAt(pos);if(w.isSpaceEmpty(p,box)&&!w.containsFluid(box)&&!java.util.Set.of(net.minecraft.block.Blocks.MAGMA_BLOCK,net.minecraft.block.Blocks.CACTUS,net.minecraft.block.Blocks.CAMPFIRE,net.minecraft.block.Blocks.SOUL_CAMPFIRE).contains(w.getBlockState(at.down()).getBlock()))return pos;}
  }return null;
 }
 public static void tick(MinecraftServer s){
  if(s.getTicks()%20!=0)return;step(s,s.getPlayerManager().getPlayerList(),s.getOverworld().getTime());
 }
 public static void step(MinecraftServer s,List<ServerPlayerEntity> players,long now){var state=state(s);var map=LIVE.computeIfAbsent(s,k->new HashMap<>());
  for(boolean lula:new boolean[]{true,false}){
   var n=record(s,lula);var p=map.get(lula);if(p!=null&&!p.isRemoved()){if(p.getTarget()!=null&&!p.getTarget().isAlive())p.clearAggression();checkpoint(p);}
   if(n.getBoolean("Defeated"))continue;
   var candidates=players.stream().filter(e->e.isAlive()&&!e.isSpectator()).toList();if(candidates.isEmpty())continue;
   var target=p!=null&&p.getTarget() instanceof ServerPlayerEntity t&&t.isAlive()?t:n.containsUuid("Followed")?s.getPlayerManager().getPlayer(n.getUuid("Followed")):null;
   if(target==null||!target.isAlive()||target.isSpectator()||!candidates.contains(target)){if(p!=null&&!p.isRemoved())p.clearAggression();target=candidates.getFirst();}n.putUuid("Followed",target.getUuid());
   if(p!=null&&!p.isRemoved()){p.followed=target.getUuid();if(p.getWorld()==target.getWorld()&&p.squaredDistanceTo(target)<=48*48){state.markDirty();continue;}}
   boolean first=!n.contains("Snapshot");if(first&&(!TerrorConfig.get().autoSpawn||s.getOverworld().getTimeOfDay()<24000))continue;
   if(!first&&(!TerrorConfig.get().teleport||now<n.getLong("NextRecovery")))continue;
   if(p==null||p.isRemoved()){
    var next=(lula?Terror.LULA:Terror.BOLSONARO).create(target.getServerWorld());if(next==null)continue;if(!first)next.readNbt(n.getCompound("Snapshot"));
    var pos=safePosition(target.getServerWorld(),target,next);if(pos==null)continue;
    next.managed=true;next.generation=n.getInt("Generation")+1;next.followed=target.getUuid();n.putInt("Generation",next.generation);next.refreshPositionAndAngles(pos.x,pos.y,pos.z,0,0);map.put(lula,next);if(!target.getServerWorld().spawnEntity(next)){map.remove(lula);continue;}p=next;
   }else{
    var pos=safePosition(target.getServerWorld(),target,p);if(pos==null)continue;
    checkpoint(p);map.remove(lula);var moved=p.teleportTo(new TeleportTarget(target.getServerWorld(),pos,Vec3d.ZERO,p.getYaw(),p.getPitch(),TeleportTarget.ADD_PORTAL_CHUNK_TICKET));if(!(moved instanceof President next))continue;p=next;map.put(lula,p);p.followed=target.getUuid();
   }
   n.putLong("NextRecovery",now+TerrorConfig.get().teleportSeconds*20L);checkpoint(p);state.markDirty();
  }
 }
}
