package dev.presidencia;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.networking.v1.*;
import net.minecraft.entity.*;
import net.minecraft.item.*;
import net.minecraft.registry.*;
import net.minecraft.util.Identifier;
public class Terror implements ModInitializer {
 public static final String ID="presidencia_terror";
 public static Identifier id(String path){return Identifier.of(ID,path);}
 private static EntityType<President> president(String name){return Registry.register(Registries.ENTITY_TYPE,id(name),EntityType.Builder.create(President::new,SpawnGroup.CREATURE).dimensions(1.1f,3.2f).eyeHeight(2.8f).maxTrackingRange(96).build(ID+":"+name));}
 public static final EntityType<President> LULA=president("lula"), BOLSONARO=president("bolsonaro");
 public static final EntityType<Rocket> ROCKET=Registry.register(Registries.ENTITY_TYPE,id("rocket"),EntityType.Builder.<Rocket>create(Rocket::new,SpawnGroup.MISC).dimensions(.3f,.3f).trackingTickInterval(1).maxTrackingRange(96).build(ID+":rocket"));
 public static final Item BEER=item("cerveja",new DrinkItem());
 public static final Item ALCOHOL=item("alcool",new Item(new Item.Settings().maxCount(16)));
 public static final net.minecraft.recipe.RecipeSerializer<AlcoholRecipe> ALCOHOL_RECIPE=Registry.register(Registries.RECIPE_SERIALIZER,id("alcool"),AlcoholRecipe.SERIALIZER);
 public static final Item BULLET=item("municao_revolver",new Item(new Item.Settings()));
 public static final Item MISSILE=item("foguete_bazuca",new Item(new Item.Settings()));
 public static final Gun REVOLVER=(Gun)item("revolver",new Gun(false));
 public static final Gun BAZOOKA=(Gun)item("bazuca",new Gun(true));
 public static final Item LULA_EGG=item("lula_spawn_egg",new SpawnEggItem(LULA,0x182b48,0xece7dc,new Item.Settings()));
 public static final Item BOLSONARO_EGG=item("bolsonaro_spawn_egg",new SpawnEggItem(BOLSONARO,0x101920,0x16854c,new Item.Settings()));
 private static Item item(String name,Item item){return Registry.register(Registries.ITEM,id(name),item);}
 public void onInitialize(){
  DirtArmor.initialize();
  TerrorSounds.initialize();
  net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents.END_SERVER_TICK.register(Gun::tickReloads);
  FabricDefaultAttributeRegistry.register(LULA,President.attributes());FabricDefaultAttributeRegistry.register(BOLSONARO,President.attributes());
  Registry.register(Registries.ITEM_GROUP,id("items"),net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup.builder().displayName(net.minecraft.text.Text.literal("Presidência do Terror")).icon(()->new ItemStack(BEER)).entries((ctx,e)->{for(var item:new Item[]{REVOLVER,BAZOOKA,BULLET,MISSILE,BEER,ALCOHOL,LULA_EGG,BOLSONARO_EGG,DirtArmor.HELMET,DirtArmor.CHEST,DirtArmor.LEGS,DirtArmor.BOOTS})e.add(item);}).build());
  StalkerDirector.initialize();
  net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents.END_SERVER_TICK.register(DamageNumbers::tick);
  PayloadTypeRegistry.playC2S().register(ReloadPacket.ID,ReloadPacket.CODEC);
  ServerPlayNetworking.registerGlobalReceiver(ReloadPacket.ID,(payload,ctx)->ctx.server().execute(()->{var p=ctx.player();if(p.getMainHandStack().getItem() instanceof Gun gun)gun.reload(p,p.getMainHandStack());}));
 }
}
