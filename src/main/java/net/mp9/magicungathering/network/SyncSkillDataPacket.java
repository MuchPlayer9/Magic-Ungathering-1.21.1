package net.mp9.magicungathering.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.mp9.magicungathering.MagicUngathering;
import net.mp9.magicungathering.SkillTreeMenu;
import net.mp9.magicungathering.attachment.SkillData;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SyncSkillDataPacket(SkillData data) implements CustomPacketPayload {
    public static final Type<SyncSkillDataPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(MagicUngathering.MOD_ID, "sync_skill_data"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SyncSkillDataPacket> STREAM_CODEC = StreamCodec.composite(
            SkillData.STREAM_CODEC,
            SyncSkillDataPacket::data,
            SyncSkillDataPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }

    public static void handle(final SyncSkillDataPacket payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            // This runs on the Client computer
            context.player().setData(MagicUngathering.SKILL_DATA, payload.data());

            // if the menu is open, we force the client to redraw its slots using the data we just received
            if (context.player().containerMenu instanceof SkillTreeMenu menu) {
                menu.refresh();
            }
        });
    }
}