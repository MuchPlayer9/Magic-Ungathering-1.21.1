package net.mp9.magicungathering.event;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Items;
import net.minecraft.core.component.DataComponents;
import net.mp9.magicungathering.MagicUngathering;
import net.mp9.magicungathering.attachment.SkillData;
import net.mp9.magicungathering.SkillTreeMenu;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.AdvancementEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.HashMap;
import java.util.Map;

@EventBusSubscriber(modid = MagicUngathering.MOD_ID)
public class AdvancementEventHandler {

    public static final Map<ResourceLocation, Integer> POINT_REWARDS = new HashMap<>();

    static {
        /*
        POINT_REWARDS.put(ResourceLocation.withDefaultNamespace("adventure/kill_a_mob"), 1);
        POINT_REWARDS.put(ResourceLocation.withDefaultNamespace("nether/return_to_sender"), 1);
        POINT_REWARDS.put(ResourceLocation.withDefaultNamespace("nether/fast_travel"), 1);
        POINT_REWARDS.put(ResourceLocation.withDefaultNamespace("adventure/kill_mob_near_sculk_catalyst"), 1);
        // POINT_REWARDS.put(ResourceLocation.withDefaultNamespace("adventure/heart_transplanter"), 1);
        POINT_REWARDS.put(ResourceLocation.withDefaultNamespace("adventure/sniper_duel"), 1);
        POINT_REWARDS.put(ResourceLocation.withDefaultNamespace("nether/explore_nether"), 1);
        POINT_REWARDS.put(ResourceLocation.withDefaultNamespace("adventure/fall_from_world_height"), 1);
        POINT_REWARDS.put(ResourceLocation.withDefaultNamespace("adventure/spyglass_at_dragon"), 1);
        POINT_REWARDS.put(ResourceLocation.withDefaultNamespace("end/kill_dragon"), 1);
        POINT_REWARDS.put(ResourceLocation.withDefaultNamespace("nether/summon_wither"), 1);
        POINT_REWARDS.put(ResourceLocation.withDefaultNamespace("nether/netherite_armor"), 1);
        POINT_REWARDS.put(ResourceLocation.withDefaultNamespace("husbandry/complete_catalogue"), 1);
        POINT_REWARDS.put(ResourceLocation.withDefaultNamespace("husbandry/whole_pack"), 1);
        POINT_REWARDS.put(ResourceLocation.withDefaultNamespace("husbandry/ride_a_boat_with_a_goat"), 1);
        POINT_REWARDS.put(ResourceLocation.withDefaultNamespace("husbandry/leash_all_frog_variants"), 1);
        POINT_REWARDS.put(ResourceLocation.withDefaultNamespace("adventure/hero_of_the_village"), 1);
        POINT_REWARDS.put(ResourceLocation.withDefaultNamespace("husbandry/allay_deliver_cake_to_note_block"), 1);
        POINT_REWARDS.put(ResourceLocation.withDefaultNamespace("adventure/overoverkill"), 1);
        POINT_REWARDS.put(ResourceLocation.withDefaultNamespace("husbandry/feed_snifflet"), 1);
        POINT_REWARDS.put(ResourceLocation.withDefaultNamespace("adventure/arbalistic"), 1);
        POINT_REWARDS.put(ResourceLocation.withDefaultNamespace("adventure/blowback"), 1);
        POINT_REWARDS.put(ResourceLocation.withDefaultNamespace("nether/uneasy_alliance"), 1);
        POINT_REWARDS.put(ResourceLocation.withDefaultNamespace("husbandry/balanced_diet"), 1);
        POINT_REWARDS.put(ResourceLocation.withDefaultNamespace("adventure/two_birds_one_arrow"), 1);
        POINT_REWARDS.put(ResourceLocation.withDefaultNamespace("adventure/very_very_frightening"), 1);
        POINT_REWARDS.put(ResourceLocation.withDefaultNamespace("nether/ride_strider_in_overworld_lava"), 1);
        POINT_REWARDS.put(ResourceLocation.withDefaultNamespace("adventure/adventuring_time"), 1);
        POINT_REWARDS.put(ResourceLocation.withDefaultNamespace("adventure/trim_with_all_exclusive_armor_patterns"), 1);
        POINT_REWARDS.put(ResourceLocation.withDefaultNamespace("adventure/kill_all_mobs"), 1);
        POINT_REWARDS.put(ResourceLocation.withDefaultNamespace("nether/all_effects"), 2);
        */
        // dev test advancement REMEMBER TO REMOVE IN UPDATE
        POINT_REWARDS.put(ResourceLocation.withDefaultNamespace("adventure/avoid_vibration"), 9999);
        POINT_REWARDS.put(ResourceLocation.withDefaultNamespace("nether/obtain_crying_obsidian"), 32);
    }

    @SubscribeEvent
    public static void onAdvancementEarned(AdvancementEvent.AdvancementEarnEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            ResourceLocation id = event.getAdvancement().id();

            if (POINT_REWARDS.containsKey(id)) {
                int reward = POINT_REWARDS.get(id);
                SkillData data = player.getData(MagicUngathering.SKILL_DATA);

                int newSpendable = data.points() + reward;
                int newTotal = data.totalPoints() + reward;

                player.setData(MagicUngathering.SKILL_DATA, new SkillData(
                        data.unlockedSkills(),
                        newSpendable,
                        data.currentClass(),
                        newTotal,
                        data.cooldowns()
                ));

                updatePlayerItems(player, newSpendable, newTotal);
                refreshMenuIfOpen(player);

                player.displayClientMessage(Component.literal("§6+ " + reward + " Skill Points Gained!"), true);
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        // Only run on server, every 20 ticks
        if (event.getEntity() instanceof ServerPlayer player && player.tickCount % 20 == 0) {
            int earnedFromAdvancements = 0;

            for (Map.Entry<ResourceLocation, Integer> entry : POINT_REWARDS.entrySet()) {
                var holder = player.server.getAdvancements().get(entry.getKey());
                if (holder != null && player.getAdvancements().getOrStartProgress(holder).isDone()) {
                    earnedFromAdvancements += entry.getValue();
                }
            }

            SkillData data = player.getData(MagicUngathering.SKILL_DATA);


            if (data.totalPoints() != earnedFromAdvancements) {
                int difference = earnedFromAdvancements - data.totalPoints();


                int newSpendablePoints = Math.max(0, data.points() + difference);

                SkillData newData = new SkillData(
                        data.unlockedSkills(),
                        newSpendablePoints,
                        data.currentClass(),
                        earnedFromAdvancements,
                        data.cooldowns()
                );

                player.setData(MagicUngathering.SKILL_DATA, newData);
                updatePlayerItems(player, newSpendablePoints, earnedFromAdvancements);
                refreshMenuIfOpen(player);
            }
        }
    }

    public static void updatePlayerItems(ServerPlayer player, int currentPoints, int totalPoints) {

        player.getInventory().items.forEach(stack -> {
            if (stack.is(Items.EXPERIENCE_BOTTLE)) {
                stack.set(DataComponents.MAX_STACK_SIZE, 64);
                stack.setCount(Math.max(1, Math.min(64, currentPoints)));

                // Add glint if points > 0
                if (currentPoints > 0) {
                    stack.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true);
                } else {
                    stack.remove(DataComponents.ENCHANTMENT_GLINT_OVERRIDE);
                }
            }

            if (stack.is(Items.BOOK)) {
                stack.set(DataComponents.MAX_STACK_SIZE, 64);
                stack.setCount(Math.max(1, Math.min(64, totalPoints)));
            }
        });
        player.inventoryMenu.broadcastChanges();
    }

    private static void refreshMenuIfOpen(ServerPlayer player) {
        if (player.containerMenu instanceof SkillTreeMenu skillMenu) {
            skillMenu.loadPage(skillMenu.getCurrentPage());
            skillMenu.broadcastChanges();
            player.inventoryMenu.broadcastChanges();
        }
    }

    @SubscribeEvent
    public static void onPlayerJoin(net.neoforged.neoforge.event.entity.EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof ServerPlayer player && !event.getLevel().isClientSide) {
            int totalEarned = 0;
            for (Map.Entry<ResourceLocation, Integer> entry : POINT_REWARDS.entrySet()) {
                var adv = player.server.getAdvancements().get(entry.getKey());
                if (adv != null && player.getAdvancements().getOrStartProgress(adv).isDone()) {
                    totalEarned += entry.getValue();
                }
            }

            SkillData data = player.getData(MagicUngathering.SKILL_DATA);

            if (data.totalPoints() != totalEarned) {
                int diff = totalEarned - data.totalPoints();
                int newSpendable = Math.max(0, data.points() + diff);


                SkillData newData = new SkillData(data.unlockedSkills(), newSpendable, data.currentClass(), totalEarned, data.cooldowns());
                player.setData(MagicUngathering.SKILL_DATA, newData);

                updatePlayerItems(player, newSpendable, totalEarned);
            }
        }
    }
}