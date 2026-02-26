package net.mp9.magicungathering.item;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.Level;
import net.mp9.magicungathering.mana.ManaAttachment;
import net.mp9.magicungathering.mana.ManaData;


// defines the speed sword as a sword item
public class JumpSword extends SwordItem {

    // sets the tier of the sword to iron and max stack size to 1
    public JumpSword() {
        super(Tiers.IRON, new Properties().stacksTo(1));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (player.getCooldowns().isOnCooldown(this)) {
            return InteractionResultHolder.fail(player.getItemInHand(hand));
        }
        ItemStack stack = player.getItemInHand(hand);

        ManaData mana = player.getData(ManaAttachment.MANA.get());

        // establishes the mana cost. will have to be changed if adding something like ultimate wise
        int cost = 20;
        // adds a static cooldown amount in ticks
        int cooldownTicks = 100;
        // if current mana is greater than or equal to cost, i.e. make sure enough mana for ability
        if (mana.currentMana() >= cost) {
            // logic for server side
            if (!level.isClientSide()) {
                // consumes the mana
                ManaData newMana = mana.consume(cost);
                // sets mana to correct amount
                player.setData(ManaAttachment.MANA.get(), newMana);

                // gives speed 2 for 20 seconds
                player.addEffect(new MobEffectInstance(MobEffects.JUMP, 10 * 20, 3));

                // set the cooldown
                player.getCooldowns().addCooldown(this, cooldownTicks);

                AreaEffectCloud cloud = new AreaEffectCloud(level, player.getX(), player.getY() + 0.2, player.getZ());
                cloud.setParticle(ParticleTypes.HAPPY_VILLAGER);
                cloud.setRadius(2.0f);
                cloud.setDuration(40);
                cloud.setWaitTime(0);
                level.addFreshEntity(cloud);

            }
        }
        return InteractionResultHolder.pass(stack);
    }
}
