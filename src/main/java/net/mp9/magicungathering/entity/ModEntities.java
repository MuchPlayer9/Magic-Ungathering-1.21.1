package net.mp9.magicungathering.entity;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.mp9.magicungathering.MagicUngathering;
import net.mp9.magicungathering.item.staff.crystal.CrystalProjectile;
import net.mp9.magicungathering.item.staff.crystal.TemporaryCrystal;
import net.mp9.magicungathering.item.staff.fireball.FireballProjectile;
import net.mp9.magicungathering.item.staff.fireball.TemporaryFireball;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(Registries.ENTITY_TYPE, MagicUngathering.MOD_ID);

    // This is the reference for the crystal sword code and crystal staff
    public static final DeferredHolder<EntityType<?>, EntityType<TemporaryCrystal>> TEMP_CRYSTAL =
            ENTITIES.register("temporary_crystal",
                    () -> EntityType.Builder.of(TemporaryCrystal::new, MobCategory.MISC)
                            .sized(2.0f, 2.0f) // Matches standard End Crystal size
                            .build("temporary_crystal"));

    public static final DeferredHolder<EntityType<?>, EntityType<CrystalProjectile>> CRYSTAL_PROJECTILE =
            ENTITIES.register("crystal_projectile",
                    () -> EntityType.Builder.<CrystalProjectile>of(CrystalProjectile::new, MobCategory.MISC)
                            .sized(1.0F, 1.0F) // Set the hit-box size
                            .clientTrackingRange(25) // how far away it stays visible
                            .updateInterval(1) // update every tick
                            .build("crystal_projectile"));

    // This is the reference for the fireball staff
    public static final DeferredHolder<EntityType<?>, EntityType<TemporaryFireball>> TEMP_FIREBALL =
            ENTITIES.register("temporary_fireball",
                    () -> EntityType.Builder.of(TemporaryFireball::new, MobCategory.MISC)
                            .sized(1.0f, 1.0f) // changes fireball size
                            .build("temporary_fireball"));

    public static final DeferredHolder<EntityType<?>, EntityType<FireballProjectile>> FIREBALL_PROJECTILE =
            ENTITIES.register("fireball_projectile",
                    () -> EntityType.Builder.<FireballProjectile>of(FireballProjectile::new, MobCategory.MISC)
                            .sized(1.5F, 1.5F) // Set the hit-box size
                            .clientTrackingRange(50) // how far away it stays visible
                            .updateInterval(1) // update every tick
                            .build("fireball_projectile"));

    public static void register(IEventBus eventBus) {
        ENTITIES.register(eventBus);
    }
}