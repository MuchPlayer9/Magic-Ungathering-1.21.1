package net.mp9.magicungathering.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

public class SmokeCloudEntity extends Entity {
    private static final EntityDataAccessor<Integer> DATA_DURATION = SynchedEntityData.defineId(SmokeCloudEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_POISONOUS = SynchedEntityData.defineId(SmokeCloudEntity.class, EntityDataSerializers.BOOLEAN);

    private int age = 0;
    private java.util.UUID ownerUUID;

    public SmokeCloudEntity(EntityType<? extends SmokeCloudEntity> type, Level level) {
        super(type, level);
        this.noPhysics = true;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DATA_DURATION, 100);
        builder.define(DATA_POISONOUS, false);
    }

    public void setProperties(int duration, boolean isPoisonous, java.util.UUID ownerUUID) {
        this.entityData.set(DATA_DURATION, duration);
        this.entityData.set(DATA_POISONOUS, isPoisonous);
        this.ownerUUID = ownerUUID;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide) return;

        this.age++;
        if (this.age >= this.entityData.get(DATA_DURATION)) {
            this.discard();
            return;
        }

        AABB smokeArea = this.getBoundingBox().inflate(0.2);
        boolean poisonous = this.entityData.get(DATA_POISONOUS);

        this.level().getEntitiesOfClass(LivingEntity.class, smokeArea).forEach(entity -> {

            if (this.ownerUUID != null && entity.getUUID().equals(ownerUUID)) {
                return;
            }

            entity.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 50, 0, false, false));

            if (poisonous) {
                // checks every tick to prevent flickering and instant damaging
                entity.addEffect(new MobEffectInstance(MobEffects.POISON, 60, 0, false, false));
            }
        });
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        this.age = tag.getInt("Age");
        java.util.UUID savedUUID = tag.hasUUID("OwnerUUID") ? tag.getUUID("OwnerUUID") : null;

        setProperties(tag.getInt("Duration"), tag.getBoolean("Poisonous"), savedUUID);
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putInt("Age", this.age);
        tag.putInt("Duration", this.entityData.get(DATA_DURATION));
        tag.putBoolean("Poisonous", this.entityData.get(DATA_POISONOUS));

        if (this.ownerUUID != null) {
            tag.putUUID("OwnerUUID", this.ownerUUID);
        }
    }
}