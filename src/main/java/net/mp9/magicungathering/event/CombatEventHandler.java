package net.mp9.magicungathering.event;

import com.mojang.logging.LogUtils;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.mp9.magicungathering.MagicUngathering;
import net.mp9.magicungathering.ModEffects;
import net.mp9.magicungathering.attachment.SkillData;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import org.slf4j.Logger;

@EventBusSubscriber(modid = MagicUngathering.MOD_ID)
public class CombatEventHandler {
    private static final Logger LOGGER = LogUtils.getLogger();

    @SubscribeEvent
    public static void onLivingHurt(LivingIncomingDamageEvent event) {
        LivingEntity victim = event.getEntity();
        if (victim == null) return;

        // Multiplier Logic
        // Use isBound() to check if the registry holder is ready
        if (ModEffects.BRITTLE_BONES.isBound() && victim.hasEffect(ModEffects.BRITTLE_BONES)) {
            event.setAmount(event.getAmount() * 1.2f);
        }

        if (event.getSource().getEntity() instanceof Player attacker) {
            SkillData data = attacker.getData(MagicUngathering.SKILL_DATA);

            // Application Logic
            if (data.hasSkill("upgrade_execute_bones") && attacker.hasEffect(MobEffects.DAMAGE_BOOST)) {

                // Only apply if the effect is actually registered and ready
                if (ModEffects.BRITTLE_BONES.isBound()) {
                    victim.addEffect(new MobEffectInstance(ModEffects.BRITTLE_BONES, 100, 0));
                }
            }
        }
    }

    @SubscribeEvent
    public static void onLivingHurt(LivingDamageEvent.Post event) {
        // 1. Was the attacker a player?
        if (event.getSource().getEntity() instanceof Player attacker) {

            // 2. Is the player currently in the "Haemorrhage" window?
            // Note: Using the holder directly works, but .get() is safer if using holders
            if (attacker.hasEffect(ModEffects.HAEMORRHAGE_WINDOW)) {

                // 3. Use getData instead of getCapability
                SkillData data = attacker.getData(MagicUngathering.SKILL_DATA);

                int duration = 130; // 6.5s base
                int amplifier = 0;  // 1hp/s base

                if (data.hasSkill("upgrade_haemorrhage_damage")) {
                    duration = 90;  // 4.5s
                    amplifier = 2;  // 2hp/s (Doubled)
                }

                // Apply the effect to the victim
                event.getEntity().addEffect(new MobEffectInstance(ModEffects.BLEEDING, duration, amplifier));
            }
        }
    }

    @SubscribeEvent
    public static void onLivingHurtPost(LivingDamageEvent.Post event) {
        if (event.getSource().getEntity() instanceof Player player) {

            // Check if the player is invisible (Vanish)
            if (player.hasEffect(MobEffects.INVISIBILITY)) {
                SkillData data = player.getData(MagicUngathering.SKILL_DATA);

                // Upgrade: Lifesteal (10%)
                if (data.hasSkill("upgrade_vanish_lifesteal")) {
                    float damageDealt = event.getNewDamage();
                    float healAmount = damageDealt * 0.10f; // x% lifesteal

                    if (healAmount > 0) {
                        player.heal(healAmount);

                        // Optional: Small heart particles for feedback
                        if (player.level() instanceof ServerLevel serverLevel) {
                            serverLevel.sendParticles(ParticleTypes.HEART,
                                    player.getX(), player.getY() + 1.0, player.getZ(),
                                    3, 0.2, 0.2, 0.2, 0.05);
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onEffectApplied(MobEffectEvent.Applicable event) {
        if (event.getEntity() instanceof Player victim) {

        }
    }
}