package net.mp9.magicungathering.menu;

import net.minecraft.ChatFormatting;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.core.component.DataComponents;
import net.mp9.magicungathering.MagicUngathering;
import net.mp9.magicungathering.SkillTreeMenu;
import net.mp9.magicungathering.attachment.SkillData;
import net.mp9.magicungathering.registration.ModMenuTypes;

public class ClassSelectionMenu extends AbstractContainerMenu {
    private final SimpleContainer container = new SimpleContainer(27);

    public ClassSelectionMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf extraData) {
        super(ModMenuTypes.CLASS_SELECTION_MENU.get(), containerId);

        // Add 27 slots for the 9x3 grid
        for (int i = 0; i < 27; i++) {
            this.addSlot(new Slot(container, i, 8 + (i % 9) * 18, 18 + (i / 9) * 18));
        }

        setupClasses();
    }

    private void setupClasses() {
        container.setItem(9, createClassItem(Items.NETHERITE_SWORD, "Assassin", ChatFormatting.RED));
        container.setItem(10, createClassItem(Items.IRON_CHESTPLATE, "Warrior", ChatFormatting.GRAY));
        container.setItem(11, createClassItem(Items.BLAZE_ROD, "Mage", ChatFormatting.AQUA));
        container.setItem(12, createClassItem(Items.GOLDEN_APPLE, "Saint", ChatFormatting.YELLOW));
        container.setItem(13, createClassItem(Items.OAK_SAPLING, "Druid", ChatFormatting.GREEN));
        container.setItem(14, createClassItem(Items.LEATHER_BOOTS, "Rogue", ChatFormatting.DARK_AQUA));
        container.setItem(15, createClassItem(Items.BREWING_STAND, "Alchemist", ChatFormatting.LIGHT_PURPLE));
        container.setItem(16, createClassItem(Items.WITHER_SKELETON_SKULL, "Necromancer", ChatFormatting.DARK_PURPLE));
        container.setItem(17, createClassItem(Items.BOOK, "Warlock", ChatFormatting.DARK_RED));
        container.setItem(21, createClassItem(Items.IRON_SWORD, "Duelist", ChatFormatting.WHITE));
        container.setItem(22, createClassItem(Items.SHIELD, "Juggernaught", ChatFormatting.GOLD));
    }

    private ItemStack createClassItem(Item item, String name, ChatFormatting color) {
        ItemStack stack = new ItemStack(item);
        stack.set(DataComponents.CUSTOM_NAME, Component.literal(name).withStyle(color).withStyle(ChatFormatting.BOLD));
        return stack;
    }

    @Override
    public boolean stillValid(Player player) { return true; }

    @Override
    public ItemStack quickMoveStack(Player player, int index) { return ItemStack.EMPTY; }

    @Override
    public void clicked(int slotId, int button, net.minecraft.world.inventory.ClickType clickType, Player player) {
        if (player instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
            // Only check slots 0-26 (the classes container)
            if (slotId >= 0 && slotId < 27) {
                ItemStack clickedStack = this.container.getItem(slotId);

                if (!clickedStack.isEmpty()) {
                    // get the raw class name (e.g., "Assassin")
                    String className = clickedStack.getHoverName().getString();

                    // get current data
                    SkillData data = serverPlayer.getData(MagicUngathering.SKILL_DATA);

                    // CREATES NEW RECORD (Since records are immutable)
                    // Constructor order: List<String> unlockedSkills, int points, String currentClass
                    SkillData newData = new SkillData(data.unlockedSkills(), data.points(), className, data.totalPoints(), data.cooldowns());

                    // save new record back to the player
                    serverPlayer.setData(MagicUngathering.SKILL_DATA, newData);

                    // Re-open SkillTreeMenu (it will now see the new class)
                    serverPlayer.openMenu(new SimpleMenuProvider(
                            (id, inv, p) -> new SkillTreeMenu(id, inv, null),
                            Component.literal(className).withStyle(ChatFormatting.RED, ChatFormatting.BOLD)
                                    .append(Component.literal(" - Skill Tree").withStyle(ChatFormatting.DARK_GRAY
                    ))));
                }
                return; // Stop processing so they don't pick up the icon
            }
        }

        // Prevent moving items in/out of the player's inventory while in this menu
        if (slotId >= 0) {
            return;
        }

        super.clicked(slotId, button, clickType, player);
    }
}