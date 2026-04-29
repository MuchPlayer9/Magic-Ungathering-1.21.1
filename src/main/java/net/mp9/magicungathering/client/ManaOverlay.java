package net.mp9.magicungathering.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.mp9.magicungathering.MagicUngathering;
import net.mp9.magicungathering.attributes.ModAttributes;
import net.mp9.magicungathering.mana.ManaAttachment;
import net.mp9.magicungathering.mana.ManaData;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;

@EventBusSubscriber(modid = MagicUngathering.MOD_ID, value = Dist.CLIENT)
public class ManaOverlay {

    private static final ResourceLocation MANA_BAR = ResourceLocation.fromNamespaceAndPath(MagicUngathering.MOD_ID, "textures/gui/mana_bar_full.png");
    private static final ResourceLocation CHAT_LAYER = ResourceLocation.withDefaultNamespace("chat");

    @SubscribeEvent
    public static void registerGuiLayers(RegisterGuiLayersEvent event) {
        final ResourceLocation MANA_EMPTY = ResourceLocation.fromNamespaceAndPath(MagicUngathering.MOD_ID, "textures/gui/mana_bar_empty.png");
        final ResourceLocation MANA_FULL = ResourceLocation.fromNamespaceAndPath(MagicUngathering.MOD_ID, "textures/gui/mana_bar_full.png");

        event.registerBelow(CHAT_LAYER, ResourceLocation.fromNamespaceAndPath(MagicUngathering.MOD_ID, "mana_bar"), (guiGraphics, partialTick) -> {
            Minecraft mc = Minecraft.getInstance();
            Player player = mc.player;
            if (player == null) return;

            ManaData mana = player.getData(ManaAttachment.MANA.get());
            if (mana == null) return;

            int current = (int) mana.currentMana();
            int max = (int) player.getAttributeValue(ModAttributes.MAX_MANA);
            if (max <= 0) max = 100;

            int texW = 16;
            int texH = 64;
            float barScale = 2.0f;

            // Position on screen
            int x = 10;
            int y = mc.getWindow().getGuiScaledHeight() - (int)(texH * barScale) - 10;

            RenderSystem.enableBlend();

            // --- BAR RENDERING ---
            guiGraphics.pose().pushPose();
            guiGraphics.pose().scale(barScale, barScale, 1.0f);

            int sx = (int)(x / barScale);
            int sy = (int)(y / barScale);

            // 1. Draw the Background (Full 16x64 frame)
            guiGraphics.blit(MANA_EMPTY, sx, sy, 0, 0, texW, texH, texW, texH);

            // 2. Calculate Inner Fill
            int border = 2; // Number of static pixels at top/bottom
            int innerHeight = texH - (border * 2); // 60 pixels

            float pct = Math.min(1.0f, (float) current / max);
            int filledInnerHeight = (int)(innerHeight * pct);

            // The offset now starts 2 pixels down from the top of the bar
            int topOffset = border + (innerHeight - filledInnerHeight);

            // 3. Draw the Fill
            // We start drawing at (sy + topOffset)
            // We sample the full texture starting at (V = topOffset)
            if (filledInnerHeight > 0) {
                guiGraphics.blit(MANA_FULL, sx, sy + topOffset, 0, topOffset, texW, filledInnerHeight, texW, texH);
            }

            guiGraphics.pose().popPose();

            // text rendering
            float textScale = 2.0f; // text size scale adjustment
            String text = current + " / " + max;

            guiGraphics.pose().pushPose();
            guiGraphics.pose().scale(textScale, textScale, 1.0f);

            // Position the text relative to the scaled bar's width
            int textX = (int)((x + (int)(texW * barScale) + 8) / textScale);
            int textY = (int)((y + (int)(texH * barScale) - 15) / textScale);

            guiGraphics.drawString(mc.font, text, textX, textY, 0x00FFFF, false);

            guiGraphics.pose().popPose();
        });
    }
}