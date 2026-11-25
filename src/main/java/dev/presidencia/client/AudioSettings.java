package dev.presidencia.client;
public final class AudioSettings {
 public float general=1,voices=1,mounts=.85f,weapons=.8f;
 private static AudioSettings current=new AudioSettings();public static AudioSettings get(){return current;}
 public float gain(int channel){return general*(channel==0?voices:channel==1?mounts:weapons);}
 private static java.nio.file.Path path(){return net.fabricmc.loader.api.FabricLoader.getInstance().getConfigDir().resolve("presidencia-audio-client.json");}
 public static void load(){try{if(java.nio.file.Files.exists(path())){var read=new com.google.gson.Gson().fromJson(java.nio.file.Files.readString(path()),AudioSettings.class);if(read!=null)current=read;}}catch(Exception e){org.slf4j.LoggerFactory.getLogger("presidencia-audio").warn("Invalid client audio settings; using defaults",e);}current.general=clamp(current.general);current.voices=clamp(current.voices);current.mounts=clamp(current.mounts);current.weapons=clamp(current.weapons);}
 private static float clamp(float f){return Float.isFinite(f)?Math.clamp(f,0,1):1;}
 public static void reset(){current=new AudioSettings();}
 public static void save(){try{java.nio.file.Files.writeString(path(),new com.google.gson.GsonBuilder().setPrettyPrinting().create().toJson(current));}catch(Exception e){org.slf4j.LoggerFactory.getLogger("presidencia-audio").warn("Cannot save audio settings",e);}}
}
