package net.mp9.magicungathering.item.staff.crystal;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.projectile.ProjectileUtil;

import javax.annotation.Nullable;
import java.util.UUID;

public class CrystalProjectile extends TemporaryCrystal implements OwnableEntity {

    // These variables store the owner's information
    private UUID ownerUUID;
    private int ownerId;

    public CrystalProjectile(EntityType<? extends EndCrystal> type, Level level) {
        super(type, level);
        this.setShowBottom(false); // Removes the bedrock base
        this.noPhysics = true;
    }

    @Override
    public void tick() {
        super.tick();

        // Move the Gravity and Position logic OUTSIDE the isClientSide check
        Vec3 movement = this.getDeltaMovement();
        double gravity = 0.05; // gravity strength (the lower the lighter)
        double drag = 0.99; // air resistance (1 is none)

        double newY = movement.y - gravity;
        Vec3 newMovement = new Vec3(movement.x * drag, newY * drag, movement.z * drag);
        this.setDeltaMovement(newMovement);

        // Update position on BOTH server and client
        this.setPos(this.getX() + newMovement.x, this.getY() + newMovement.y, this.getZ() + newMovement.z);

        if (!this.level().isClientSide) {
            // ONLY keep Collision and Explosion logic inside the Server check
            HitResult hitresult = ProjectileUtil.getHitResultOnMoveVector(this, entity ->
                    !entity.isSpectator() && entity.isPickable() && entity != this.getOwner()
            );

            if (hitresult.getType() != HitResult.Type.MISS) {
                this.onHit(hitresult);
                return;
            }

            if (this.tickCount > 60) {
                this.explode();
            }
        } else {
            // Client-side particles
            this.level().addParticle(ParticleTypes.END_ROD, this.getX(), this.getY(), this.getZ(), 0, 0, 0);
        }
    }

    @Override
    public boolean displayFireAnimation() {
        return false;
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double distance) {
        return true;
    }

    protected void onHit(HitResult result) {
        if (result.getType() != HitResult.Type.MISS) {
            this.explode();
        }
    }

    private void explode() {
        this.level().explode(this, this.getX(), this.getY(), this.getZ(), 3.0F, Level.ExplosionInteraction.BLOCK);
        this.discard();
    }

    // Ownership implementation
    public void setOwner(@Nullable Entity entity) {
        if (entity != null) {
            this.ownerUUID = entity.getUUID();
            this.ownerId = entity.getId();
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