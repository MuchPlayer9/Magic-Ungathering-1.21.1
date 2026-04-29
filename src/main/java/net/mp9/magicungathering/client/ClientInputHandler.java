package net.mp9.magicungathering.client;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.mp9.magicungathering.MagicUngathering;
import net.mp9.magicungathering.network.OpenSkillTreePacket;

@EventBusSubscriber(modid = MagicUngathering.MOD_ID, value = Dist.CLIENT)
public class ClientInputHandler {

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        while (KeyInputHandler.OPEN_MAGIC_MENU.consumeClick()) {
            PacketDistributor.sendToServer(new OpenSkillTreePacket());
        }
    }
}