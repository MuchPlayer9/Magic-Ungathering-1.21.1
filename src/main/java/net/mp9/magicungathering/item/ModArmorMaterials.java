package net.mp9.magicungathering.item;

import net.mp9.magicungathering.MagicUngathering;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

public class ModArmorMaterials {
    public static final ArmorMaterial TIER_ONE = createRobeMaterial("tier_one_robe");
    public static final ArmorMaterial TIER_TWO = createRobeMaterial("tier_two_robe");
    public static final ArmorMaterial SLIME = createRobeMaterial("slime_boots");
    public static final ArmorMaterial CREATIVE = createRobeMaterial("creative_leggings");

    private static ArmorMaterial createRobeMaterial(String textureName) {
        return new ArmorMaterial(

    // 1. Defense Map (Boots, Leggings, Chest, Helmet)
            Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
                map.put(ArmorItem.Type.BOOTS, 1);
                map.put(ArmorItem.Type.LEGGINGS, 2);
                map.put(ArmorItem.Type.CHESTPLATE, 3);
                map.put(ArmorItem.Type.HELMET, 1);
            }),
            // 2. Enchantability
            15,
            // 3. Equip Sound
            SoundEvents.ARMOR_EQUIP_LEATHER,
            // 4. Repair Ingredient Supplier
            () -> Ingredient.of(Items.LEATHER),
            // 5. Texture Layers
            List.of(new ArmorMaterial.Layer(
                    ResourceLocation.fromNamespaceAndPath(MagicUngathering.MOD_ID, textureName),
                    "",false
            )),            // 6. Toughness
            0.0F,
            // 7. Knockback Resistance
            0.0F
    );
}}