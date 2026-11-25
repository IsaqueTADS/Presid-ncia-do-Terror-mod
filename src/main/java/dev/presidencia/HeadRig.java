package dev.presidencia;

import com.google.gson.*;
import dev.presidencia.Terror;
import dev.presidencia.BazookaPose;
import java.util.*;
import java.io.*;
import org.joml.Matrix4f;
import org.joml.Vector3f;

/** Read-only runtime export: cuboids and actual polygon meshes share one rig evaluator. */
public final class HeadRig {
 private static final Gson GSON=new Gson();
 private static final Map<String,HeadRig> CACHE=new HashMap<>();
 private final JsonObject source;
 private final Map<String,JsonObject> bones=new HashMap<>();
 private final Map<String,String> parents=new HashMap<>(),owners=new HashMap<>();
 private final boolean legacy;
 public static void clear(){CACHE.clear();}
 public static HeadRig get(String name){return CACHE.computeIfAbsent(name,HeadRig::new);}
 private HeadRig(String name){
  try(var reader=new InputStreamReader(HeadRig.class.getResourceAsStream("/assets/presidencia_terror/rigs/"+name+".json"),java.nio.charset.StandardCharsets.UTF_8)){source=JsonParser.parseReader(reader).getAsJsonObject();}catch(IOException ex){throw new IllegalStateException("Missing rig "+name,ex);}
  legacy=source.getAsJsonObject("meta").has("animation_coordinates");
  for(var bone:source.getAsJsonArray("outliner"))visit(bone.getAsJsonObject(),null);
 }
 private void visit(JsonObject b,String parent){String id=b.get("uuid").getAsString();bones.put(id,b);if(parent!=null)parents.put(id,parent);for(var child:b.getAsJsonArray("children")){if(child.isJsonPrimitive())owners.put(child.getAsString(),id);else visit(child.getAsJsonObject(),id);}}
 private static Vector3f vector(JsonArray a){return new Vector3f(a.get(0).getAsFloat(),a.get(1).getAsFloat(),a.get(2).getAsFloat());}
 private static Vector3f vec(JsonObject o,String key){return o.has(key)?vector(o.getAsJsonArray(key)):new Vector3f();}
 private static Matrix4f transform(Vector3f pivot,Vector3f rotation,Vector3f pos,Vector3f scale){return new Matrix4f().translation(new Vector3f(pivot).add(pos)).rotateZ((float)Math.toRadians(rotation.z)).rotateY((float)Math.toRadians(rotation.y)).rotateX((float)Math.toRadians(rotation.x)).scale(scale).translate(new Vector3f(pivot).negate());}
 private Map<String,Map<String,Vector3f>> tracks(String clip,float time){
  var result=new HashMap<String,Map<String,Vector3f>>();if(clip==null)return result;
  JsonObject animation=null;for(var c:source.getAsJsonArray("animations"))if(c.getAsJsonObject().get("name").getAsString().endsWith("."+clip)){animation=c.getAsJsonObject();break;}
  if(animation==null)return result;
  float length=animation.get("length").getAsFloat();time=animation.get("loop").getAsString().equals("loop")?time%length:Math.min(length,time);
  for(var entry:animation.getAsJsonObject("animators").entrySet()){
   var channels=new HashMap<String,List<JsonObject>>();
   for(var k:entry.getValue().getAsJsonObject().getAsJsonArray("keyframes")){var key=k.getAsJsonObject();channels.computeIfAbsent(key.get("channel").getAsString(),x->new ArrayList<>()).add(key);}
   var values=new HashMap<String,Vector3f>();
   for(var channel:channels.entrySet()){
    var keys=channel.getValue();keys.sort(Comparator.comparingDouble(k->k.get("time").getAsDouble()));var a=keys.getFirst();var b=keys.getLast();
    for(var k:keys){if(k.get("time").getAsFloat()<=time)a=k;if(k.get("time").getAsFloat()>=time){b=k;break;}}
    float ta=a.get("time").getAsFloat(),tb=b.get("time").getAsFloat(),f=tb==ta?0:(time-ta)/(tb-ta);
    var av=point(a);var bv=point(b);var v=av.lerp(bv,f);
    if(legacy){if(channel.getKey().equals("rotation")){v.x=-v.x;v.y=-v.y;}if(channel.getKey().equals("position"))v.x=-v.x;}
    values.put(channel.getKey(),v);
   }result.put(entry.getKey(),values);
  }return result;
 }
 private static Vector3f point(JsonObject key){var p=key.getAsJsonArray("data_points").get(0).getAsJsonObject();return new Vector3f(p.get("x").getAsFloat(),p.get("y").getAsFloat(),p.get("z").getAsFloat());}
 private Matrix4f bone(String id,Map<String,Map<String,Vector3f>> tracks,Map<String,Matrix4f> memo){
  if(memo.containsKey(id))return memo.get(id);var b=bones.get(id);var tr=tracks.getOrDefault(id,Map.of());
  var mat=parents.containsKey(id)?new Matrix4f(bone(parents.get(id),tracks,memo)):new Matrix4f();
  mat.mul(transform(vec(b,"origin"),vec(b,"rotation").add(tr.getOrDefault("rotation",new Vector3f())),tr.getOrDefault("position",new Vector3f()),tr.getOrDefault("scale",new Vector3f(1))));memo.put(id,mat);return mat;
 }
 private Map<String,Map<String,Vector3f>> layeredTracks(String clip,float time,String baseClip,float baseTime){
  var pose=baseClip==null?tracks(clip,time):tracks(baseClip,baseTime);
  if(baseClip!=null)for(var entry:tracks(clip,time).entrySet())pose.computeIfAbsent(entry.getKey(),k->new HashMap<>()).putAll(entry.getValue());
  return pose;
 }
 public Matrix4f attachment(String boneName,String clip,float time,String baseClip,float baseTime){
  for(var entry:bones.entrySet())if(entry.getValue().get("name").getAsString().equals(boneName))return new Matrix4f(bone(entry.getKey(),layeredTracks(clip,time,baseClip,baseTime),new HashMap<>()));
  return new Matrix4f();
 }
 public boolean hit(net.minecraft.util.math.Vec3d start,net.minecraft.util.math.Vec3d end,String clip,float time,String gait,float gaitTime){
  var pose=layeredTracks(clip,time,gait,gaitTime);var memo=new HashMap<String,Matrix4f>();
  for(var element:source.getAsJsonArray("elements")){
   var e=element.getAsJsonObject();String owner=owners.get(e.get("uuid").getAsString()),id=owner;boolean head=false;
   while(id!=null){if(bones.get(id).get("name").getAsString().equals("head"))head=true;id=parents.get(id);}if(!head)continue;
   var matrix=new Matrix4f(bone(owner,pose,memo)).mul(transform(vec(e,"origin"),vec(e,"rotation"),new Vector3f(),new Vector3f(1))).invert();
   var a=matrix.transformPosition(new Vector3f((float)start.x,(float)start.y,(float)start.z));var b=matrix.transformPosition(new Vector3f((float)end.x,(float)end.y,(float)end.z));
   Vector3f lo,hi;if(e.get("type").getAsString().equals("mesh")){lo=new Vector3f(Float.POSITIVE_INFINITY);hi=new Vector3f(Float.NEGATIVE_INFINITY);for(var v:e.getAsJsonObject("vertices").entrySet()){var p=vector(v.getValue().getAsJsonArray());lo.min(p);hi.max(p);}}else{lo=vec(e,"from");hi=vec(e,"to");}
   if(new net.minecraft.util.math.Box(lo.x,lo.y,lo.z,hi.x,hi.y,hi.z).raycast(new net.minecraft.util.math.Vec3d(a.x,a.y,a.z),new net.minecraft.util.math.Vec3d(b.x,b.y,b.z)).isPresent())return true;
  }return false;
 }
 public static boolean isHead(net.minecraft.entity.LivingEntity entity,net.minecraft.util.math.Vec3d start,net.minecraft.util.math.Vec3d end){
  if(!(entity instanceof President p))return end.y>=entity.getEyeY()-.15&&end.y<=entity.getBoundingBox().maxY+.1;
  String name=p.lula()?"lula":"bolsonaro";if(p.visualMounted())name+=p.lula()?"_jumento":"_trex";
  if(p.lula()&&(p.actionClip().equals("drink_beer")||p.actionClip().equals("tipsy_sway")))name+="_cerveja";
  double dx=p.getX()-p.prevX,dz=p.getZ()-p.prevZ,speed=dx*dx+dz*dz;boolean moving=speed>.00015;String gait=p.visualMounted()?(moving?(speed>.012?"mounted_run":"mounted_walk"):"mount_idle"):(moving?(speed>.0324?"run":"walk"):"idle");String clip=p.actionClip().isEmpty()?gait:p.actionClip();float t=p.actionClip().isEmpty()?p.age/20f:p.actionTime(0);
  boolean overlay=moving&&!p.actionClip().isEmpty()&&!Set.of("lie_down","settle_sleep","rest_sleep","wake_up","stand_up","transform_cue","drink_beer","tipsy_sway").contains(clip);
  float yaw=(float)Math.toRadians(-(180-p.getYaw()));var a=start.subtract(p.getPos()).rotateY(yaw).multiply(16);var b=end.subtract(p.getPos()).rotateY(yaw).multiply(16);
  return get(name).hit(a,b,clip,t,overlay?gait:null,p.age/20f);
 }
}