package net.mp9.magicungathering.event;

import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.entity.EndCrystalRenderer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.component.DyedItemColor;
import net.mp9.magicungathering.MagicUngathering;
import net.mp9.magicungathering.client.ClassSelectionScreen;
import net.mp9.magicungathering.client.renderer.SmokeCloudRenderer;
import net.mp9.magicungathering.entity.ModEntities;
import net.mp9.magicungathering.item.ModItems;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.mp9.magicungathering.registration.ModMenuTypes;
import net.mp9.magicungathering.client.SkillTreeScreen;
import net.neoforged.neoforge.event.entity.player.AdvancementEvent;

@EventBusSubscriber(modid = MagicUngathering.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModClientEvents {

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.TEMP_CRYSTAL.get(), EndCrystalRenderer::new);

        event.registerEntityRenderer(ModEntities.CRYSTAL_PROJECTILE.get(), EndCrystalRenderer::new);

        // 2.0F makes the temporary fireball big above the player
        event.registerEntityRenderer(ModEntities.TEMP_FIREBALL.get(),
                context -> new net.mp9.magicungathering.client.renderer.BigFireballRenderer<>(context, 2.0F));

        // 1.5F makes the flying projectile slightly smaller
        event.registerEntityRenderer(ModEntities.FIREBALL_PROJECTILE.get(),
                context -> new net.mp9.magicungathering.client.renderer.BigFireballRenderer<>(context, 1.5F));

        // smoke screen smoke cloud
        event.registerEntityRenderer(ModEntities.SMOKE_CLOUD.get(), SmokeCloudRenderer::new);
    }

    @SubscribeEvent
    public static void registerItemColors(RegisterColorHandlersEvent.Item event) {
        event.register((stack, tintIndex) -> {
            if (tintIndex == 0) {
                DyedItemColor color = stack.get(DataComponents.DYED_COLOR);
                return color != null ? color.rgb() : -1;
            }
            return -1;
        }, ModItems.TIER_ONE_ROBE.get());
    }

    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        // Skill Tree
        event.register(ModMenuTypes.SKILL_TREE_MENU.get(), SkillTreeScreen::new);

        // Class Selection
        event.register(ModMenuTypes.CLASS_SELECTION_MENU.get(), ClassSelectionScreen::new);
    }
}