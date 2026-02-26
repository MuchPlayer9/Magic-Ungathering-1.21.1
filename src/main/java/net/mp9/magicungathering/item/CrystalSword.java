package net.mp9.magicungathering.item;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.Level;
import net.mp9.magicungathering.entity.TemporaryCrystal;
import net.mp9.magicungathering.mana.ManaAttachment;
import net.mp9.magicungathering.mana.ManaData;
import net.mp9.magicungathering.entity.ModEntities;
import net.mp9.magicungathering.entity.TemporaryCrystal;

public class CrystalSword extends SwordItem {

    public CrystalSword() {
        super(Tiers.IRON, new Properties().stacksTo(1));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (player.getCooldowns().isOnCooldown(this)) {
            return InteractionResultHolder.fail(player.getItemInHand(hand));
        }

        ItemStack stack = player.getItemInHand(hand);
        ManaData mana = player.getData(ManaAttachment.MANA.get());

        int cost = 50; // Higher cost for a powerful entity
        int cooldownTicks = 120; // 6 second cooldown

        if (mana.currentMana() >= cost) {
            if (!level.isClientSide()) {
                // Consume mana
                ManaData newMana = mana.consume(cost);
                player.setData(ManaAttachment.MANA.get(), newMana);

                // Create the End Crystal
                TemporaryCrystal crystal = ModEntities.TEMP_CRYSTAL.get().create(level);
                if (crystal != null) {
                    // Set position to the player's eye level (the "head")
                    crystal.moveTo(player.getX(), player.getEyeY(), player.getZ(), 0, 0);

                    // Optional: Remove the bedrock base if you want it floating cleanly
                    crystal.setShowBottom(false);
                    //makes the crystal unbreakable, later scheduled 0.5 seconds later to make vulnerable for a grace period
                    crystal.setInvulnerable(true);
                    level.addFreshEntity(crystal);
                }

                // Set the cooldown
                player.getCooldowns().addCooldown(this, cooldownTicks);
            }
            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
        }

        return InteractionResultHolder.pass(stack);
    }
}