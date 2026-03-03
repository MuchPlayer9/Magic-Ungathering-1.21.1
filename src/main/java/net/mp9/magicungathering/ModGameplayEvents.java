package net.mp9.magicungathering.event;

import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.mp9.magicungathering.MagicUngathering;
import net.mp9.magicungathering.item.ModArmorMaterials;
import net.mp9.magicungathering.item.ModRobeItem;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

@EventBusSubscriber(modid = MagicUngathering.MOD_ID)
public class ModGameplayEvents {

    // This manually points to #minecraft:is_magic
    private static final TagKey<DamageType> IS_MAGIC = TagKey.create(Registries.DAMAGE_TYPE,
            ResourceLocation.withDefaultNamespace("is_magic"));

    @SubscribeEvent
    public static void onArmorTick(EntityTickEvent.Post event) {
        if (event.getEntity() instanceof Player player) {
            if (player.tickCount % 10 == 0) {

                // 1. Check if they have ANY full set (Mixed or Matching)
                if (ModRobeItem.isWearingAnyRobeSet(player)) {

                    if (!player.level().isClientSide && player.level() instanceof ServerLevel serverLevel) {

                        // 2. Check if it's the PERFECT Tier 2 set for the best particles
                        if (ModRobeItem.hasFullSet(player, ModArmorMaterials.TIER_TWO)) {
                            serverLevel.sendParticles(ParticleTypes.WITCH,
                                    player.getX(), player.getY() + 0.1, player.getZ(),
                                    3, 0.3, 0.1, 0.3, 0.01);
                            player.displayClientMessage(Component.literal("★ Greater Spell Shield ★").withStyle(ChatFormatting.GOLD), true);
                        }
                        // 3. Otherwise, it's a mixed set or Tier 1
                        else {
                            serverLevel.sendParticles(ParticleTypes.INSTANT_EFFECT,
                                    player.getX(), player.getY() + 0.1, player.getZ(),
                                    3, 0.3, 0.1, 0.3, 0.01);
                            player.displayClientMessage(Component.literal("★ Spell Shield ★").withStyle(ChatFormatting.AQUA), true);
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onSpellDamage(net.neoforged.neoforge.event.entity.living.LivingDamageEvent.Pre event) {
        if (event.getEntity() instanceof Player player) {

            // Check if the damage source is tagged as Magic/Spell damage
// Correct 1.21.1 reference for Magic Damage
            if (event.getSource().is(net.minecraft.tags.DamageTypeTags.WITCH_RESISTANT_TO)) {
                float reduction = 1.0f; // Default: No reduction

                // 1. Check for PERFECT Tier 2 set (50% reduction)
                if (ModRobeItem.hasFullSet(player, ModArmorMaterials.TIER_TWO)) {
                    reduction = 0.50f;
                }
                // 2. Check for ANY other combination (Tier 1 or Mixed) (25% reduction)
                else if (ModRobeItem.isWearingAnyRobeSet(player)) {
                    reduction = 0.75f;
                }

                // If a reduction was applied
                if (reduction < 1.0f) {
                    float originalDamage = event.getNewDamage();
                    event.setNewDamage(originalDamage * reduction);

                    // Visual/Audio Feedback when the shield absorbs damage
                    if (!player.level().isClientSide && player.level() instanceof net.minecraft.server.level.ServerLevel serverLevel) {

                        // Spawn a "burst" of particles to show the shield in action
                        serverLevel.sendParticles(net.minecraft.core.particles.ParticleTypes.ENCHANT,
                                player.getX(), player.getY() + 1.0, player.getZ(),
                                10, 0.5, 0.5, 0.5, 0.05);

                        // Play a subtle magical sound
                        player.level().playSound(null, player.blockPosition(),
                                net.minecraft.sounds.SoundEvents.AMETHYST_BLOCK_CHIME,
                                net.minecraft.sounds.SoundSource.PLAYERS, 1.0F, 0.8F);
                    }
                }
            }
        }
    }
}