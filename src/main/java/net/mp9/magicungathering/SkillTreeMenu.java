package net.mp9.magicungathering;

import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemLore;
import net.mp9.magicungathering.attachment.SkillData;
import net.mp9.magicungathering.registration.ModMenuTypes;
import net.neoforged.neoforge.network.PacketDistributor;
import net.mp9.magicungathering.network.UnlockSkillPacket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.mp9.magicungathering.menu.ClassSelectionMenu;

public class SkillTreeMenu extends AbstractContainerMenu {
    private final SimpleContainer container = new SimpleContainer(54);
    private int currentPage = 0;
    private final Player player;
    private boolean confirmReset = false;
    private final Map<Integer, String> activeSkillSlots = new HashMap<>();

    public SkillTreeMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, null);
    }

    public SkillTreeMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf extraData) {
        super(ModMenuTypes.SKILL_TREE_MENU.get(), containerId);
        this.player = playerInventory.player;

        // Container Slots (0-53)
        for (int row = 0; row < 6; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new Slot(container, col + row * 9, 8 + col * 18, 18 + row * 18));
            }
        }
        // Player Inventory
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 140 + row * 18));
            }
        }
        // Hotbar
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 198));
        }

        loadPage(0);
    }

    private Item getVisualItem(String skillId, Item defaultItem, SkillData data) {
        if (data == null || !data.hasSkill(skillId)) {
            return defaultItem;
        }
        if (defaultItem == Items.SLIME_BALL) return Items.MAGMA_CREAM;
        if (defaultItem == Items.ENDER_PEARL) return Items.ENDER_EYE;
        if (defaultItem == Items.CHORUS_FRUIT) return Items.POPPED_CHORUS_FRUIT;
        return defaultItem;
    }

    public void loadPage(int page) {
        this.currentPage = page;
        this.container.clearContent();
        this.activeSkillSlots.clear();

        SkillData data = this.player.getData(MagicUngathering.SKILL_DATA);
        String playerClass = (data != null && data.getCurrentClass() != null) ? data.getCurrentClass() : "Assassin";

        switch (playerClass) {
            case "Assassin" -> loadAssassinTree(page, data);
            case "Warrior" -> loadWarriorTree(page, data);
            default -> loadAssassinTree(page, data);
        }

        if (data != null) setupBottomBar(data);
    }

    private void placeSkill(int slot, String skillId, Item icon, String name, String lore, SkillData data) {
        boolean isUnlocked = (data != null) && data.hasSkill(skillId);
        Item displayIcon = getVisualItem(skillId, icon, data);
        this.container.setItem(slot, createSkillItem(displayIcon, name, lore, isUnlocked, 1));
        this.activeSkillSlots.put(slot, skillId);
    }

    private void placeSkillUpgrade(int slot, String skillId, Item icon, String name, String lore, SkillData data) {
        boolean isUnlocked = (data != null) && data.hasSkill(skillId);
        Item displayIcon = getVisualItem(skillId, icon, data);
        this.container.setItem(slot, createSkillItem(displayIcon, name, lore, isUnlocked, 2));
        this.activeSkillSlots.put(slot, skillId);
    }

    private void loadAssassinTree(int page, SkillData data) {
        if (page == 0) {
            placeSkill(4, "assassin_mark", Items.ENDER_PEARL, "Mark Enemy", "Track your target", data);
            placeSkillUpgrade(18, "upgrade_execute_time", Items.CHORUS_FRUIT, "Execute +1.5s", "increases the length of the execute ability by +1.5s", data);
            placeSkillUpgrade(20, "upgrade_mark_reduced_cost", Items.CHORUS_FRUIT, "Mark enemy reduced cost", "Mana cost of mark enemy is reduced by X amount", data);
            placeSkill(22, "assassin_1speed5", Items.SLIME_BALL, "+5% Speed", "Increases your base speed by +5%.", data);
            placeSkill(36, "assassin_2speed5", Items.SLIME_BALL, "+5% Speed", "Increases your base speed by +5%.", data);
            placeSkillUpgrade(40, "upgrade_mark_increased_time", Items.CHORUS_FRUIT, "mark lasts +3s", "Increases the length enemies are marked by 3 seconds", data);

            placeConnector(13, new String[]{"assassin_mark"}, data);
            placeConnector(21, new String[]{"assassin_1speed5"}, data);
            placeConnector(27, new String[]{"assassin_2speed5"}, data);
            placeConnector(31, new String[]{"assassin_1speed5"}, data);
        }

        if (page == 1) {
            placeSkillUpgrade(9, "upgrade_blink_weakness", Items.CHORUS_FRUIT, "Weakening Blink", "Blink also inflicts weakness 1.", data);
            placeSkill(11, "assassin_execute", Items.ENDER_PEARL, "Execute", "Strength 2 for 2s.", data);
            placeSkill(13, "assassin_blink", Items.ENDER_PEARL, "Blink", "Teleport forward.", data);
            placeSkill(15, "assassin_1damage5", Items.SLIME_BALL, "+5% Melee Damage", "Melee damage increase.", data);
            placeSkill(27, "shadow_step", Items.ENDER_PEARL, "Shadow Step", "Teleport behind your marked enemy.", data);
            placeSkillUpgrade(29, "upgrade_execute_level", Items.CHORUS_FRUIT, "Execute Strength 3", "Upgrades strength level.", data);
            placeSkill(31, "smoke_screen", Items.ENDER_PEARL, "Smoke Screen", "Blinds enemies within.", data);
            placeSkill(33, "assassin_1speed5armor1", Items.SLIME_BALL, "+5% Speed, -1 Armor", "Trade armor for speed.", data);
            placeSkillUpgrade(35, "upgrade_blink_time", Items.CHORUS_FRUIT, "Blink lasts +2s", "Duration increase.", data);

            placeConnector(0, new String[]{"upgrade_blink_weakness"}, data);
            placeConnector(4, new String[]{"upgrade_mark_increased_time"}, data);
            placeConnector(10, new String[]{"assassin_execute"}, data);
            placeConnector(12, new String[]{"assassin_blink"}, data);
            placeConnector(14, new String[]{"assassin_blink"}, data);
            placeConnector(20, new String[]{"assassin_execute", "upgrade_execute_level"}, data);
            placeConnector(24, new String[]{"assassin_1damage5", "assassin_1speed5armor1"}, data);
            placeConnector(28, new String[]{"upgrade_execute_level"}, data);
            placeConnector(30, new String[]{"upgrade_execute_level", "smoke_screen"}, data);
            placeConnector(32, new String[]{"assassin_1speed5armor1", "smoke_screen"}, data);
            placeConnector(34, new String[]{"assassin_1speed5armor1", "upgrade_blink_time"}, data);
            placeConnector(38, new String[]{"upgrade_execute_level", "assassin_manaregen2"}, data);
            placeConnector(40, new String[]{"smoke_screen", "upgrade_smoke_screen_length"}, data);
            placeConnector(44, new String[]{"upgrade_blink_time", "assassin_1maxmana5"}, data);
        }

        if (page == 2) {
            placeSkill(2, "assassin_manaregen2", Items.SLIME_BALL, "+0.2% Mana Regen", "Increases mana regen.", data);
            placeSkillUpgrade(4, "upgrade_smoke_screen_length", Items.CHORUS_FRUIT, "+3s to Smoke Screen", "Increases smoke duration.", data);
            placeSkill(6, "assassin_vanish", Items.ENDER_PEARL, "Vanish", "Invisibility, Speed 2, Weakness 1.", data);
            placeSkill(8, "assassin_1maxmana5", Items.SLIME_BALL, "+5 Max Mana", "Gain +5 max mana.", data);
            placeSkillUpgrade(18, "upgrade_blink_reduced_cost", Items.CHORUS_FRUIT, "Cheaper Blink", "Blink cost reduction.", data);
            placeSkill(20, "assassin_speed5", Items.SLIME_BALL, "Speedy", "Gain +5% speed.", data);
            placeSkill(22, "assassin_1armor1", Items.SLIME_BALL, "Toughen Up", "Gain +1 armor.", data);
            placeSkill(24, "assassin_2armor1", Items.SLIME_BALL, "Toughed it Out", "Gain +1 armor.", data);
            placeSkill(26, "assassin_haemorrhage", Items.ENDER_PEARL, "Haemorrhage", "Melee attacks inflict bleeding.", data);
            placeSkill(36, "assassin_heart1speed5", Items.SLIME_BALL, "Speed for Life", "Speed +5%, Hearts -1.", data);
            placeSkillUpgrade(40, "upgrade_smoke_screen_poison", Items.CHORUS_FRUIT, "Poisonous Smoke", "Inflicts poison.", data);
            placeSkillUpgrade(42, "upgrade_vanish_no_weakness", Items.CHORUS_FRUIT, "Strong Vanish", "No weakness during vanish.", data);
            placeSkill(44, "assassin_2maxmana5", Items.SLIME_BALL, "+5 Max Mana", "Gain +5 max mana.", data);

            placeConnector(7, new String[]{"assassin_vanish", "assassin_maxmana5"}, data);
            placeConnector(11, new String[]{"assassin_manaregen2", "assassin_speed5"}, data);
            placeConnector(13, new String[]{"upgrade_smoke_screen_length", "assassin_1armor1"}, data);
            placeConnector(15, new String[]{"assassin_vanish", "assassin_2armor1"}, data);
            placeConnector(17, new String[]{"assassin_1maxmana5", "assassin_haemorrhage"}, data);
            placeConnector(19, new String[]{"upgrade_blink_reduced_cost", "assassin_speed5"}, data);
            placeConnector(23, new String[]{"assassin_1armor1", "assassin_2armor1"}, data);
            placeConnector(27, new String[]{"upgrade_blink_reduced_cost", "assassin_heart1speed5"}, data);
            placeConnector(31, new String[]{"assassin_1armor1", "upgrade_smoke_screen_poison"}, data);
            placeConnector(33, new String[]{"assassin_2armor1", "upgrade_vanish_no_weakness"}, data);
            placeConnector(35, new String[]{"assassin_haemorrhage", "assassin_2maxmana5"}, data);
            placeConnector(43, new String[]{"upgrade_vanish_no_weakness", "assassin_2maxmana5"}, data);
        }

        if (page == 3) {
            placeSkill(9, "assassin_maxmana10", Items.SLIME_BALL, "General Knowledge", "Max mana +10.", data);
            placeSkill(11, "assassin_manaregen5", Items.SLIME_BALL, "Knowledge", "Mana regen +5%.", data);
            placeSkill(13, "assassin_maxmana15heart1", Items.SLIME_BALL, "Knowledge For Life", "Hearts +1, Mana -15.", data);
            placeSkill(15, "assassin_2speed5armor1", Items.SLIME_BALL, "Even Further Beyond", "Speed +5%, Armor -1.", data);
            placeSkillUpgrade(17, "upgrade_vanish_lifesteal", Items.CHORUS_FRUIT, "Hidden Lifesteal", "Lifesteal 20% of damage dealt whilst vanished.", data);
            placeSkillUpgrade(27, "upgrade_execute_bones", Items.CHORUS_FRUIT, "Brittle Bones", "Target takes 1.2x damage.", data);
            placeSkillUpgrade(29, "upgrade_execute_time2", Items.CHORUS_FRUIT, "Ultimate Executioner", "Duration +2.5s.", data);
            placeSkill(31, "assassin_damage15heart1", Items.SLIME_BALL, "My Life for Damage", "Damage +15%, Hearts -1.", data);
            placeSkillUpgrade(33, "upgrade_haemorrhage_time", Items.CHORUS_FRUIT, "Haemorrhage Time", "Infliction time +1.5s.", data);
            placeSkillUpgrade(35, "upgrade_haemorrhage_damage", Items.CHORUS_FRUIT, "Super Haemorrhage", "Doubles the bleed damage, however the window to inflict is reduced by half and the time of bleeding is reduced by 1/3.", data);

            placeConnector(0, new String[]{"assassin_heart1speed5", "assassin_maxmana10"}, data);
            placeConnector(6, new String[]{"upgrade_vanish_no_weakness", "assassin_2speed5armor1"}, data);
            placeConnector(8, new String[]{"assassin_2maxmana5", "upgrade_vanish_lifesteal"}, data);
            placeConnector(10, new String[]{"assassin_maxmana10", "assassin_manaregen5"}, data);
            placeConnector(12, new String[]{"assassin_manaregen5", "assassin_maxmana15heart1"}, data);
            placeConnector(14, new String[]{"assassin_maxmana15heart1", "assassin_2speed5armor1"}, data);
            placeConnector(16, new String[]{"assassin_2speed5armor1", "upgrade_vanish_lifesteal"}, data);
            placeConnector(20, new String[]{"assassin_manaregen5"}, data);
            placeConnector(22, new String[]{"assassin_damage15heart1"}, data);
            placeConnector(28, new String[]{"upgrade_execute_time2"}, data);
            placeConnector(32, new String[]{"assassin_damage15heart1"}, data);
            placeConnector(34, new String[]{"upgrade_haemorrhage_time"}, data);
        }
    }

    private void loadWarriorTree(int page, SkillData data) {
        if (page == 0) placeSkill(4, "warrior_mark", Items.ENDER_PEARL, "Warrior's Focus", "Target damage focus.", data);
    }

    private ItemStack createSkillItem(Item item, String name, String lore, boolean unlocked, int cost) {
        ItemStack stack = new ItemStack(item);
        stack.set(DataComponents.CUSTOM_NAME, Component.literal(name).withStyle(unlocked ? ChatFormatting.GREEN : ChatFormatting.GOLD));
        List<Component> loreList = new ArrayList<>();
        loreList.add(Component.literal(lore).withStyle(ChatFormatting.GRAY));
        loreList.add(Component.literal(unlocked ? "UNLOCKED" : "COST: " + cost + " AP").withStyle(unlocked ? ChatFormatting.DARK_GREEN : ChatFormatting.RED));
        stack.set(DataComponents.LORE, new ItemLore(loreList));
        return stack;
    }

    private ItemStack createSimpleItem(Item item, String name, ChatFormatting color) {
        ItemStack stack = new ItemStack(item);
        stack.set(DataComponents.CUSTOM_NAME, Component.literal(name).withStyle(color).withStyle(ChatFormatting.BOLD));
        return stack;
    }

    private void setupBottomBar(SkillData data) {
        if (currentPage > 0) this.container.setItem(45, createSimpleItem(Items.PAPER, "↑ Previous Page", ChatFormatting.AQUA));
        if (currentPage < 4) this.container.setItem(46, createSimpleItem(Items.PAPER, "↓ Next Page", ChatFormatting.AQUA));

        ItemStack apBottle = createSimpleItem(Items.EXPERIENCE_BOTTLE, "AP Available: " + data.points(), ChatFormatting.GREEN);
        apBottle.setCount(Math.max(1, Math.min(64, data.points())));
        this.container.setItem(48, apBottle);

        ItemStack apBook = createSimpleItem(Items.BOOK, data.totalPoints() + " / 32 available points" , ChatFormatting.AQUA);
        apBook.setCount(Math.max(1, Math.min(64, data.totalPoints())));
        this.container.setItem(50, apBook);

        this.container.setItem(52, createSimpleItem(Items.COMPASS, "Change Class", ChatFormatting.LIGHT_PURPLE));

        Item resetIcon = confirmReset ? Items.REDSTONE_BLOCK : Items.BARRIER;
        this.container.setItem(53, createSimpleItem(resetIcon, confirmReset ? "CONFIRM RESET?" : "Reset Skill Tree", confirmReset ? ChatFormatting.RED : ChatFormatting.GRAY));
    }

    @Override
    public void clicked(int slotId, int button, ClickType clickType, Player player) {
        if (clickType == ClickType.SWAP || clickType == ClickType.QUICK_MOVE) return;

        if (activeSkillSlots.containsKey(slotId)) {
            if (player.level().isClientSide) {
                PacketDistributor.sendToServer(new UnlockSkillPacket(activeSkillSlots.get(slotId)));
            }
            return;
        }

        if (slotId == 45 && currentPage > 0) loadPage(currentPage - 1);
        else if (slotId == 46 && currentPage < 4) loadPage(currentPage + 1);

        if (!player.level().isClientSide && player instanceof ServerPlayer serverPlayer) {
            if (slotId == 52) {
                serverPlayer.openMenu(new net.minecraft.world.SimpleMenuProvider((id, inv, p) ->
                        new ClassSelectionMenu(id, inv, null), Component.literal("Classes")));
            } else if (slotId == 53) {
                if (!confirmReset) {
                    confirmReset = true;
                    // Play a small click sound to indicate state change
                    serverPlayer.level().playSound(null, serverPlayer.getX(), serverPlayer.getY(), serverPlayer.getZ(),
                            net.minecraft.sounds.SoundEvents.UI_BUTTON_CLICK.value(),
                            net.minecraft.sounds.SoundSource.PLAYERS, 1.0f, 1.0f);
                    loadPage(currentPage);
                } else {
                    confirmReset = false;

                    // --- ACTUAL RESET LOGIC START ---
                    SkillData data = serverPlayer.getData(MagicUngathering.SKILL_DATA);

                    // Create a new SkillData instance:
                    // 1. New empty list for unlockedSkills
                    // 2. Points reset to the totalPoints value
                    // 3. Keep currentClass, totalPoints, and cooldowns the same
                    SkillData resetData = new SkillData(
                            new java.util.ArrayList<>(),
                            data.totalPoints(),
                            data.currentClass(),
                            data.totalPoints(),
                            data.cooldowns()
                    );

                    serverPlayer.setData(MagicUngathering.SKILL_DATA, resetData);
                    // --- ACTUAL RESET LOGIC END ---

                    // Play a Success sound for the reset
                    serverPlayer.level().playSound(null, serverPlayer.getX(), serverPlayer.getY(), serverPlayer.getZ(),
                            net.minecraft.sounds.SoundEvents.PLAYER_LEVELUP,
                            net.minecraft.sounds.SoundSource.PLAYERS, 1.0f, 0.5f);

                    serverPlayer.displayClientMessage(Component.literal("Skill Tree Reset!").withStyle(ChatFormatting.GOLD), true);

                    loadPage(currentPage);
                }
            } else {
                // If they click any other slot, cancel the reset confirmation
                if (confirmReset) {
                    confirmReset = false;
                    loadPage(currentPage);
                }
            }
            this.broadcastChanges();
        }

        if (slotId >= 54 || slotId == -999) super.clicked(slotId, button, clickType, player);
    }

    public void refresh() {
        loadPage(this.currentPage);
        this.broadcastChanges();
    }

    public int getCurrentPage() {
        return this.currentPage;
    }

    private void placeConnector(int slot, String[] skillIdsToCheck, SkillData data) {
        boolean active = false;
        if (data != null) {
            for (String id : skillIdsToCheck) {
                if (data.hasSkill(id)) {
                    active = true;
                    break;
                }
            }
        }
        Item icon = active ? Items.LIME_STAINED_GLASS_PANE : Items.RED_STAINED_GLASS_PANE;
        this.container.setItem(slot, createSimpleItem(icon, active ? "§aLink Active" : "§cLink Inactive", active ? ChatFormatting.GREEN : ChatFormatting.RED));
    }

    @Override public ItemStack quickMoveStack(Player p, int i) { return ItemStack.EMPTY; }
    @Override public boolean stillValid(Player p) { return true; }
}