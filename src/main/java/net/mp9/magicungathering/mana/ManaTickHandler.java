package net.mp9.magicungathering.mana;

import net.mp9.magicungathering.MagicUngathering;
import net.minecraft.world.entity.player.Player;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.attachment.AttachmentType;

@EventBusSubscriber(modid = MagicUngathering.MOD_ID)
public class ManaTickHandler {

    // this sets the regen time for mana to every 20 ticks, or 1 second.
    private static final int REGEN_TICKS = 20;

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();

        if (player.level().isClientSide()) return; // ensures it is server side only. return means do nothing/end task

        AttachmentType<ManaData> manaType = ManaAttachment.MANA.get();
        ManaData mana = player.getData(manaType);
        if (mana == null) return;

        // regenerates mana every second (20 ticks)
        // if both player tick count and regen tick count are equal (20) then set tick count to 0 and run below command in if statement
        if (player.tickCount % REGEN_TICKS == 0) {
            ManaData newMana = mana.add(1); // generates 1 mana. this will have to be changed to a percent of max at some point
            player.setData(ManaAttachment.MANA.get(), newMana);
            player.syncData(ManaAttachment.MANA.get()); // send updated mana to client
        }
    }
}
