package net.mp9.magicungathering.item.staff.crystal;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.mp9.magicungathering.entity.ModEntities;
import net.mp9.magicungathering.mana.ManaAttachment;
import net.mp9.magicungathering.mana.ManaData;

public class CrystalLauncher extends Item {
    public CrystalLauncher() {
        super(new Item.Properties().stacksTo(1));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);

        // 1. Get the Mana Data from the player
        // Note: Replace 'ModDataTypes.MANA' with whatever your actual Mana registration name is
        float currentMana = player.getData(ManaAttachment.MANA).currentMana();
        float manaCost = 120.0f; // Set cost here

        // 2. Check if the player has enough mana
        if (currentMana >= manaCost) {
            if (!level.isClientSide()) {
                // 3. Subtract the mana
                player.setData(ManaAttachment.MANA, new ManaData(currentMana - manaCost));

                // 4. Your existing crystal spawning logic
                CrystalProjectile crystal = new CrystalProjectile(ModEntities.CRYSTAL_PROJECTILE.get(), level);
                crystal.setOwner(player);
                crystal.setPos(player.getX(), player.getEyeY(), player.getZ());

                Vec3 look = player.getLookAngle();
                double speed = 2.5;
                crystal.setDeltaMovement(look.x * speed, look.y * speed, look.z * speed);

                level.addFreshEntity(crystal);

                // Optional: Play a "Success" sound
                level.playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.ENDER_EYE_LAUNCH, SoundSource.PLAYERS, 1.0F, 1.0F);
            }

            player.getCooldowns().addCooldown(this, 20); // 1 second cooldown
            return InteractionResultHolder.sidedSuccess(itemstack, level.isClientSide());
        } else {
            // 5. If not enough mana, play a "Fail" sound on the client
            if (level.isClientSide) {
                player.playSound(SoundEvents.VILLAGER_NO, 1.0f, 1.0f);
            }
            return InteractionResultHolder.fail(itemstack);
        }
    }
}