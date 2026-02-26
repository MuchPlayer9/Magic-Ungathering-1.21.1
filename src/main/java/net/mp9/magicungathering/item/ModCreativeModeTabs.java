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
                        output.accept(ModItems.MANA_STICK);
                        output.accept(ModItems.DASH_SWORD);
                        output.accept(ModItems.FLASH_STEP_SWORD);
                    }).build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TAB.register(eventBus);
    }
}
