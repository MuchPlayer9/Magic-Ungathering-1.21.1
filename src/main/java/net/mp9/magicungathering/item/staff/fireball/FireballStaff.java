package net.mp9.magicungathering.item.staff.fireball;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.mp9.magicungathering.entity.ModEntities;
import net.mp9.magicungathering.mana.ManaAttachment;
import net.mp9.magicungathering.mana.ManaData;

import java.util.List;

public class FireballStaff extends Item {
    public FireballStaff() {
        super(new Item.Properties().stacksTo(1));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        float currentMana = player.getData(ManaAttachment.MANA).currentMana();
        float manaCost = 60.0f;

        List<TemporaryFireball> balls = level.getEntitiesOfClass(TemporaryFireball.class,
                player.getBoundingBox().inflate(20.0D),
                ball -> ball.getOwner() == player);

        if (!balls.isEmpty()) {
            TemporaryFireball existingBall = balls.get(0);
            if (!level.isClientSide()) {
                Vec3 look = player.getLookAngle();
                double speed = 1.8;

                FireballProjectile projectile = new FireballProjectile(ModEntities.FIREBALL_PROJECTILE.get(), level);
                projectile.setOwner(player);
                projectile.setPos(existingBall.getX(), existingBall.getY(), existingBall.getZ());
                projectile.setDeltaMovement(look.x * speed, look.y * speed, look.z * speed);

                level.addFreshEntity(projectile);
                existingBall.discard();

                // PHYSICS RELEASE
                player.setNoGravity(false);
                player.hurtMarked = true;

                level.playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.FIRECHARGE_USE, SoundSource.PLAYERS, 1.5F, 1.0F);
            }
            return InteractionResultHolder.sidedSuccess(itemstack, level.isClientSide());

        } else if (currentMana >= manaCost) {
            if (!level.isClientSide()) {
                player.setData(ManaAttachment.MANA, new ManaData(currentMana - manaCost));

                TemporaryFireball stationaryBall = new TemporaryFireball(ModEntities.TEMP_FIREBALL.get(), level);
                stationaryBall.setOwner(player);
                stationaryBall.setPos(player.getX(), player.getY() + 2.5, player.getZ());
                level.addFreshEntity(stationaryBall);

                level.playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.FIRE_AMBIENT, SoundSource.PLAYERS, 1.0F, 1.0F);
            }
            return InteractionResultHolder.sidedSuccess(itemstack, level.isClientSide());
        }

        return InteractionResultHolder.fail(itemstack);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (entity instanceof Player player) {
            List<TemporaryFireball> balls = level.getEntitiesOfClass(TemporaryFireball.class,
                    player.getBoundingBox().inflate(30.0D),
                    ball -> ball.getOwner() == player);

            if (!balls.isEmpty()) {
                // 1. BACKLASH (Explosion)
                if (player.hurtTime > 0) {
                    if (!level.isClientSide()) {
                        FireballProjectile backlash = new FireballProjectile(ModEntities.FIREBALL_PROJECTILE.get(), level);
                        backlash.setPos(player.getX(), player.getY() + 1.0, player.getZ());
                        level.addFreshEntity(backlash);
                        backlash.triggerExplosion();
                        balls.forEach(Entity::discard);
                    }
                    player.setNoGravity(false);
                    return;
                }

                // 2. THE TOTAL FREEZE
                // Set velocity to zero to stop sliding
                player.setDeltaMovement(Vec3.ZERO);
                player.setNoGravity(true);

                // This is the key to preventing "side-to-side" movement:
                // We reset the player's position to where they were the previous tick.
                if (!level.isClientSide) {
                    // On server, we teleport them to their exact coordinates
                    player.teleportTo(player.getX(), player.getY(), player.getZ());
                } else {
                    // On client, we overwrite the movement simulation
                    player.setPos(player.getX(), player.getY(), player.getZ());
                }

                // 3. PRESERVE FALL DAMAGE
                // By NOT calling resetFallDistance(), the player keeps their built-up distance.
                // However, we prevent it from growing while they are suspended.
                if (player.fallDistance > 0) {
                    player.fallDistance = player.fallDistance; // Keeps the value static
                }

                player.hurtMarked = true;
            } else {
                // RESET PHYSICS
                if (player.isNoGravity() && !player.isCreative() && !player.isSpectator()) {
                    player.setNoGravity(false);
                    player.hurtMarked = true;
                }
            }
        }
    }
}