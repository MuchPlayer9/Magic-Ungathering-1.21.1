package net.mp9.magicungathering.item.sword;

import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.mp9.magicungathering.mana.ManaAttachment;
import net.mp9.magicungathering.mana.ManaData;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class FlashStepSword extends SwordItem {

    public FlashStepSword() {
        super(Tiers.IRON, new Properties().stacksTo(1));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (player.getCooldowns().isOnCooldown(this)) {
            return InteractionResultHolder.fail(stack);
        }

        ManaData mana = player.getData(ManaAttachment.MANA.get());
        int cost = 100;

        if (mana.currentMana() >= cost) {
            if (!level.isClientSide()) {
                Entity target = findTarget(player, level);

                if (target != null) {
                    Vec3 destination = getSafePositionBehind(target, level);

                    // save current vertical angle (pitch)
                    float originalPitch = player.getXRot();

                    float targetYaw = target.getYRot();

                    // Perform the Teleport
                    player.teleportTo(
                            (ServerLevel) level,
                            destination.x,
                            destination.y,
                            destination.z,
                            Collections.emptySet(),
                            targetYaw,
                            originalPitch
                    );

                    player.setYHeadRot(targetYaw);


                    // Mana & Cooldown
                    player.setData(ManaAttachment.MANA.get(), mana.consume(cost));
                    player.getCooldowns().addCooldown(this, 40);

                    // Effects
                    level.playSound(null, player.getX(), player.getY(), player.getZ(),
                            SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0f, 1.2f);
                    ((ServerLevel)level).sendParticles(ParticleTypes.REVERSE_PORTAL,
                            player.getX(), player.getY() + 1.0, player.getZ(), 20, 0.2, 0.2, 0.2, 0.1);

                    return InteractionResultHolder.success(stack);
                } else {
                    player.displayClientMessage(Component.literal("No target found!")
                            .withStyle(ChatFormatting.RED), true);
                    return InteractionResultHolder.fail(stack);
                }
            }
            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
        }
        return InteractionResultHolder.pass(stack);
    }

    private Entity findTarget(Player player, Level level) {
        double mobRange = 20.0;
        double playerRange = 8.0;

        // Find all living entities in a large box around the player
        AABB searchBox = player.getBoundingBox().inflate(mobRange);
        List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, searchBox, e -> e != player);

        return entities.stream()
                .filter(e -> {
                    double dist = player.distanceTo(e);
                    if (e instanceof Player) return dist <= playerRange;
                    if (e instanceof Enemy) return dist <= mobRange;
                    return dist <= 12.0; // Default range for neutrals
                })
                .min(Comparator.comparingDouble(player::distanceTo))
                .orElse(null);
    }

    private Vec3 getSafePositionBehind(Entity target, Level level) {
        Vec3 look = target.getLookAngle();
        // We exclude Y to avoid teleporting into the floor/ceiling
        Vec3 behindOffset = new Vec3(look.x, 0, look.z).normalize().scale(-1.5);
        Vec3 targetPos = target.position();
        Vec3 wantedPos = targetPos.add(behindOffset);

        // Raytrace from target to the "wanted" position to check for walls

        BlockHitResult hitResult = level.clip(new ClipContext(
                targetPos,
                wantedPos,
                ClipContext.Block.COLLIDER,
                ClipContext.Fluid.NONE,
                target
        ));

        if (hitResult.getType() == HitResult.Type.BLOCK) {
            // If we hit a wall, move the destination slightly in front of the wall
            Vec3 hitVec = hitResult.getLocation();
            Vec3 toTarget = targetPos.subtract(hitVec).normalize().scale(0.5);
            return hitVec.add(toTarget);
        }

        return wantedPos;
    }
}