package net.mp9.magicungathering.item.staff.fireball;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class FireBallCloud extends AreaEffectCloud {

    public FireBallCloud(Level level, double x, double y, double z) {
        super(level, x, y, z);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide) {
            float r_max = this.getRadius();
            for (int i = 0; i < 100; i++) { // High density
                double theta = this.random.nextDouble() * 2 * Math.PI;
                double phi = Math.acos(2 * this.random.nextDouble() - 1);
                double r = Math.pow(this.random.nextDouble(), 1.0/3.0) * r_max;

                double x = r * Math.sin(phi) * Math.cos(theta);
                double y = r * Math.sin(phi) * Math.sin(theta);
                double z = r * Math.cos(phi);

                this.level().addParticle(ParticleTypes.FLAME, this.getX() + x, this.getY() + y, this.getZ() + z, 0, 0, 0);
            }
        }
    }
}