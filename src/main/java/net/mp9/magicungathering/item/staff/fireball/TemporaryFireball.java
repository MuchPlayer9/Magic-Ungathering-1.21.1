package net.mp9.magicungathering.item.staff.fireball;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.Fireball;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.UUID;

public class TemporaryFireball extends Fireball {

    public TemporaryFireball(EntityType<? extends Fireball> type, Level level) {
        super(type, level);
        // Setting these to 0 prevents the ghast-like acceleration
        this.accelerationPower = 0;
        // ensure it doesn't start with any velocity
        this.setDeltaMovement(Vec3.ZERO);
    }

    // Add this to fulfill the OwnableEntity requirement for the staff's search logic
    private UUID ownerUUID;

    public void setOwner(@Nullable Entity entity) {
        if (entity != null) this.ownerUUID = entity.getUUID();
    }

    @Override
    protected boolean shouldBurn() {
        // Returning false prevents the fireball from trying to calculate
        // acceleration logic that causes the "Zero Vector" crash.
        return false;
    }

    @Override
    public ItemStack getItem() {
        // This is what the BigFireballRenderer will actually draw
        return new ItemStack(Items.FIRE_CHARGE);
    }

    @Nullable
    @Override
    public Entity getOwner() {
        if (this.ownerUUID != null && this.level() instanceof ServerLevel serverLevel) {
            return serverLevel.getEntity(this.ownerUUID);
        }
        return null;
    }
}