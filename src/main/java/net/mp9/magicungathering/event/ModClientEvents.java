package net.mp9.magicungathering.event;

import net.minecraft.client.renderer.entity.EndCrystalRenderer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.DyedItemColor;
import net.mp9.magicungathering.MagicUngathering;
import net.mp9.magicungathering.entity.ModEntities;
import net.mp9.magicungathering.item.ModItems;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;

@EventBusSubscriber(modid = MagicUngathering.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModClientEvents {

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        // This links your custom entity to the vanilla End Crystal look
        event.registerEntityRenderer(ModEntities.TEMP_CRYSTAL.get(), EndCrystalRenderer::new);

        event.registerEntityRenderer(ModEntities.CRYSTAL_PROJECTILE.get(), EndCrystalRenderer::new);
    }

    @SubscribeEvent
    public static void registerItemColors(RegisterColorHandlersEvent.Item event) {
        event.register((stack, tintIndex) -> {
            // tintIndex 0 corresponds to "layer0" in your JSON
            if (tintIndex == 0) {
                DyedItemColor color = stack.get(DataComponents.DYED_COLOR);
                return color != null ? color.rgb() : -1;
            }
            // tintIndex 1 corresponds to "layer1" (the straps), which we don't tint
            return -1;
        }, ModItems.TIER_ONE_ROBE.get());
    }
}