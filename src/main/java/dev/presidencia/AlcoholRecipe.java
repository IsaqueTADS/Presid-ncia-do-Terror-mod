package dev.presidencia;
import net.minecraft.recipe.*;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.item.*;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.potion.Potions;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.world.World;
/** Fictional shapeless game recipe: only actual water bottles, never arbitrary potions. */
public final class AlcoholRecipe extends ShapelessRecipe {
 public AlcoholRecipe(CraftingRecipeCategory category){super("",category,new ItemStack(Terror.ALCOHOL),ingredients());}
 private static net.minecraft.util.collection.DefaultedList<Ingredient> ingredients(){var list=net.minecraft.util.collection.DefaultedList.<Ingredient>of();list.add(Ingredient.ofItems(Items.POTION));list.add(Ingredient.ofItems(Items.SUGAR_CANE));return list;}
 public AlcoholRecipe(ShapelessRecipe base){super(base.getGroup(),base.getCategory(),base.getResult(null),base.getIngredients());}
 public static final RecipeSerializer<AlcoholRecipe> SERIALIZER=new RecipeSerializer<>(){
  public com.mojang.serialization.MapCodec<AlcoholRecipe> codec(){return RecipeSerializer.SHAPELESS.codec().xmap(AlcoholRecipe::new,recipe->recipe);}
  public net.minecraft.network.codec.PacketCodec<net.minecraft.network.RegistryByteBuf,AlcoholRecipe> packetCodec(){return RecipeSerializer.SHAPELESS.packetCodec().xmap(AlcoholRecipe::new,recipe->recipe);}
 };
 public boolean matches(CraftingRecipeInput input,World world){
  int water=0,cane=0;for(var s:input.getStacks()){if(s.isEmpty())continue;
   if(s.isOf(Items.SUGAR_CANE))cane++;
   else if(s.isOf(Items.POTION)&&s.get(DataComponentTypes.POTION_CONTENTS)!=null&&s.get(DataComponentTypes.POTION_CONTENTS).matches(Potions.WATER))water++;
   else return false;
  }return water==1&&cane==1;
 }
 public ItemStack craft(CraftingRecipeInput input,RegistryWrapper.WrapperLookup lookup){return new ItemStack(Terror.ALCOHOL);}
 public boolean fits(int width,int height){return width*height>=2;}
 public RecipeSerializer<?> getSerializer(){return Terror.ALCOHOL_RECIPE;}
}
