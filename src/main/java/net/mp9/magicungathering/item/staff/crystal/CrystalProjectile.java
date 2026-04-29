package net.mp9.magicungathering.item.staff.crystal;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.mp9.magicungathering.ModDamageTypes;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import javax.annotation.Nullable;

public class CrystalProjectile extends TemporaryCrystal implements ItemSupplier {

    @Override
    public ItemStack getItem() {
        return new ItemStack(Items.END_CRYSTAL);
    }

    @Nullable
    private Entity owner;

    public CrystalProjectile(EntityType<? extends net.minecraft.world.entity.boss.enderdragon.EndCrystal> type, Level level) {
        super(type, level);
        this.setShowBottom(false);
        this.noPhysics = true;
    }

    public void setOwner(@Nullable Entity owner) {
        this.owner = owner;
    }

    @Nullable
    public Entity getOwner() {
        return this.owner;
    }

    @Override
    public void tick() {
        super.tick();

        Vec3 movement = this.getDeltaMovement();
        double gravity = 0.05;
        double drag = 0.99;
        Vec3 nextMovement = new Vec3(movement.x * drag, (movement.y - gravity) * drag, movement.z * drag);
        this.setDeltaMovement(nextMovement);

        if (!this.level().isClientSide) {
            HitResult hitresult = ProjectileUtil.getHitResultOnMoveVector(this, entity ->
                    !entity.isSpectator() && entity.isPickable()
            );

            if (hitresult.getType() != HitResult.Type.MISS) {
                this.setPos(hitresult.getLocation());
                this.explode();
                return;
            }

            if (this.level().getBlockState(this.blockPosition()).isSolid()) {
                this.explode();
                return;
            }

            if (this.tickCount > 60) this.explode();
        } else {
            this.level().addParticle(ParticleTypes.END_ROD, this.getX(), this.getY(), this.getZ(), 0, 0, 0);
        }

        this.setPos(this.getX() + nextMovement.x, this.getY() + nextMovement.y, this.getZ() + nextMovement.z);
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
        if (!this.level().isClientSide) {
            Vec3 blastPos = this.position().add(0, 0.5, 0);
            if (owner != null) {
                Vec3 toOwner = owner.getEyePosition().subtract(this.position()).normalize();
                blastPos = blastPos.add(toOwner.scale(0.5));
            }

            DamageSource magicSource = this.level().damageSources().source(ModDamageTypes.SPELL, this.getOwner());

            this.level().explode(
                    this,
                    magicSource,
                    null,
                    blastPos.x, blastPos.y, blastPos.z,
                    6.0F,
                    false,
                    Level.ExplosionInteraction.BLOCK
            );

            this.discard();
        }
    }
}