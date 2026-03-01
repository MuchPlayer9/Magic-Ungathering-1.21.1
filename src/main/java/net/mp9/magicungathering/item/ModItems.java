package net.mp9.magicungathering.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.mp9.magicungathering.MagicUngathering;
import net.mp9.magicungathering.attributes.ModAttributes;
import net.mp9.magicungathering.item.staff.fireball.FireballStaff;
import net.mp9.magicungathering.item.sword.*;
import net.mp9.magicungathering.item.staff.crystal.CrystalLauncher;
import net.mp9.magicungathering.item.staff.crystal.CrystalSword;
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
            tooltipComponents.add(Component.literal("Right Click: ")
                    .withStyle(ChatFormatting.YELLOW, ChatFormatting.BOLD)
                    .append(Component.literal("Speed Boost")
                            .withStyle(style -> style.withBold(false).withColor(ChatFormatting.GOLD))));
            tooltipComponents.add(Component.literal("Grants ")
                    .withStyle(ChatFormatting.GRAY)
                    .append(Component.literal("Speed 2 ")
                            .withStyle(ChatFormatting.WHITE)
                            .append(Component.literal("for ")
                                    .withStyle(ChatFormatting.GRAY)
                                    .append(Component.literal("30s")
                                            .withStyle(ChatFormatting.GREEN)
                                            .append(Component.literal(".")
                                                    .withStyle(ChatFormatting.GRAY))))));
            tooltipComponents.add(Component.literal("Mana Cost: ")
                    .withStyle(ChatFormatting.DARK_GRAY)
                    .append(Component.literal("30")
                            .withStyle(ChatFormatting.DARK_AQUA)));
            tooltipComponents.add(Component.literal("Cooldown: ")
                    .withStyle(ChatFormatting.DARK_GRAY)
                    .append(Component.literal("5s")
                            .withStyle(ChatFormatting.GREEN)));
            super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        }
    });

    public static final DeferredItem<Item> STRENGTH_SWORD = ITEMS.register("strength_sword",
            () -> new StrengthSword() {
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.literal("Right Click: ")
                            .withStyle(ChatFormatting.YELLOW, ChatFormatting.BOLD)
                            .append(Component.literal("Strength Boost")
                                    .withStyle(style -> style.withBold(false).withColor(ChatFormatting.GOLD))));
                    tooltipComponents.add(Component.literal("Grants ")
                            .withStyle(ChatFormatting.GRAY)
                            .append(Component.literal("Strength 2 ")
                                    .withStyle(ChatFormatting.WHITE)
                                    .append(Component.literal("for ")
                                            .withStyle(ChatFormatting.GRAY)
                                            .append(Component.literal("10s")
                                                    .withStyle(ChatFormatting.GREEN)
                                                    .append(Component.literal(".")
                                                            .withStyle(ChatFormatting.GRAY))))));
                    tooltipComponents.add(Component.literal("Mana Cost: ")
                            .withStyle(ChatFormatting.DARK_GRAY)
                            .append(Component.literal("50")
                                    .withStyle(ChatFormatting.DARK_AQUA)));
                    tooltipComponents.add(Component.literal("Cooldown: ")
                            .withStyle(ChatFormatting.DARK_GRAY)
                            .append(Component.literal("5s")
                                    .withStyle(ChatFormatting.GREEN)));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }
            });

    public static final DeferredItem<Item> JUMP_SWORD = ITEMS.register("jump_sword",
            () -> new JumpSword() {
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.literal("Right Click: ")
                            .withStyle(ChatFormatting.YELLOW, ChatFormatting.BOLD)
                            .append(Component.literal("Jump Boost")
                                    .withStyle(style -> style.withBold(false).withColor(ChatFormatting.GOLD))));
                    tooltipComponents.add(Component.literal("Grants ")
                            .withStyle(ChatFormatting.GRAY)
                            .append(Component.literal("Jump Boost 4 ")
                                    .withStyle(ChatFormatting.WHITE)
                                    .append(Component.literal("for ")
                                            .withStyle(ChatFormatting.GRAY)
                                            .append(Component.literal("10s")
                                                    .withStyle(ChatFormatting.GREEN)
                                                    .append(Component.literal(".")
                                                            .withStyle(ChatFormatting.GRAY))))));
                    tooltipComponents.add(Component.literal("Mana Cost: ")
                            .withStyle(ChatFormatting.DARK_GRAY)
                            .append(Component.literal("20")
                                    .withStyle(ChatFormatting.DARK_AQUA)));
                    tooltipComponents.add(Component.literal("Cooldown: ")
                            .withStyle(ChatFormatting.DARK_GRAY)
                            .append(Component.literal("5s")
                                    .withStyle(ChatFormatting.GREEN)));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }
            });

    public static final DeferredItem<Item> CRYSTAL_SWORD = ITEMS.register("crystal_sword",
            () -> new CrystalSword() {
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.literal("Right Click: ")
                            .withStyle(ChatFormatting.YELLOW, ChatFormatting.BOLD)
                            .append(Component.literal("Kamikaze")
                                    .withStyle(style -> style.withBold(false).withColor(ChatFormatting.GOLD))));
                    tooltipComponents.add(Component.literal("Spawns an ")
                            .withStyle(ChatFormatting.GRAY)
                            .append(Component.literal("End Crystal ")
                                    .withStyle(ChatFormatting.WHITE)
                                    .append(Component.literal("on the player with ")
                                            .withStyle(ChatFormatting.GRAY)
                                            .append(Component.literal("0.5s ")
                                                    .withStyle(ChatFormatting.GREEN)
                                                    .append(Component.literal("before it can explode.")
                                                            .withStyle(ChatFormatting.GRAY))))));
                    tooltipComponents.add(Component.literal("Mana Cost: ")
                            .withStyle(ChatFormatting.DARK_GRAY)
                            .append(Component.literal("50")
                                    .withStyle(ChatFormatting.DARK_AQUA)));
                    tooltipComponents.add(Component.literal("Cooldown: ")
                            .withStyle(ChatFormatting.DARK_GRAY)
                            .append(Component.literal("6s")
                                    .withStyle(ChatFormatting.GREEN)));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }
            });

    public static final DeferredItem<Item> MANA_STICK = ITEMS.register("mana_stick",
            () -> new ManaStick() {
            @Override
            public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                tooltipComponents.add(Component.literal("Right Click: ")
                        .withStyle(ChatFormatting.YELLOW, ChatFormatting.BOLD)
                        .append(Component.literal("Mana Boost")
                                .withStyle(style -> style.withBold(false).withColor(ChatFormatting.GOLD))));
                tooltipComponents.add(Component.literal("Grants ")
                        .withStyle(ChatFormatting.GRAY)
                        .append(Component.literal("400 Mana ")
                                .withStyle(ChatFormatting.DARK_AQUA)));
                tooltipComponents.add(Component.literal("Cooldown: ")
                        .withStyle(ChatFormatting.DARK_GRAY)
                        .append(Component.literal("1s")
                                .withStyle(ChatFormatting.GREEN)));
                super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
            }
        });

    public static final DeferredItem<Item> DASH_SWORD = ITEMS.register("dash_sword",
            () -> new DashSword() {
                @Override
                public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.literal("Right Click: ")
                            .withStyle(ChatFormatting.YELLOW, ChatFormatting.BOLD)
                            .append(Component.literal("Dash")
                                    .withStyle(style -> style.withBold(false).withColor(ChatFormatting.GOLD))));
                    tooltipComponents.add(Component.literal("Dash ")
                            .withStyle(ChatFormatting.WHITE)
                            .append(Component.literal("in the direction you're facing.")
                                    .withStyle(ChatFormatting.GRAY)));
                    tooltipComponents.add(Component.literal("Mana Cost: ")
                            .withStyle(ChatFormatting.DARK_GRAY)
                            .append(Component.literal("40")
                                    .withStyle(ChatFormatting.DARK_AQUA)));
                    tooltipComponents.add(Component.literal("Cooldown: ")
                            .withStyle(ChatFormatting.DARK_GRAY)
                            .append(Component.literal("1s")
                                    .withStyle(ChatFormatting.GREEN)));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }
            });

    public static final DeferredItem<Item> FLASH_STEP_SWORD = ITEMS.register("flash_step_sword",
            () -> new FlashStepSword() {
                @Override
                public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.literal("Right Click: ")
                            .withStyle(ChatFormatting.YELLOW, ChatFormatting.BOLD)
                            .append(Component.literal("Flash Step")
                                    .withStyle(style -> style.withBold(false).withColor(ChatFormatting.GOLD))));
                    tooltipComponents.add(Component.literal("Teleport ")
                            .withStyle(ChatFormatting.WHITE)
                            .append(Component.literal("behind the nearest player in a 4 block radius, ")
                                    .withStyle(ChatFormatting.GRAY)));
                    tooltipComponents.add(Component.literal("or ")
                            .withStyle(ChatFormatting.WHITE)
                            .append(Component.literal("teleport ")
                                    .withStyle(ChatFormatting.WHITE)
                                    .append(Component.literal("behind the nearest entity in a 10 block radius.")
                                            .withStyle(ChatFormatting.GRAY))));
                    tooltipComponents.add(Component.literal("Mana Cost: ")
                            .withStyle(ChatFormatting.DARK_GRAY)
                            .append(Component.literal("100")
                                    .withStyle(ChatFormatting.DARK_AQUA)));
                    tooltipComponents.add(Component.literal("Cooldown: ")
                            .withStyle(ChatFormatting.DARK_GRAY)
                            .append(Component.literal("2s")
                                    .withStyle(ChatFormatting.GREEN)));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }
            });

    public static final DeferredItem<Item> ATTACK_SPEED_SWORD = ITEMS.register("attack_speed_sword",
            () -> new AttackSpeedSword() {
                @Override
                public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.literal("Right Click: ")
                            .withStyle(ChatFormatting.YELLOW, ChatFormatting.BOLD)
                            .append(Component.literal("Haste")
                                    .withStyle(style -> style.withBold(false).withColor(ChatFormatting.GOLD))));
                    tooltipComponents.add(Component.literal("Grants ")
                            .withStyle(ChatFormatting.GRAY)
                            .append(Component.literal("Haste 3 ")
                                    .withStyle(ChatFormatting.WHITE)
                                    .append(Component.literal("for ")
                                            .withStyle(ChatFormatting.GRAY)
                                            .append(Component.literal("10s")
                                                    .withStyle(ChatFormatting.GREEN)
                                                    .append(Component.literal(".")
                                                            .withStyle(ChatFormatting.GRAY))))));
                    tooltipComponents.add(Component.literal("Mana Cost: ")
                            .withStyle(ChatFormatting.DARK_GRAY)
                            .append(Component.literal("80")
                                    .withStyle(ChatFormatting.DARK_AQUA)));
                    tooltipComponents.add(Component.literal("Cooldown: ")
                            .withStyle(ChatFormatting.DARK_GRAY)
                            .append(Component.literal("5s")
                                    .withStyle(ChatFormatting.GREEN)));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }
            });

    public static final DeferredItem<Item> CRYSTAL_LAUNCHER = ITEMS.register("crystal_launcher",
            () -> new CrystalLauncher() {
                @Override
                public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.literal("Right Click: ")
                            .withStyle(ChatFormatting.YELLOW, ChatFormatting.BOLD)
                            .append(Component.literal("Crystal Launch")
                                    .withStyle(style -> style.withBold(false).withColor(ChatFormatting.GOLD))));
                    tooltipComponents.add(Component.literal("Throws an ")
                            .withStyle(ChatFormatting.GRAY)
                            .append(Component.literal("End Crystal")
                                    .withStyle(ChatFormatting.WHITE)
                                    .append(Component.literal(", that explodes")
                                            .withStyle(ChatFormatting.GRAY)
                                            .append(Component.literal(" on contact with an entity or block, or after ")
                                                    .withStyle(ChatFormatting.GRAY)
                                            .append(Component.literal("10s")
                                                    .withStyle(ChatFormatting.GREEN)
                                                    .append(Component.literal(".")
                                                            .withStyle(ChatFormatting.GRAY)))))));
                    tooltipComponents.add(Component.literal("Mana Cost: ")
                            .withStyle(ChatFormatting.DARK_GRAY)
                            .append(Component.literal("120")
                                    .withStyle(ChatFormatting.DARK_AQUA)));
                    tooltipComponents.add(Component.literal("Cooldown: ")
                            .withStyle(ChatFormatting.DARK_GRAY)
                            .append(Component.literal("3s")
                                    .withStyle(ChatFormatting.GREEN)));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }
            });

    public static final DeferredItem<Item> FIREBALL_STAFF = ITEMS.register("fireball_staff",
            () -> new FireballStaff() {
                @Override
                public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.literal("Right Click: ")
                            .withStyle(ChatFormatting.YELLOW, ChatFormatting.BOLD)
                            .append(Component.literal("Fireball")
                                    .withStyle(style -> style.withBold(false).withColor(ChatFormatting.GOLD))));
                    tooltipComponents.add(Component.literal("Summon a ")
                            .withStyle(ChatFormatting.GRAY)
                            .append(Component.literal("Fireball")
                                    .withStyle(ChatFormatting.WHITE)
                                    .append(Component.literal(" and freeze the player, right click again to shoot the fireball.")
                                            .withStyle(ChatFormatting.GRAY))));
                    tooltipComponents.add(Component.literal("If you take damage whilst aiming the fireball explodes, damaging you.")
                            .withStyle(ChatFormatting.DARK_GRAY));
                    tooltipComponents.add(Component.literal("Mana Cost: ")
                            .withStyle(ChatFormatting.DARK_GRAY)
                            .append(Component.literal("60")
                                    .withStyle(ChatFormatting.DARK_AQUA)));
                    tooltipComponents.add(Component.literal("Cooldown: ")
                            .withStyle(ChatFormatting.DARK_GRAY)
                            .append(Component.literal("idk ¯\\_(ツ)_/¯")
                                    .withStyle(ChatFormatting.GREEN)));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }
            });

    public static final DeferredItem<Item> BASIC_MANA_STICK = ITEMS.register("basic_mana_stick",
            () -> new BasicManaStick() {
                @Override
                public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.literal("Right Click: ")
                            .withStyle(ChatFormatting.YELLOW, ChatFormatting.BOLD)
                            .append(Component.literal("Mana Infusion")
                                    .withStyle(style -> style.withBold(false).withColor(ChatFormatting.GOLD))));
                    tooltipComponents.add(Component.literal("Take ")
                            .withStyle(ChatFormatting.GRAY)
                            .append(Component.literal("1 heart ")
                                    .withStyle(ChatFormatting.DARK_RED)
                                    .append(Component.literal("of damage and gain ")
                                            .withStyle(ChatFormatting.GRAY)
                                            .append(Component.literal("30 Mana")
                                                    .withStyle(ChatFormatting.DARK_AQUA)
                                                    .append(Component.literal(".")
                                                            .withStyle(ChatFormatting.GRAY))))));
                    tooltipComponents.add(Component.literal("Health Cost: ")
                            .withStyle(ChatFormatting.DARK_GRAY)
                            .append(Component.literal("2")
                                    .withStyle(ChatFormatting.DARK_RED)));
                    tooltipComponents.add(Component.literal("Cooldown: ")
                            .withStyle(ChatFormatting.DARK_GRAY)
                            .append(Component.literal("10s")
                                    .withStyle(ChatFormatting.GREEN)));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);

                }
            });



    // armor
    public static final DeferredItem<Item> SLIME_BOOTS = ITEMS.register("slime_boots",
            () -> new ArmorItem(Holder.direct(ModArmorMaterials.SLIME), ArmorItem.Type.BOOTS, new Item.Properties().stacksTo(1)) {
                @Override
                public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.literal("Slime block physics")
                                    .withStyle(style -> style.withColor(ChatFormatting.GOLD)));
                    tooltipComponents.add(Component.literal("Take no fall damage, instead you bounce. ")
                            .withStyle(ChatFormatting.GRAY));
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
