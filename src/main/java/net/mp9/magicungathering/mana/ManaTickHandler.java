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

        // --- SECTION 1: INSTANT CLAMPING (Runs every tick) ---
        // This handles armor removal or abilities that set mana too high immediately.
        if (currentMana > maxManaLimit) {
            currentMana = maxManaLimit;
            player.setData(manaType, new ManaData(currentMana));
            // Force sync here if needed so the client UI updates instantly
        }

        // --- SECTION 2: REGENERATION (Runs every second) ---
        if (player.tickCount % REGEN_TICKS == 0) {
            if (currentMana < maxManaLimit) {
                float regenAmount = maxManaLimit * 0.01f; // 1% regen

                // Math.min is the "safety net" for the regen specifically
                float nextManaValue = Math.min(currentMana + regenAmount, maxManaLimit);

                player.setData(manaType, new ManaData(nextManaValue));
            }
        }
    }
}