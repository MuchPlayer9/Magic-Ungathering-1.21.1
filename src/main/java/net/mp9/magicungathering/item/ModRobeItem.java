package net.mp9.magicungathering.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.mp9.magicungathering.attributes.ModAttributes;

import java.util.List;

public class ModRobeItem extends ArmorItem {

    public ModRobeItem(Holder<ArmorMaterial> material, Type type, Properties properties) {
        super(material, type, properties);
    }

    @Override
    public ItemAttributeModifiers getDefaultAttributeModifiers() {
        ItemAttributeModifiers.Builder builder = ItemAttributeModifiers.builder();

        // 1. Standard armor values (Defense/Toughness)
        super.getDefaultAttributeModifiers().modifiers().forEach(entry ->
                builder.add(entry.attribute(), entry.modifier(), entry.slot())
        );

        boolean isTierTwo = this.getMaterial().value() == ModArmorMaterials.TIER_TWO;
        String slotName = this.getType().getSlot().getName();

        // 2. Add Knockback Resistance to Tier 2
        if (isTierTwo) {
            builder.add(net.minecraft.world.entity.ai.attributes.Attributes.KNOCKBACK_RESISTANCE,
                    new AttributeModifier(
                            ResourceLocation.fromNamespaceAndPath("magicungathering", "robe_kb_res_" + slotName),
                            0.05, // 0.5 is 5% resistance per piece
                            AttributeModifier.Operation.ADD_VALUE),
                    EquipmentSlotGroup.bySlot(this.getType().getSlot())
            );
        }

        // 3. Mana Bonus Logic (Using unique IDs to prevent interrupting)
        double manaBonus = calculateManaBonus(isTierTwo);
        if (manaBonus > 0) {
            builder.add(ModAttributes.MAX_MANA, new AttributeModifier(
                            ResourceLocation.fromNamespaceAndPath("magicungathering", "robe_mana_" + slotName),
                            manaBonus,
                            AttributeModifier.Operation.ADD_VALUE),
                    EquipmentSlotGroup.bySlot(this.getType().getSlot())
            );
        }

        return builder.build();
    }

    private double calculateManaBonus(boolean isTierTwo) {
        return switch (this.getType()) {
            case HELMET -> isTierTwo ? 35.0 : 20.0;
            case CHESTPLATE -> isTierTwo ? 45.0 : 25.0;
            case LEGGINGS -> isTierTwo ? 40.0 : 20.0;
            case BOOTS -> isTierTwo ? 30.0 : 15.0;
            default -> 0.0;
        };
    }

    /**
     * DYNAMIC TOOLTIP
     * Now explains that any combination of robes grants the protection bonus.
     */
    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.empty());

        // Header
        tooltipComponents.add(Component.literal("Set Bonus: Spell Shield")
                .withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD));

        // Description - Shows 30% for Tier 2 pieces, but notes that mixed sets work.
        int percent = (this.getMaterial().value() == ModArmorMaterials.TIER_TWO) ? 30 : 15;

        tooltipComponents.add(Component.literal("Wearing any full robe set reduces ")
                .withStyle(ChatFormatting.GRAY));

        tooltipComponents.add(Component.literal("Magic Damage ")
                .withStyle(ChatFormatting.BLUE)
                .append(Component.literal("by " + percent + "%*")
                        .withStyle(ChatFormatting.GRAY)));

        tooltipComponents.add(Component.literal("*Bonus based on lowest tier piece equipped.")
                .withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC));

        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }

    /**
     * Helper to check if a player is wearing a SPECIFIC full set (used for Particles/Max Bonus)
     */
    public static boolean hasFullSet(Player player, ArmorMaterial material) {
        for (ItemStack stack : player.getArmorSlots()) {
            if (stack.isEmpty()) return false;
            if (!(stack.getItem() instanceof ArmorItem armor) || armor.getMaterial().value() != material) {
                return false;
            }
        }
        return true;
    }

    /**
     * Helper to check if the player is wearing ANY combination of robes (Mixed Set)
     */
    public static boolean isWearingAnyRobeSet(Player player) {
        for (ItemStack stack : player.getArmorSlots()) {
            // Checks if every armor slot contains a ModRobeItem
            if (stack.isEmpty() || !(stack.getItem() instanceof ModRobeItem)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return stack.isEnchanted();
    }
}