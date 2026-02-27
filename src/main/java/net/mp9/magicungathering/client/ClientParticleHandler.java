package net.mp9.magicungathering.client.event;

import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.level.Level;
import net.mp9.magicungathering.MagicUngathering;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

import java.util.Random;

// bus = GAME is used for events that happen during gameplay (like rendering)
// value = Dist.CLIENT prevents the server from trying to load this class
@EventBusSubscriber(modid = MagicUngathering.MOD_ID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class ClientParticleHandler {

    private static final Random RANDOM = new Random();

    @SubscribeEvent
    public static void onLevelRender(RenderLevelStageEvent event) {
        // We only want to run this once per frame, after particles are drawn
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_PARTICLES) return;

        Level level = Minecraft.getInstance().level;
        if (level == null) return;

        // Find clouds near the player
        for (AreaEffectCloud cloud : level.getEntitiesOfClass(AreaEffectCloud.class,
                Minecraft.getInstance().player.getBoundingBox().inflate(64))) {

            // This is our 'ID card' for the fireball cloud.
            // If it has a 10.0 radius, we draw our 3D sphere.
            if (cloud.getRadius() >= 9.9F) {
                spawnFireSphere(level, cloud);
            }
        }
    }

    private static void spawnFireSphere(Level level, AreaEffectCloud cloud) {
        float r_max = cloud.getRadius();

        // Increase this number (e.g., 80) if the ball looks too thin
        for (int i = 0; i < 60; i++) {
            double theta = RANDOM.nextDouble() * 2 * Math.PI;
            double phi = Math.acos(2 * RANDOM.nextDouble() - 1);

            // Cubed root ensures the ball is filled evenly, not just a clump in the middle
            double r = Math.pow(RANDOM.nextDouble(), 1.0/3.0) * r_max;

            double x = r * Math.sin(phi) * Math.cos(theta);
            double y = r * Math.sin(phi) * Math.sin(theta);
            double z = r * Math.cos(phi);

            level.addParticle(ParticleTypes.FLAME,
                    cloud.getX() + x, cloud.getY() + y, cloud.getZ() + z,
                    0, 0, 0);
        }
    }
}