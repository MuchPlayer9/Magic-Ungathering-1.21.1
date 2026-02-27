package net.mp9.magicungathering.item;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.mp9.magicungathering.MagicUngathering;

import java.util.function.Supplier;

public class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TAB =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MagicUngathering.MOD_ID);

    public static final Supplier<CreativeModeTab> MAGIC_UNGATHERED_WEAPONS = CREATIVE_MODE_TAB.register("magic_ungathered_weapons",
            () -> CreativeModeTab.builder()
                    .icon(() -> new ItemStack(ModItems.SPEED_SWORD.get()))
                    .title(Component.translatable("creativetab.magicungathering.magic_ungathered_weapons"))
                    .displayItems((itemDisplayParameters, output) -> {
                        output.accept(ModItems.SPEED_SWORD);
                        output.accept(ModItems.DASH_SWORD);
                        output.accept(ModItems.FLASH_STEP_SWORD);
                        output.accept(ModItems.STRENGTH_SWORD);
                        output.accept(ModItems.JUMP_SWORD);
                        output.accept(ModItems.CRYSTAL_SWORD);
                        output.accept(ModItems.ATTACK_SPEED_SWORD);
                        output.accept(ModItems.CRYSTAL_LAUNCHER);
                        output.accept(ModItems.FIREBALL_STAFF);
                        output.accept(ModItems.BASIC_MANA_STICK);
                    }).build());

    public static final Supplier<CreativeModeTab> MAGIC_UNGATHERED_ARMOR = CREATIVE_MODE_TAB.register("magic_ungathered_armor",
            () -> CreativeModeTab.builder()
                    .icon(() -> new ItemStack(ModItems.TIER_ONE_ROBE.get()))
                    .title(Component.translatable("creativetab.magicungathering.magic_ungathered_armor"))
                    .displayItems((itemDisplayParameters, output) -> {
                        output.accept(ModItems.TIER_ONE_ROBE);
                        output.accept(ModItems.TIER_TWO_ROBE);
                        output.accept(ModItems.SLIME_BOOTS);
                    }).build());

    public static final Supplier<CreativeModeTab> MAGIC_UNGATHERED_CREATIVE = CREATIVE_MODE_TAB.register("magic_ungathered_creative",
            () -> CreativeModeTab.builder()
                    .icon(() -> new ItemStack(ModItems.MANA_STICK.get()))
                    .title(Component.translatable("creativetab.magicungathering.magic_ungathered_creative"))
                    .displayItems((itemDisplayParameters, output) -> {
                        output.accept(ModItems.MANA_STICK);
                        output.accept(ModItems.CREATIVE_LEGGINGS);

                    }).build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TAB.register(eventBus);
    }
}
