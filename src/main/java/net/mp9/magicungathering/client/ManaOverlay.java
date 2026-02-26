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

    private static final ResourceLocation MANA_BAR = ResourceLocation.fromNamespaceAndPath(MagicUngathering.MOD_ID, "textures/gui/mana_bar.png");
    private static final ResourceLocation CHAT_LAYER = ResourceLocation.withDefaultNamespace("chat");

    @SubscribeEvent
    public static void registerGuiLayers(RegisterGuiLayersEvent event) {
        event.registerBelow(CHAT_LAYER, ResourceLocation.fromNamespaceAndPath(MagicUngathering.MOD_ID, "mana_bar"), (guiGraphics, partialTick) -> {

            Minecraft mc = Minecraft.getInstance();
            Player player = mc.player;
            if (player == null) return;

            ManaData mana = player.getData(ManaAttachment.MANA.get());
            if (mana == null) return;

            // 1. Fetch Dynamic Max Mana from Attribute
            int current = (int) mana.currentMana();
            int max = (int) player.getAttributeValue(ModAttributes.MAX_MANA);
            if (max <= 0) max = 100; // Fallback to prevent division by zero

            int texW = 16;
            int texH = 64;
            float scale = 2.0f;

            int x = 10;
            int y = mc.getWindow().getGuiScaledHeight() - (int)(texH * scale) - 10;

            RenderSystem.enableBlend();
            guiGraphics.pose().pushPose();
            guiGraphics.pose().scale(scale, scale, 1.0f);

            int sx = (int)(x / scale);
            int sy = (int)(y / scale);

            // 2. Draw Background (The empty frame)
            // Assumes your background is at (0, 0) in the 16x64 texture
            guiGraphics.blit(MANA_BAR, sx, sy, 0, 0, texW, texH, texW, texH * 2);

            // 3. Draw Filled Portion (From bottom to top)
            // Assumes your filled texture is at (0, 64) in the texture file (a 16x128 total image)
            float pct = Math.min(1.0f, (float) current / max);
            int filledHeight = (int)(texH * pct);
            int offset = texH - filledHeight;

            // blit parameters: (texture, x, y, uOffset, vOffset, width, height, texWidth, texHeight)
            guiGraphics.blit(MANA_BAR, sx, sy + offset, 0, texH + offset, texW, filledHeight, texW, texH * 2);

            // 4. Draw Text
            String text = current + " / " + max;
            guiGraphics.drawString(mc.font, text, sx + texW + 4, sy + texH - 10, 0x00FFFF, false);

            guiGraphics.pose().popPose();
        });
    }
}