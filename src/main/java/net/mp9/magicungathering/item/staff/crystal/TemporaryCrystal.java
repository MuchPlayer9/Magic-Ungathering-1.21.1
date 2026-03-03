package net.mp9.magicungathering.item.staff.crystal;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.level.Level;
import net.mp9.magicungathering.ModDamageTypes;

public class TemporaryCrystal extends EndCrystal {

    private static final float EXPLOSION_POWER = 6.0F;

    public TemporaryCrystal(EntityType<? extends EndCrystal> type, Level level) {
        super(type, level);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (this.tickCount < 6 || this.isRemoved()) {
            return false;
        }

        if (!this.level().isClientSide()) {
            this.discard();

            // We pass 'null' as the causer. This makes the explosion "wild."
            // It will hurt the summoner just as much as an enemy.
            DamageSource magicSource = ModDamageTypes.source(this.level(), ModDamageTypes.SPELL, null);

            this.level().explode(
                    this,
                    magicSource,
                    null,
                    this.getX(), this.getY(), this.getZ(),
                    EXPLOSION_POWER,
                    false,
                    Level.ExplosionInteraction.BLOCK
            );
        }
        return true;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.tickCount < 6) {
            this.setInvulnerable(true);
        } else if (this.tickCount == 6) {
            this.setInvulnerable(false);
        }
    }
}