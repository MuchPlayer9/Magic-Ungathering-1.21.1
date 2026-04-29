package net.mp9.magicungathering.event;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.mp9.magicungathering.MagicUngathering;
import net.mp9.magicungathering.client.KeyInputHandler;
import net.mp9.magicungathering.network.ActivateAbilityPacket;
import net.mp9.magicungathering.network.OpenSkillTreePacket;

@EventBusSubscriber(modid = MagicUngathering.MOD_ID, value = Dist.CLIENT)
public class InputEventHandler {

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Player player = Minecraft.getInstance().player;
        if (player == null) return;

        // Handle the Skill Tree Menu Key
        if (KeyInputHandler.OPEN_MAGIC_MENU.consumeClick()) {
            PacketDistributor.sendToServer(new OpenSkillTreePacket());
        }

        // Handle the Movement Ability Key
        if (KeyInputHandler.MOVEMENT.consumeClick()) {
            PacketDistributor.sendToServer(new ActivateAbilityPacket("movement_skill"));
        }

        // Handle Ability 1
        if (KeyInputHandler.ABILITY_1.consumeClick()) {
            PacketDistributor.sendToServer(new ActivateAbilityPacket("ability_1"));
        }

        // Handle Ability 2
        if (KeyInputHandler.ABILITY_2.consumeClick()) {
            PacketDistributor.sendToServer(new ActivateAbilityPacket("ability_2"));
        }

        // Handle Ability 3
        if (KeyInputHandler.ABILITY_3.consumeClick()) {
            PacketDistributor.sendToServer(new ActivateAbilityPacket("ability_3"));
        }
    }
}