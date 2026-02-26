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

        // Only run logic on the server to prevent desync
        if (player.level().isClientSide()) return;

        AttachmentType<ManaData> manaType = ManaAttachment.MANA.get();
        ManaData mana = player.getData(manaType);
        if (mana == null) return;

        // 1. Get the dynamic Max Mana from our attribute system
        int maxManaLimit = (int) player.getAttributeValue(ModAttributes.MAX_MANA);

        if (player.tickCount % REGEN_TICKS == 0) {
            float currentMana = mana.currentMana();

            // 2. Only regenerate if we are currently below the max
            if (currentMana < maxManaLimit) {
                // calculate percentage regen
                float regenAmount = (float)(maxManaLimit * 0.01);

                // Use Math.min to ensure we don't "overshoot" the max mana
                float nextManaValue = Math.min(currentMana + regenAmount, maxManaLimit);

                ManaData newMana = new ManaData(nextManaValue);
                player.setData(manaType, newMana);

                // Optional: Force a sync if your attachment isn't auto-syncing on change
                // player.syncData(manaType);
            }
            // 3. Optional: If player removes armor and current mana is > new max,
            // you can choose to cap it immediately or let it drain.
            else if (currentMana > maxManaLimit) {
                player.setData(manaType, new ManaData(maxManaLimit));
            }
        }
    }
}