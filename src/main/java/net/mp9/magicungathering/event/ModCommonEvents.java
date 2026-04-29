package net.mp9.magicungathering.event;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.mp9.magicungathering.MagicUngathering;
import net.mp9.magicungathering.attachment.SkillData;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.AdvancementEvent;

@EventBusSubscriber(modid = MagicUngathering.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class ModCommonEvents {

    @SubscribeEvent
    public static void onAdvancement(AdvancementEvent.AdvancementEarnEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            String id = event.getAdvancement().id().toString();

            if (id.contains("magicungathering")) {
                SkillData data = player.getData(MagicUngathering.SKILL_DATA);
                player.setData(MagicUngathering.SKILL_DATA, data.addPoints(1));
                player.sendSystemMessage(Component.literal("Ability Point Earned!")
                        .withStyle(ChatFormatting.AQUA, ChatFormatting.BOLD));
            }
        }
    }

}