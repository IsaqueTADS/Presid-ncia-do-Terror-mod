package dev.presidencia;
public final class TerrorConfig {
 public boolean autoSpawn=true,teleport=true,destroyBlocks=true;public int voiceMinSeconds=45,voiceMaxSeconds=90,teleportSeconds=30;public float voiceVolume=.8f;
 private static TerrorConfig value=new TerrorConfig();public static TerrorConfig get(){return value;}
 public static void load(){value=new TerrorConfig();var p=net.fabricmc.loader.api.FabricLoader.getInstance().getConfigDir().resolve("presidencia-terror.json");var g=new com.google.gson.GsonBuilder().setPrettyPrinting().create();try{if(java.nio.file.Files.exists(p)){var parsed=g.fromJson(java.nio.file.Files.readString(p),TerrorConfig.class);if(parsed!=null)value=parsed;}else java.nio.file.Files.writeString(p,g.toJson(value));}catch(Exception e){throw new IllegalStateException("Invalid presidencia-terror.json",e);}value.teleportSeconds=Math.max(1,value.teleportSeconds);value.voiceMinSeconds=Math.max(10,value.voiceMinSeconds);value.voiceMaxSeconds=Math.max(value.voiceMinSeconds,value.voiceMaxSeconds);value.voiceVolume=Math.clamp(value.voiceVolume,0,2);}
}
