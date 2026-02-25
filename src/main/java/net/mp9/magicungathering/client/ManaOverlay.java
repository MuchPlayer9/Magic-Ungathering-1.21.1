package net.mp9.magicungathering.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.mp9.magicungathering.MagicUngathering;
import net.mp9.magicungathering.mana.ManaAttachment;
import net.mp9.magicungathering.mana.ManaData;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;

@EventBusSubscriber(modid = MagicUngathering.MOD_ID, value = Dist.CLIENT)
public class ManaOverlay {

    // generates the mana bar texture
    private static final ResourceLocation MANA_BAR =
            ResourceLocation.fromNamespaceAndPath(MagicUngathering.MOD_ID, "textures/gui/mana_bar.png");

    // sets minecraft chat layer as CHAT_LAYER
    private static final ResourceLocation CHAT_LAYER =
            ResourceLocation.withDefaultNamespace("chat");

    @SubscribeEvent
    public static void registerGuiLayers(RegisterGuiLayersEvent event) {
        // sets mana bar texture gui below chat layer
        event.registerBelow(
                CHAT_LAYER,
                ResourceLocation.fromNamespaceAndPath(MagicUngathering.MOD_ID, "mana_bar"),
                ((guiGraphics, partialTick) -> {

                    Minecraft mc = Minecraft.getInstance();
                    if (mc.player == null) return;

                    AttachmentType<ManaData> manaType = ManaAttachment.MANA.get();
                    ManaData mana = mc.player.getData(manaType); // returns ManaData

                    if (mana == null) return;

                    // read values from ManaData
                    // System.out.println("Regenerated mana for " + player.getName().getString() + " = " + newMana.currentMana());
                    int current = mana.currentMana();
                    int max = mana.maxMana();

                    int texW = 16;
                    int texH = 64;
                    float scale = 2.0f;

                    int x = 10;
                    int y = mc.getWindow().getGuiScaledHeight() - (int)(texH * scale) -10;

                    RenderSystem.enableBlend();
                    guiGraphics.pose().pushPose();
                    guiGraphics.pose().scale(scale, scale, 1.0f);

                    int sx = (int)(x / scale);
                    int sy = (int)(y / scale);

                    // Draw background
                    guiGraphics.blit(MANA_BAR, sx, sy, 0, 0, texW, texH, texW, texH);

                    //Draw filled portion
                    float pct = (float) current / Math.max(1, max);
                    int filled = (int)(texH * pct);
                    guiGraphics.blit(MANA_BAR, sx, sy + (texH - filled), 0, texH - filled, texW, filled, texW, texH);

                    // Draw text
                    String text = current + " / " + max;
                    guiGraphics.drawString(mc.font,text, sx + texW + 4, sy + texH - 10, 0x00FFFF, false);

                    guiGraphics.pose().popPose();

                })
        );
    }
}
