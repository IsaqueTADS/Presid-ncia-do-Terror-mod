package dev.presidencia;
import net.minecraft.world.PersistentState;import net.minecraft.nbt.NbtCompound;import net.minecraft.registry.RegistryWrapper;
public final class StalkerState extends PersistentState {
 public static final Type<StalkerState> TYPE=new Type<>(StalkerState::new,StalkerState::read,null);public NbtCompound data=new NbtCompound();
 private static StalkerState read(NbtCompound n,RegistryWrapper.WrapperLookup lookup){var s=new StalkerState();s.data=n.getCompound("Dupla");return s;}
 @Override public NbtCompound writeNbt(NbtCompound n,RegistryWrapper.WrapperLookup lookup){n.put("Dupla",data);return n;}
}
