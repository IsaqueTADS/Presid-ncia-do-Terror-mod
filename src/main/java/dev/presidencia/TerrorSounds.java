package dev.presidencia;
import java.util.*;
import net.minecraft.registry.*;
import net.minecraft.sound.*;
public final class TerrorSounds {
 private static final Map<String,SoundEvent> EVENTS=new HashMap<>();
 public static void initialize(){for(String name:List.of("human_step","donkey_step","trex_step","donkey_voice","trex_voice","creature_hurt","transform","swish","impact","reload","lula_food","lula_taunt","lula_react","bolsonaro_phone","bolsonaro_laugh","bolsonaro_taunt"))EVENTS.put(name,Registry.register(Registries.SOUND_EVENT,Terror.id(name),SoundEvent.of(Terror.id(name))));}
 public static SoundEvent get(String name){return EVENTS.get(name);}
 private TerrorSounds(){}
}
