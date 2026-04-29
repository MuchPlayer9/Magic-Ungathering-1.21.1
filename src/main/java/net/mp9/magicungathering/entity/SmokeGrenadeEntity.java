package net.mp9.magicungathering.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.server.TickTask;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.mp9.magicungathering.attachment.SkillData;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class SmokeGrenadeEntity extends ThrowableItemProjectile implements ItemSupplier {
    private int fuse = 60;
    private SkillData skillData;

    public SmokeGrenadeEntity(EntityType<? extends ThrowableItemProjectile> type, Level level) {
        super(type, level);
    }

    @Override
    public ItemStack getItem() {
        return new ItemStack(Items.FIREWORK_STAR);
    }

    public void setSkillData(SkillData data) {
        this.skillData = data;
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide) {
            fuse--;
            if (fuse <= 0) explode();
        }
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (!this.level().isClientSide) explode();
    }

    private void explode() {
        if (this.level().isClientSide) return;

        SmokeCloudEntity cloud = new SmokeCloudEntity(ModEntities.SMOKE_CLOUD.get(), this.level());

        // Snaps the Y coordinate to the top of the block the grenade landed on
        cloud.setPos(this.getX(), Math.floor(this.getY()), this.getZ());

        java.util.UUID ownerId = this.getOwner() != null ? this.getOwner().getUUID() : null;

        int duration = 100;
        boolean poison = false;

        if (this.skillData != null) {
            if (this.skillData.hasSkill("upgrade_smoke_screen_length")) duration = 160;
            if (this.skillData.hasSkill("upgrade_smoke_screen_poison")) poison = true;
        }

        cloud.setProperties(duration, poison, ownerId);
        this.level().addFreshEntity(cloud);
        this.discard();
    }

    @Override
    protected Item getDefaultItem() {
        return Items.FIREWORK_STAR;
    }
}