package dev.presidencia.client;

import com.google.gson.*;
import dev.presidencia.Terror;
import dev.presidencia.BazookaPose;
import java.util.*;
import java.io.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;
import org.joml.Vector3f;

/** Read-only runtime export: cuboids and actual polygon meshes share one rig evaluator. */
public final class BlockbenchModel {
 private static final Gson GSON=new Gson();
 private static final Map<String,BlockbenchModel> CACHE=new HashMap<>();
 private final JsonObject source;
 private final Map<String,JsonObject> bones=new HashMap<>();
 private final Map<String,String> parents=new HashMap<>(),owners=new HashMap<>();
 private final List<Identifier> textures=new ArrayList<>();
 private final boolean legacy;
 public static void clear(){CACHE.clear();}
 public static BlockbenchModel get(String name){return CACHE.computeIfAbsent(name,BlockbenchModel::new);}
 private BlockbenchModel(String name){
  try(var reader=new InputStreamReader(MinecraftClient.getInstance().getResourceManager().getResource(Terror.id("rigs/"+name+".json")).orElseThrow().getInputStream(),java.nio.charset.StandardCharsets.UTF_8)){source=JsonParser.parseReader(reader).getAsJsonObject();}catch(IOException ex){throw new IllegalStateException("Missing rig "+name,ex);}
  legacy=source.getAsJsonObject("meta").has("animation_coordinates");
  for(var bone:source.getAsJsonArray("outliner"))visit(bone.getAsJsonObject(),null);
  for(var texture:source.getAsJsonArray("textures"))textures.add(Terror.id("textures/rig/"+texture.getAsJsonObject().get("name").getAsString()));
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
 public void draw(MatrixStack matrices,VertexConsumerProvider buffers,int light,int overlay,String clip,float time,int rounds){
  draw(matrices,buffers,light,overlay,clip,time,rounds,-1);
 }
 public void draw(MatrixStack matrices,VertexConsumerProvider buffers,int light,int overlay,String clip,float time,int rounds,float armRaise){
  draw(matrices,buffers,light,overlay,clip,time,rounds,armRaise,null,0);
 }
 public void draw(MatrixStack matrices,VertexConsumerProvider buffers,int light,int overlay,String clip,float time,int rounds,float armRaise,String baseClip,float baseTime){
  var tracks=layeredTracks(clip,time,baseClip,baseTime);
  var memo=new HashMap<String,Matrix4f>();
  if(armRaise>=0)for(var b:bones.entrySet()){
   String n=b.getValue().get("name").getAsString();
   if(n.equals("arm_right")||n.equals("arm_left"))tracks.computeIfAbsent(b.getKey(),k->new HashMap<>()).put("rotation",n.equals("arm_right")?new Vector3f(BazookaPose.armAngle(armRaise),0,0):new Vector3f(-25*armRaise,0,15*armRaise));
  }
  for(var element:source.getAsJsonArray("elements")){
   var e=element.getAsJsonObject();String name=e.get("name").getAsString();var transform=new Matrix4f(bone(owners.get(e.get("uuid").getAsString()),tracks,memo)).mul(transform(vec(e,"origin"),vec(e,"rotation"),new Vector3f(),new Vector3f(1)));
   var vertices=new HashMap<String,Vector3f>();boolean mesh=e.get("type").getAsString().equals("mesh");
   if(mesh)for(var v:e.getAsJsonObject("vertices").entrySet())vertices.put(v.getKey(),vector(v.getValue().getAsJsonArray()));
   else{var a=vec(e,"from");var b=vec(e,"to");for(int i=0;i<8;i++)vertices.put(""+i,new Vector3f((i&4)==0?a.x:b.x,(i&2)==0?a.y:b.y,(i&1)==0?a.z:b.z));}
   for(var v:vertices.values())transform.transformPosition(v);
   Map<String,String[]> sides=Map.of("west",new String[]{"0","1","3","2"},"east",new String[]{"4","6","7","5"},"down",new String[]{"0","4","5","1"},"up",new String[]{"2","3","7","6"},"north",new String[]{"0","2","6","4"},"south",new String[]{"1","5","7","3"});
   for(var f:e.getAsJsonObject("faces").entrySet()){
    var face=f.getValue().getAsJsonObject();if(!face.has("texture")||face.get("texture").isJsonNull())continue;
    String[] ids=mesh?GSON.fromJson(face.get("vertices"),String[].class):sides.get(f.getKey());
    boolean loaded=rounds>=0&&(name.startsWith("camara_visual_")||name.startsWith("cartucho_visual_"))&&Integer.parseInt(name.substring(name.lastIndexOf('_')+1))<rounds;
    var vc=buffers.getBuffer(RenderLayer.getEntityCutoutNoCull(loaded?Terror.id("textures/white.png"):textures.get(face.get("texture").getAsInt())));
    var normal=new Vector3f(vertices.get(ids[1])).sub(vertices.get(ids[0])).cross(new Vector3f(vertices.get(ids[2])).sub(vertices.get(ids[0]))).normalize();
    for(int i=0;i<4;i++){
     int j=Math.min(i,ids.length-1);var p=vertices.get(ids[j]);float u,v;
     if(loaded){u=v=.5f;}else if(mesh){var uv=face.getAsJsonObject("uv").getAsJsonArray(ids[j]);u=uv.get(0).getAsFloat()/128;v=uv.get(1).getAsFloat()/128;}else{var uv=face.getAsJsonArray("uv");u=uv.get(i==1||i==2?2:0).getAsFloat()/128;v=uv.get(i>=2?3:1).getAsFloat()/128;}
     vc.vertex(matrices.peek().getPositionMatrix(),p.x,p.y,p.z).color(loaded?0xffffcd45:0xffffffff).texture(u,v).overlay(overlay).light(light).normal(matrices.peek(),normal.x,normal.y,normal.z);
    }
   }
  }
 }
}
