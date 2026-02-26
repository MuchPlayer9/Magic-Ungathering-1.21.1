package net.mp9.magicungathering.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.mp9.magicungathering.MagicUngathering;
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

    public static void register(IEventBus eventBus) {ITEMS.register(eventBus);}
}
