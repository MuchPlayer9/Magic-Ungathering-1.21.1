package net.mp9.magicungathering.item.staff.fireball;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.projectile.ProjectileUtil;

import javax.annotation.Nullable;
import java.util.UUID;

public class FireballProjectile extends TemporaryFireball implements OwnableEntity, ItemSupplier {

    @Override
    public ItemStack getItem() {
        return new ItemStack(Items.FIRE_CHARGE);
    }

    private UUID ownerUUID;

    public FireballProjectile(EntityType<? extends FireballProjectile> type, Level level) {
        super(type, level);
        this.accelerationPower = 0;
    }

    @Override
    public void tick() {
        Vec3 movement = this.getDeltaMovement();
        this.setPos(this.getX() + movement.x, this.getY() + movement.y, this.getZ() + movement.z);

        if (this.level().isClientSide) {
            this.level().addParticle(ParticleTypes.LARGE_SMOKE, this.getX(), this.getY(), this.getZ(), 0, 0, 0);
            this.level().addParticle(ParticleTypes.FLAME, this.getX(), this.getY(), this.getZ(), 0, 0, 0);
        } else {
            HitResult hitresult = ProjectileUtil.getHitResultOnMoveVector(this, entity ->
                    !entity.isSpectator() && entity.isPickable() && entity != this.getOwner()
            );

            if (hitresult.getType() != HitResult.Type.MISS) {
                this.onHit(hitresult);
                return;
            }

            if (this.tickCount > 200) {
                this.triggerExplosion();
            }
        }
    }

    protected void onHit(HitResult result) {
        if (result.getType() != HitResult.Type.MISS) {
            this.triggerExplosion();
        }
    }

    public void triggerExplosion() {
        if (!this.level().isClientSide) {
            ServerLevel serverLevel = (ServerLevel) this.level();
            Vec3 center = this.position();

            serverLevel.sendParticles(ParticleTypes.FLASH,
                    center.x, center.y, center.z,
                    1, 0, 0, 0, 0);

            // 1. HIGH-DENSITY "CONTAINED" BURST
            // second number is particle count
            for (int i = 0; i < 1200; i++) {
                double theta = this.random.nextDouble() * 2 * Math.PI;
                double phi = Math.acos(2 * this.random.nextDouble() - 1);

                // SPEED of particles
                double speed = 0.35 + this.random.nextDouble() * 0.35;

                double vx = speed * Math.sin(phi) * Math.cos(theta);
                double vy = speed * Math.sin(phi) * Math.sin(theta);
                double vz = speed * Math.cos(phi);

                // Using count 0 so vx/vy/vz act as direction + velocity
                serverLevel.sendParticles(ParticleTypes.FLAME,
                        center.x, center.y, center.z,
                        0, vx, vy, vz, 1.0);
            }

            // 2. IMMEDIATE RADIUS DAMAGE
            // Finds entities in a radius and burns them once
            this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(10.0D)).forEach(entity -> {
                if (entity != this.getOwner() && entity.position().distanceTo(center) <= 7.0) {
                    entity.igniteForSeconds(5.0F);
                    entity.hurt(this.damageSources().onFire(), 10.0F);
                }
            });

            // 3. EXPLOSION SOUND
            this.level().playSound(null, center.x, center.y, center.z,
                    SoundEvents.FIREWORK_ROCKET_LARGE_BLAST, SoundSource.BLOCKS, 4.0F, 0.7F);

            this.discard();
        }
    }

    public void setOwner(@Nullable Entity entity) {
        if (entity != null) {
            this.ownerUUID = entity.getUUID();
        }
    }

    @Nullable
    @Override
    public LivingEntity getOwner() {
        if (this.ownerUUID != null && this.level() instanceof ServerLevel serverLevel) {
            Entity entity = serverLevel.getEntity(this.ownerUUID);
            if (entity instanceof LivingEntity living) {
                return living;
            }
        }
        return null;
    }

    @Nullable
    @Override
    public UUID getOwnerUUID() {
        return this.ownerUUID;
    }
}