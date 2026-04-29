package net.mp9.magicungathering.network;

import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.mp9.magicungathering.MagicUngathering;
import net.mp9.magicungathering.SkillTreeMenu;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record OpenSkillTreePacket() implements CustomPacketPayload {
    public static final Type<OpenSkillTreePacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(MagicUngathering.MOD_ID, "open_skill_tree"));
    public static final StreamCodec<FriendlyByteBuf, OpenSkillTreePacket> STREAM_CODEC = StreamCodec.unit(new OpenSkillTreePacket());

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }

    public static void handle(final OpenSkillTreePacket payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer serverPlayer) {
                var data = serverPlayer.getData(MagicUngathering.SKILL_DATA);

                // get the class name
                String className = data.getCurrentClass();
                if (className == null || className.isEmpty()) {
                    className = "Unknown";
                }

                // make the dynamic title (note if this is changed also change the same thing in ClassSelectionMenu.java)
                Component dynamicTitle = Component.literal(className).withStyle(ChatFormatting.RED, ChatFormatting.BOLD)
                        .append(Component.literal(" - Skill Tree").withStyle(ChatFormatting.DARK_GRAY));
                // tell the server
                serverPlayer.openMenu(new SimpleMenuProvider(
                        (id, inv, p) -> new SkillTreeMenu(id, inv),
                        dynamicTitle
                ));
            }
        });
    }
}