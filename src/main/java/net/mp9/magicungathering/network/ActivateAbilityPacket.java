package net.mp9.magicungathering.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.mp9.magicungathering.MagicUngathering;
import net.mp9.magicungathering.logic.AbilityManager;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ActivateAbilityPacket(String abilityId) implements CustomPacketPayload {
    public static final Type<ActivateAbilityPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(MagicUngathering.MOD_ID, "activate_ability"));

    public static final StreamCodec<FriendlyByteBuf, ActivateAbilityPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            ActivateAbilityPacket::abilityId,
            ActivateAbilityPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(final ActivateAbilityPacket payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer player) {
                System.out.println("[DEBUG] SERVER RECEIVED PACKET. ID: " + payload.abilityId());
                // This calls the logic for the specific ability
                AbilityManager.tryActivate((ServerPlayer) context.player(), payload.abilityId());
            }
        });
    }
}