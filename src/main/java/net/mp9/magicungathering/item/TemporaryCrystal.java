package net.mp9.magicungathering.entity;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.level.Level;

public class TemporaryCrystal extends EndCrystal {

    public TemporaryCrystal(EntityType<? extends EndCrystal> type, Level level) {
        super(type, level);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        // Prevent explosion if the crystal is younger than 10 ticks (0.5s)
        if (this.tickCount < 6) {
            return false;
        }
        return super.hurt(source, amount);
    }

    @Override
    public void tick() {
        super.tick();

        // This makes the 'setInvulnerable' status follow the tick logic
        if (this.tickCount < 6) {
            this.setInvulnerable(true);
        } else if (this.tickCount == 6) {
            this.setInvulnerable(false);
        }
    }
}