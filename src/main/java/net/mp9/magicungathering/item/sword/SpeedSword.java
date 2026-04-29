package net.mp9.magicungathering.item.sword;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import net.mp9.magicungathering.ModEffects;
import net.mp9.magicungathering.mana.ManaAttachment;
import net.mp9.magicungathering.mana.ManaData;

import java.util.Optional;

public class SpeedSword extends SwordItem {

    public SpeedSword() {
        super(Tiers.IRON, new Item.Properties().stacksTo(1));
    }

    @Override
    public ItemAttributeModifiers getDefaultAttributeModifiers() {
        ItemAttributeModifiers.Builder builder = ItemAttributeModifiers.builder();

        // Base Sword Stats: +6 Attack Damage (Diamond level)
        builder.add(Attributes.ATTACK_DAMAGE, new AttributeModifier(
                        BASE_ATTACK_DAMAGE_ID, 6.0, AttributeModifier.Operation.ADD_VALUE),
                EquipmentSlotGroup.MAINHAND);

        // Standard sword swing speed (-2.4 results in 1.6 total)
        builder.add(Attributes.ATTACK_SPEED, new AttributeModifier(
                        BASE_ATTACK_SPEED_ID, -2.4, AttributeModifier.Operation.ADD_VALUE),
                EquipmentSlotGroup.MAINHAND);

        return builder.build();
    }

    @Override
    public int getEnchantmentValue() {
        return 10;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (player.getCooldowns().isOnCooldown(this)) {
            return InteractionResultHolder.fail(stack);
        }

        ManaData mana = player.getData(ManaAttachment.MANA.get());
        int cost = 30;
        int cooldownTicks = 100;

        if (mana.currentMana() >= cost) {
            if (!level.isClientSide()) {
                // 1. Consume Mana
                player.setData(ManaAttachment.MANA.get(), mana.consume(cost));

                // 2. Apply Custom Timer Effect (HIDDEN)
                // We pass 'false' for visible and 'false' for showIcon.
                // This makes it act exactly like a direct attribute change.
                player.addEffect(new MobEffectInstance(
                        ModEffects.SPEED_BOOST_TIMER,
                        400,   // 20 seconds
                        0,     // Level 1 (Amplifier)
                        false, // isAmbient
                        false, // visible (No particles)
                        false  // showIcon (No HUD icon)
                ));

                // 3. Set Cooldown
                player.getCooldowns().addCooldown(this, cooldownTicks);

                // 4. Visual Effects (Manual particles since we hid the effect ones)
                AreaEffectCloud cloud = new AreaEffectCloud(level, player.getX(), player.getY() + 0.2, player.getZ());
                cloud.setParticle(ParticleTypes.ELECTRIC_SPARK);
                cloud.setRadius(2.0f);
                cloud.setDuration(40);
                cloud.setWaitTime(0);
                level.addFreshEntity(cloud);
            }
            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
        }

        return InteractionResultHolder.fail(stack);
    }
}