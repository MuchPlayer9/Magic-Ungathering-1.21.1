package net.mp9.magicungathering.item;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.mp9.magicungathering.MagicUngathering;
import net.mp9.magicungathering.attributes.ModAttributes;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MagicUngathering.MOD_ID);

    // items
    public static final DeferredItem<Item> SPEED_SWORD = ITEMS.register("speed_sword",
            () -> new SpeedSword() {
        @Override
        public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
            tooltipComponents.add(Component.translatable("tooltip.magicungathering.speed_sword.tooltip"));
            super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        }
    });

    public static final DeferredItem<Item> STRENGTH_SWORD = ITEMS.register("strength_sword",
            () -> new StrengthSword() {
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("tooltip.magicungathering.strength_sword.tooltip"));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }
            });

    public static final DeferredItem<Item> JUMP_SWORD = ITEMS.register("jump_sword",
            () -> new JumpSword() {
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("tooltip.magicungathering.jump_sword.tooltip"));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }
            });

    public static final DeferredItem<Item> CRYSTAL_SWORD = ITEMS.register("crystal_sword",
            () -> new CrystalSword() {
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("tooltip.magicungathering.crystal_sword.tooltip"));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }
            });

    public static final DeferredItem<Item> MANA_STICK = ITEMS.register("mana_stick",
            () -> new ManaStick() {
            @Override
            public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                tooltipComponents.add(Component.translatable("tooltip.magicungathering.mana_stick.tooltip"));
                super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
            }
        });

    public static final DeferredItem<Item> DASH_SWORD = ITEMS.register("dash_sword",
            () -> new DashSword() {
                @Override
                public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("tooltip.magicungathering.dash_sword.tooltip"));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }
            });

    public static final DeferredItem<Item> FLASH_STEP_SWORD = ITEMS.register("flash_step_sword",
            () -> new FlashStepSword() {
                @Override
                public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("tooltip.magicungathering.flash_step_sword.tooltip"));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }
            });

    public static final DeferredItem<Item> ATTACK_SPEED_SWORD = ITEMS.register("attack_speed_sword",
            () -> new AttackSpeedSword() {
                @Override
                public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("tooltip.magicungathering.attack_speed_sword.tooltip"));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }
            });



    // armor
    public static final DeferredItem<Item> SLIME_BOOTS = ITEMS.register("slime_boots",
            () -> new ArmorItem(Holder.direct(ModArmorMaterials.SLIME), ArmorItem.Type.BOOTS, new Item.Properties().stacksTo(1)) {
                @Override
                public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("tooltip.magicungathering.slime_boots.tooltip"));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }
            });

    public static final DeferredItem<Item> TIER_ONE_ROBE = ITEMS.register("tier_one_robe",
            () -> new ArmorItem(
                    Holder.direct(ModArmorMaterials.TIER_ONE),
                    ArmorItem.Type.CHESTPLATE,
                    new Item.Properties()
                            .stacksTo(1)
                        .attributes(ItemAttributeModifiers.builder()
                            // add mana bonus
                            .add(ModAttributes.MAX_MANA,
                                    new AttributeModifier(
                                            ResourceLocation.fromNamespaceAndPath("magicungathering", "tier_one_robe_mana_bonus"),
                                            30.0, // the amount of extra mana
                                            AttributeModifier.Operation.ADD_VALUE),
                                    EquipmentSlotGroup.CHEST)
                            // Re-add standard armor points since attributes overrides defaults
                                    .add(Attributes.ARMOR,
                                            new AttributeModifier(
                                                    ResourceLocation.withDefaultNamespace("armor"),
                                                    3.0,
                                                    AttributeModifier.Operation.ADD_VALUE),
                                            EquipmentSlotGroup.CHEST)
                            .build())));

    public static final DeferredItem<Item> TIER_TWO_ROBE = ITEMS.register("tier_two_robe",
            () -> new ArmorItem(
                    Holder.direct(ModArmorMaterials.TIER_TWO),
                    ArmorItem.Type.CHESTPLATE,
                    new Item.Properties()
                            .stacksTo(1)
                            .attributes(ItemAttributeModifiers.builder()
                                    // add mana bonus
                                    .add(ModAttributes.MAX_MANA,
                                            new AttributeModifier(
                                                    ResourceLocation.fromNamespaceAndPath("magicungathering", "tier_two_robe_mana_bonus"),
                                                    50.0, // the amount of extra mana
                                                    AttributeModifier.Operation.ADD_VALUE),
                                            EquipmentSlotGroup.CHEST)
                                    // Re-add standard armor points since attributes overrides defaults
                                    .add(Attributes.ARMOR,
                                            new AttributeModifier(
                                                    ResourceLocation.withDefaultNamespace("armor"),
                                                    4.0,
                                                    AttributeModifier.Operation.ADD_VALUE),
                                            EquipmentSlotGroup.CHEST)
                                    .build())));

    public static final DeferredItem<Item> CREATIVE_LEGGINGS = ITEMS.register("creative_leggings",
            () -> new ArmorItem(
                    Holder.direct(ModArmorMaterials.CREATIVE),
                    ArmorItem.Type.LEGGINGS,
                    new Item.Properties()
                            .stacksTo(1)
                            .attributes(ItemAttributeModifiers.builder()
                                    // add mana bonus
                                    .add(ModAttributes.MAX_MANA,
                                            new AttributeModifier(
                                                    ResourceLocation.fromNamespaceAndPath("magicungathering", "creative_leggings_mana_bonus"),
                                                    50000.0, // the amount of extra mana
                                                    AttributeModifier.Operation.ADD_VALUE),
                                            EquipmentSlotGroup.LEGS)
                                    // Re-add standard armor points since attributes overrides defaults
                                    .add(Attributes.ARMOR,
                                            new AttributeModifier(
                                                    ResourceLocation.withDefaultNamespace("armor"),
                                                    4.0,
                                                    AttributeModifier.Operation.ADD_VALUE),
                                            EquipmentSlotGroup.LEGS)
                                    .build())));

    public static void register(IEventBus eventBus) {ITEMS.register(eventBus);}
}
