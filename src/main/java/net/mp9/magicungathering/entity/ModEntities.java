package net.mp9.magicungathering.entity;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.mp9.magicungathering.MagicUngathering;
import net.mp9.magicungathering.entity.TemporaryCrystal;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(Registries.ENTITY_TYPE, MagicUngathering.MOD_ID);

    // This is the "reference" you will use in your sword code
    public static final DeferredHolder<EntityType<?>, EntityType<TemporaryCrystal>> TEMP_CRYSTAL =
            ENTITIES.register("temporary_crystal",
                    () -> EntityType.Builder.of(TemporaryCrystal::new, MobCategory.MISC)
                            .sized(2.0f, 2.0f) // Matches standard End Crystal size
                            .build("temporary_crystal"));

    public static void register(IEventBus eventBus) {
        ENTITIES.register(eventBus);
    }
}