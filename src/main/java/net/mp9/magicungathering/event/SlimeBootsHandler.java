package net.mp9.magicungathering.event;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.mp9.magicungathering.MagicUngathering;
import net.mp9.magicungathering.item.ModItems;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingFallEvent;

@EventBusSubscriber(modid = MagicUngathering.MOD_ID)
public class SlimeBootsHandler {

    @SubscribeEvent
    public static void onFall(LivingFallEvent event) {
        if (event.getEntity() instanceof Player player) { // this probs needs to be changed to allow mobs to use them too
            ItemStack boots = player.getItemBySlot(EquipmentSlot.FEET);

            // Check if the player is wearing your specific boots
            if (boots.is(ModItems.SLIME_BOOTS.get())) {
                // Cancel fall damage entirely
                event.setDamageMultiplier(0);

                // Apply the bounce logic
                // Only bounce if they fell more than 1 blocks to prevent jitter
                if (event.getDistance() > 1.0F && !player.isShiftKeyDown()) {
                    double bounceVelocity = Math.min(event.getDistance() * 0.14D, 1.4D);
                    player.setDeltaMovement(player.getDeltaMovement().x, bounceVelocity, player.getDeltaMovement().z);
                    player.hurtMarked = true; // Syncs movement to the client

                    // play the slime sound
                    player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                            SoundEvents.SLIME_BLOCK_FALL, player.getSoundSource(), 1.0F, 1.0F);
                }
            }
        }
    }
}