package net.mp9.magicungathering.registration;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.mp9.magicungathering.MagicUngathering;
import net.mp9.magicungathering.SkillTreeMenu;
import net.mp9.magicungathering.menu.ClassSelectionMenu;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(Registries.MENU, MagicUngathering.MOD_ID);

    public static final DeferredHolder<MenuType<?>, MenuType<SkillTreeMenu>> SKILL_TREE_MENU =
            MENUS.register("skill_tree_menu", () -> IMenuTypeExtension.create(SkillTreeMenu::new));

    public static final DeferredHolder<MenuType<?>, MenuType<ClassSelectionMenu>> CLASS_SELECTION_MENU =
            MENUS.register("class_selection_menu", () -> IMenuTypeExtension.create(ClassSelectionMenu::new));

    public static void register(IEventBus eventBus) {
        MENUS.register(eventBus);
    }
}