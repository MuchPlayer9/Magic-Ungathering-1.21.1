package net.mp9.magicungathering;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.mp9.magicungathering.effect.BleedingEffect;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ModEffects {
    public static final DeferredRegister<MobEffect> MOB_EFFECTS =
            DeferredRegister.create(Registries.MOB_EFFECT, "magicungathering");

    public static final DeferredHolder<MobEffect, MobEffect> SPEED_BOOST_TIMER = MOB_EFFECTS.register("speed_boost_timer",
            () -> new EmptyEffect(MobEffectCategory.BENEFICIAL, 0x34f1ff)
                    .addAttributeModifier(Attributes.MOVEMENT_SPEED,
                            ResourceLocation.fromNamespaceAndPath("magicungathering", "speed_sword_boost"),
                            0.4,
                            AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));

    // Adds Brittle Bones Effect
    public static final DeferredHolder<MobEffect, MobEffect> BRITTLE_BONES = MOB_EFFECTS.register("brittle_bones",
            () -> new EmptyEffect(MobEffectCategory.HARMFUL, 0xD1C4E9));

    public static final DeferredHolder<MobEffect, BleedingEffect> BLEEDING =
            MOB_EFFECTS.register("bleeding", BleedingEffect::new);

    public static final DeferredHolder<MobEffect, MobEffect> HAEMORRHAGE_WINDOW =
            MOB_EFFECTS.register("haemorrhage_window", () ->
                    new EmptyEffect(MobEffectCategory.BENEFICIAL, 0xFF4500));

    private static class EmptyEffect extends MobEffect {
        protected EmptyEffect(MobEffectCategory category, int color) {
            super(category, color);
        }
    }

    public static void register(IEventBus eventBus) {
        MOB_EFFECTS.register(eventBus);
    }
}