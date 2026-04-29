package net.mp9.magicungathering.mana;

import net.mp9.magicungathering.MagicUngathering;
import net.mp9.magicungathering.attributes.ModAttributes;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.attachment.AttachmentType;

@EventBusSubscriber(modid = MagicUngathering.MOD_ID)
public class ManaTickHandler {

    private static final int REGEN_TICKS = 20;

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (player.level().isClientSide()) return;

        AttachmentType<ManaData> manaType = ManaAttachment.MANA.get();
        ManaData mana = player.getData(manaType);
        if (mana == null) return;

        float currentMana = mana.currentMana();
        float maxManaLimit = (float) player.getAttributeValue(ModAttributes.MAX_MANA);

        // Fetch the regen rate from the new attribute (Default 0.02)
        float regenRate = (float) player.getAttributeValue(ModAttributes.MANA_REGEN);

        // --- SECTION 1: INSTANT CLAMPING ---
        if (currentMana > maxManaLimit) {
            currentMana = maxManaLimit;
            player.setData(manaType, new ManaData(currentMana));
        }

        // --- SECTION 2: REGENERATION ---
        if (player.tickCount % REGEN_TICKS == 0) {
            if (currentMana < maxManaLimit) {
                // Now uses the dynamic attribute value
                float regenAmount = maxManaLimit * regenRate;

                float nextManaValue = Math.min(currentMana + regenAmount, maxManaLimit);
                player.setData(manaType, new ManaData(nextManaValue));
            }
        }
    }
}