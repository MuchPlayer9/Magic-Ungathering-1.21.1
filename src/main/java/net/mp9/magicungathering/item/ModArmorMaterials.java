package net.mp9.magicungathering.item;

import net.mp9.magicungathering.MagicUngathering;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import java.util.EnumMap;
import java.util.List;

public class ModArmorMaterials {
    // Tier 1 & 2
    public static final ArmorMaterial TIER_ONE = createRobeMaterial("tier_one_robe", 2, 6, 7, 3);
    public static final ArmorMaterial TIER_TWO = createRobeMaterial("tier_two_robe", 3, 6, 8, 3);

    // Specialty Pieces
    // Slime: 2 Armor (Boots) | Creative: 6 Armor (Leggings)
    public static final ArmorMaterial SLIME = createRobeMaterial("slime_boots", 2, 0, 0, 0);
    public static final ArmorMaterial CREATIVE = createRobeMaterial("creative_leggings", 0, 6, 0, 0);

    private static ArmorMaterial createRobeMaterial(String textureName, int boots, int legs, int chest, int helm) {
        return new ArmorMaterial(
                Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
                    map.put(ArmorItem.Type.BOOTS, boots);
                    map.put(ArmorItem.Type.LEGGINGS, legs);
                    map.put(ArmorItem.Type.CHESTPLATE, chest);
                    map.put(ArmorItem.Type.HELMET, helm);
                }),
                15,
                SoundEvents.ARMOR_EQUIP_LEATHER,
                () -> Ingredient.of(Items.LEATHER),
                List.of(new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath(MagicUngathering.MOD_ID, textureName))),
                0.0F,
                0.0F
        );
    }
}