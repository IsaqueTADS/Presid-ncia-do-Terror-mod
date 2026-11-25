package dev.presidencia;
import net.minecraft.item.*;import net.minecraft.registry.*;import net.minecraft.registry.entry.RegistryEntry;import net.minecraft.entity.*;
public final class DirtArmor {
 public static final RegistryEntry<ArmorMaterial> MATERIAL=Registry.registerReference(Registries.ARMOR_MATERIAL,Terror.id("terra"),new ArmorMaterial(java.util.Map.of(ArmorItem.Type.HELMET,4,ArmorItem.Type.CHESTPLATE,10,ArmorItem.Type.LEGGINGS,8,ArmorItem.Type.BOOTS,4,ArmorItem.Type.BODY,10),20,net.minecraft.sound.SoundEvents.ITEM_ARMOR_EQUIP_NETHERITE,()->net.minecraft.recipe.Ingredient.ofItems(Items.DIRT),java.util.List.of(new ArmorMaterial.Layer(Terror.id("terra"))),5f,.15f));
 private static Item piece(String name,ArmorItem.Type type){return Registry.register(Registries.ITEM,Terror.id(name),new ArmorItem(MATERIAL,type,new Item.Settings().maxDamage(type.getMaxDamage(55))){
  @Override public void appendTooltip(ItemStack s,Item.TooltipContext context,java.util.List<net.minecraft.text.Text> lines,net.minecraft.item.tooltip.TooltipType kind){lines.add(net.minecraft.text.Text.literal("Terra compactada: proteção e resistência superiores à netherite."));lines.add(net.minecraft.text.Text.literal("Conjunto completo: reduz dano de queda em 50%."));}
 });}
 public static final Item HELMET=piece("capacete_terra",ArmorItem.Type.HELMET),CHEST=piece("peitoral_terra",ArmorItem.Type.CHESTPLATE),LEGS=piece("calcas_terra",ArmorItem.Type.LEGGINGS),BOOTS=piece("botas_terra",ArmorItem.Type.BOOTS);
 public static boolean fullSet(LivingEntity e){return e.getEquippedStack(EquipmentSlot.HEAD).isOf(HELMET)&&e.getEquippedStack(EquipmentSlot.CHEST).isOf(CHEST)&&e.getEquippedStack(EquipmentSlot.LEGS).isOf(LEGS)&&e.getEquippedStack(EquipmentSlot.FEET).isOf(BOOTS);}
 public static void initialize(){}
}
