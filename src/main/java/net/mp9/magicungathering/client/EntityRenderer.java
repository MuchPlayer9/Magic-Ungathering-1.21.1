package net.mp9.magicungathering.client;

import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.mp9.magicungathering.MagicUngathering;
import net.mp9.magicungathering.entity.ModEntities;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(modid = MagicUngathering.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class EntityRenderer {

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.SMOKE_GRENADE.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(ModEntities.CRYSTAL_PROJECTILE.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(ModEntities.FIREBALL_PROJECTILE.get(), ThrownItemRenderer::new);
    }
}