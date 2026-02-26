package net.mp9.magicungathering.item;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.mp9.magicungathering.mana.ManaAttachment;
import net.mp9.magicungathering.mana.ManaData;

// defines the dash sword as a sword item
public class DashSword extends SwordItem {

    // sets the tier of the sword to iron and max stack size to 1
    public DashSword() {
        super(Tiers.IRON, new Item.Properties().stacksTo(1));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (player.getCooldowns().isOnCooldown(this)) {
            return InteractionResultHolder.fail(player.getItemInHand(hand));
        }
        ItemStack stack = player.getItemInHand(hand);

        ManaData mana = player.getData(ManaAttachment.MANA.get());

        // establishes the mana cost. will have to be changed if adding something like ultimate wise
        int cost = 60;
        // adds a static cooldown amount in ticks
        int cooldownTicks = 20;
        // if current mana is greater than or equal to cost, i.e. make sure enough mana for ability
        if (mana.currentMana() >= cost) {
            // logic for server side
            if (!level.isClientSide()) {
                // consumes the mana
                ManaData newMana = mana.consume(cost);
                // sets mana to correct amount
                player.setData(ManaAttachment.MANA.get(), newMana);

                // calculate the dash
                Vec3 lookVec = player.getLookAngle(); // gets player direction
                double dashPower = 2.5; // adjust this for distance

                // apply the dash velocity
                player.setDeltaMovement(lookVec.x * dashPower, lookVec.y * (dashPower / 2), lookVec.z * dashPower);

                // mark player as hurt in the air so they don't immediately take fall damage
                player.hurtMarked = true;

                // set the cooldown
                player.getCooldowns().addCooldown(this, cooldownTicks);

                ((ServerLevel)level).sendParticles(ParticleTypes.POOF,
                        player.getX(), player.getY(), player.getZ(),
                        15, 0.2, 0.2, 0.2, 0.1);

            }
        }
        return InteractionResultHolder.pass(stack);
    }
}

