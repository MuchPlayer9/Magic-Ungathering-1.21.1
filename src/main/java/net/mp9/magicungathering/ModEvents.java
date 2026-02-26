package net.mp9.magicungathering.event;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.DyedItemColor;
import net.mp9.magicungathering.MagicUngathering;
import net.mp9.magicungathering.attributes.ModAttributes;
import net.minecraft.world.entity.EntityType;
import net.mp9.magicungathering.entity.ModEntities;
import net.mp9.magicungathering.entity.TemporaryCrystal;
import net.mp9.magicungathering.item.ModItems;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;

// IMPORTANT: This must be on the MOD bus
@EventBusSubscriber(modid = MagicUngathering.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModEvents {

    @SubscribeEvent
    public static void onAttributeModification(EntityAttributeModificationEvent event) {
        // This gives the "Max Mana" attribute to every player that spawns
        event.add(EntityType.PLAYER, ModAttributes.MAX_MANA);
    }

    @SubscribeEvent
    public static void registerItemColors(RegisterColorHandlersEvent.Item event) {
        // This tells the game: "For our Robe, look at the DYED_COLOR component to get the tint"
        event.register((stack, tintIndex) -> {
            if (tintIndex == 0) { // Only tint the first layer (layer0)
                DyedItemColor color = stack.get(DataComponents.DYED_COLOR);
                return color != null ? color.rgb() : -1; // -1 means no color (default)
            }
            return -1; // Layer 1 (overlay) stays untinted
        }, ModItems.TIER_ONE_ROBE.get());
    }
}