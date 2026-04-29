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

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = ENTITIES;

    // --- SMOKE SCREEN ---
    public static final DeferredHolder<EntityType<?>, EntityType<SmokeGrenadeEntity>> SMOKE_GRENADE =
            ENTITIES.register("smoke_grenade",
                    () -> EntityType.Builder.<SmokeGrenadeEntity>of(SmokeGrenadeEntity::new, MobCategory.MISC)
                            .sized(0.25F, 0.25F)
                            .clientTrackingRange(4)
                            .updateInterval(10)
                            .build("smoke_grenade"));

    // --- CRYSTAL STAFF & SWORD ---
    public static final DeferredHolder<EntityType<?>, EntityType<TemporaryCrystal>> TEMP_CRYSTAL =
            ENTITIES.register("temporary_crystal",
                    () -> EntityType.Builder.of(TemporaryCrystal::new, MobCategory.MISC)
                            .sized(2.0f, 2.0f)
                            .build("temporary_crystal"));

    public static final DeferredHolder<EntityType<?>, EntityType<CrystalProjectile>> CRYSTAL_PROJECTILE =
            ENTITIES.register("crystal_projectile",
                    () -> EntityType.Builder.<CrystalProjectile>of(CrystalProjectile::new, MobCategory.MISC)
                            .sized(1.0F, 1.0F)
                            .clientTrackingRange(25)
                            .updateInterval(1)
                            .build("crystal_projectile"));

    // --- FIREBALL STAFF ---
    public static final DeferredHolder<EntityType<?>, EntityType<TemporaryFireball>> TEMP_FIREBALL =
            ENTITIES.register("temporary_fireball",
                    () -> EntityType.Builder.of(TemporaryFireball::new, MobCategory.MISC)
                            .sized(1.0f, 1.0f)
                            .build("temporary_fireball"));

    public static final DeferredHolder<EntityType<?>, EntityType<FireballProjectile>> FIREBALL_PROJECTILE =
            ENTITIES.register("fireball_projectile",
                    () -> EntityType.Builder.<FireballProjectile>of(FireballProjectile::new, MobCategory.MISC)
                            .sized(1.5F, 1.5F)
                            .clientTrackingRange(50)
                            .updateInterval(1)
                            .build("fireball_projectile"));

    public static final DeferredHolder<EntityType<?>, EntityType<SmokeCloudEntity>> SMOKE_CLOUD =
            ENTITIES.register("smoke_cloud", () -> EntityType.Builder.of(SmokeCloudEntity::new, MobCategory.MISC)
                    .sized(5.0f, 5.0f) // size of smoke cloud
                    .build("smoke_cloud"));

    public static void register(IEventBus eventBus) {
        ENTITIES.register(eventBus);
    }
}