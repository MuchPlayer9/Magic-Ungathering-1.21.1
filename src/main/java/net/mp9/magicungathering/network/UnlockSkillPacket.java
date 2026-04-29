package net.mp9.magicungathering.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.mp9.magicungathering.MagicUngathering;
import net.mp9.magicungathering.SkillTreeMenu;
import net.mp9.magicungathering.attachment.SkillData;
import net.mp9.magicungathering.event.AdvancementEventHandler;
import net.mp9.magicungathering.logic.AbilityManager;
import net.mp9.magicungathering.logic.SkillRequirements;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.ArrayList;
import java.util.List;

public record UnlockSkillPacket(String skillId) implements CustomPacketPayload {
    public static final Type<UnlockSkillPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(MagicUngathering.MOD_ID, "unlock_skill"));

    public static final StreamCodec<FriendlyByteBuf, UnlockSkillPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, UnlockSkillPacket::skillId,
            UnlockSkillPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }

    public static void handle(final UnlockSkillPacket payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            ServerPlayer player = (ServerPlayer) context.player();
            SkillData data = player.getData(MagicUngathering.SKILL_DATA);
            String id = payload.skillId();

            // --- CATEGORIZATION LOGIC ---

            // Whitelist for Movement skills (Exempt from limit)
            boolean isMovement = id.equals("assassin_blink") || id.equals("warrior_dash");

            // Keyword check for all Passive Upgrades (Exempt from limit)
            // Includes "regen" to catch manaregen2/5 and "upgrade" to catch all AUs.
            boolean isUpgrade = id.contains("upgrade") || id.contains("speed") ||
                    id.contains("armor") || id.contains("damage") ||
                    id.contains("heart") || id.contains("mana") ||
                    id.contains("regen");

            // Base Ability (A) = Anything that isn't Movement or an Upgrade.
            // This covers: mark, shadow_step, execute, vanish, haemorrhage, and smoke_screen.
            boolean isBaseAbility = !isMovement && !isUpgrade;

            // --- LIMIT ENFORCEMENT ---

            if (isBaseAbility) {
                // Count current Base Abilities (A) in the player's data using the same filters
                long activeCount = data.unlockedSkills().stream()
                        .filter(s -> !s.equals("assassin_blink") && !s.equals("warrior_dash"))
                        .filter(s -> !s.contains("upgrade") && !s.contains("speed") &&
                                !s.contains("armor") && !s.contains("damage") &&
                                !s.contains("heart") && !s.contains("mana") &&
                                !s.contains("regen"))
                        .count();

                if (activeCount >= 3) {
                    player.displayClientMessage(Component.literal("§cSlot limit reached! You can only have 3 abilities + movement."), true);
                    return;
                }
            }

            // --- PURCHASE LOGIC ---

            // Check if the skill already exists
            if (data.hasSkill(id)) {
                player.displayClientMessage(Component.literal("§cAlready unlocked!"), true);
                return;
            }

            // Prerequisite check (SkillRequirements class)
            SkillRequirements.RequirementResult result = SkillRequirements.check(id, data);
            if (!result.success()) {
                player.displayClientMessage(Component.literal("§c" + result.error()), true);
                return;
            }

            // Cost calculation: AUs cost 2, everything else (A and GU) costs 1
            int cost = (id.startsWith("upgrade_") || id.contains("upgrade")) ? 2 : 1;

            if (data.points() >= cost) {
                // Update Skill List
                List<String> newList = new ArrayList<>(data.unlockedSkills());
                newList.add(id);

                SkillData newData = new SkillData(
                        newList,
                        data.points() - cost,
                        data.getCurrentClass(),
                        data.totalPoints(),
                        data.cooldowns()
                );

                // Save and Sync
                player.setData(MagicUngathering.SKILL_DATA, newData);

                // Re-apply passives (Attributes)
                AbilityManager.applyAttributeModifiers(player, newData);

                if (player.containerMenu instanceof SkillTreeMenu menu) {
                    menu.refresh();
                }

                context.reply(new SyncSkillDataPacket(newData));
                AdvancementEventHandler.updatePlayerItems(player, newData.points(), newData.totalPoints());

                player.displayClientMessage(Component.literal("§aUnlocked: §6" + id), true);
            } else {
                player.displayClientMessage(Component.literal("§cNot enough AP! (Need " + cost + ")"), true);
            }
        });
    }
}