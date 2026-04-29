package net.mp9.magicungathering.network;

import net.mp9.magicungathering.MagicUngathering;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = MagicUngathering.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class ModNetworking {
    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(MagicUngathering.MOD_ID);

        registrar.playToClient(
                SyncSkillDataPacket.TYPE,
                SyncSkillDataPacket.STREAM_CODEC,
                SyncSkillDataPacket::handle
        );

        // Register Open Tree
        registrar.playToServer(
                OpenSkillTreePacket.TYPE,
                OpenSkillTreePacket.STREAM_CODEC,
                OpenSkillTreePacket::handle
        );

        // Register Unlock Skill
        registrar.playToServer(
                UnlockSkillPacket.TYPE,
                UnlockSkillPacket.STREAM_CODEC,
                UnlockSkillPacket::handle
        );

        registrar.playToServer(
                ActivateAbilityPacket.TYPE,
                ActivateAbilityPacket.STREAM_CODEC,
                ActivateAbilityPacket::handle
        );


    }
}