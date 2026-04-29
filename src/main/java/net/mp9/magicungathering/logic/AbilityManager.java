package net.mp9.magicungathering.logic;

import net.minecraft.ChatFormatting;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.mp9.magicungathering.MagicUngathering;
import net.mp9.magicungathering.ModEffects;
import net.mp9.magicungathering.attachment.MarkData;
import net.mp9.magicungathering.attachment.SkillData;
import net.mp9.magicungathering.attributes.ModAttributes;
import net.mp9.magicungathering.entity.ModEntities;
import net.mp9.magicungathering.entity.SmokeGrenadeEntity;
import net.mp9.magicungathering.mana.ManaAttachment;
import net.mp9.magicungathering.mana.ManaData;

import java.util.List;

public class AbilityManager {

    public static void tryActivate(ServerPlayer player, String abilityId) {
        SkillData data = player.getData(MagicUngathering.SKILL_DATA);
        String currentClass = data.getCurrentClass() != null ? data.getCurrentClass() : "Assassin";

        if (abilityId.equals("movement_skill")) {
            handleMovement(player, currentClass, data);
        } else if (abilityId.startsWith("ability_")) {
            handleActions(player, currentClass, abilityId, data);
        }
    }

    private static void handleMovement(ServerPlayer player, String className, SkillData data) {
        switch (className) {
            case "Assassin" -> { if (data.hasSkill("assassin_blink")) executeBlink(player, data); }
            case "Warrior" -> { if (data.hasSkill("warrior_dash")) executeDash(player); }
        }
    }

    private static void handleActions(ServerPlayer player, String className, String slot, SkillData data) {
        // Filter for active skills only, excluding movement, upgrades, and passive stat nodes
        List<String> activeAbilities = data.unlockedSkills().stream()
                .filter(id -> !id.equals("assassin_blink") && !id.equals("warrior_dash"))
                .filter(id -> !id.matches(".*\\d.*"))
                .filter(id -> !id.startsWith("upgrade_") && !id.contains("_upgrade"))
                .filter(id -> !id.contains("_speed") && !id.contains("_armor") &&
                        !id.contains("_damage") && !id.contains("_heart") &&
                        !id.contains("_mana") && !id.contains("_regen"))
                .toList();

        // debug
        System.out.println("[DEBUG] Active Abilities Unlocked: " + activeAbilities);

        int index = switch (slot) {
            case "ability_1" -> 0;
            case "ability_2" -> 1;
            case "ability_3" -> 2;
            default -> -1;
        };

        if (index >= 0 && index < activeAbilities.size()) {
            executeAbilityLogic(player, activeAbilities.get(index), data);
        }
    }

    private static void executeAbilityLogic(ServerPlayer player, String skillId, SkillData data) {
        switch (skillId) {
            case "assassin_mark" -> executeMark(player, data);
            case "assassin_execute" -> executeExecute(player, data);
            case "smoke_screen" -> executeSmokeScreen(player, data);
            case "warrior_slam" -> executeSlam(player);
            case "assassin_blink" -> executeBlink(player, data);
            case "assassin_haemorrhage" -> executeHaemorrhage(player, data);
            case "assassin_vanish" -> executeVanish(player, data);
            case "shadow_step" -> executeShadowStep(player, data);
        }
    }

    // --- ATTRIBUTE SYSTEM ---

    public static void applyAttributeModifiers(ServerPlayer player, SkillData data) {
        // SPEED (+5%)
        applyStackableModifier(player, Attributes.MOVEMENT_SPEED, 0.05, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL,
                List.of("assassin_1speed5", "assassin_2speed5", "assassin_1speed5armor1", "assassin_heart1speed5", "assassin_2speed5armor1"), data);

        // ARMOR (+1)
        applyStackableModifier(player, Attributes.ARMOR, 1.0, AttributeModifier.Operation.ADD_VALUE,
                List.of("assassin_1armor1", "assassin_2armor1"), data);

        // ARMOR (-1 Penalty)
        applyStackableModifier(player, Attributes.ARMOR, -1.0, AttributeModifier.Operation.ADD_VALUE,
                List.of("assassin_1speed5armor1", "assassin_2speed5armor1"), data);

        // DAMAGE (+5% and +15%)
        applyStackableModifier(player, Attributes.ATTACK_DAMAGE, 0.05, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL,
                List.of("assassin_1damage5"), data);
        applyStackableModifier(player, Attributes.ATTACK_DAMAGE, 0.15, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL,
                List.of("assassin_damage15heart1"), data);

        // MAX HEALTH (-2 per heart penalty)
        applyStackableModifier(player, Attributes.MAX_HEALTH, -2.0, AttributeModifier.Operation.ADD_VALUE,
                List.of("assassin_heart1speed5", "assassin_damage15heart1", "assassin_maxmana15heart1"), data);

        // MANA REGEN
        applyStackableModifier(player, ModAttributes.MANA_REGEN, 0.2, AttributeModifier.Operation.ADD_VALUE,
                List.of("assassin_manaregen2"), data);
        applyStackableModifier(player, ModAttributes.MANA_REGEN, 5.0, AttributeModifier.Operation.ADD_VALUE,
                List.of("assassin_manaregen5"), data);

        // MAX MANA
        applyStackableModifier(player, ModAttributes.MAX_MANA, 5.0, AttributeModifier.Operation.ADD_VALUE,
                List.of("assassin_1maxmana5", "assassin_2maxmana5"), data);
        applyStackableModifier(player, ModAttributes.MAX_MANA, 10.0, AttributeModifier.Operation.ADD_VALUE,
                List.of("assassin_maxmana10"), data);
        applyStackableModifier(player, ModAttributes.MAX_MANA, -15.0, AttributeModifier.Operation.ADD_VALUE,
                List.of("assassin_maxmana15heart1"), data);
    }

    private static void applyStackableModifier(ServerPlayer player, Holder<Attribute> attribute, double amount,
                                               AttributeModifier.Operation op, List<String> skillIds, SkillData data) {
        var instance = player.getAttribute(attribute);
        if (instance == null) return;

        for (String id : skillIds) {
            ResourceLocation modifierId = ModAttributes.skillId(id);
            instance.removeModifier(modifierId);
            if (data.hasSkill(id)) {
                instance.addPermanentModifier(new AttributeModifier(modifierId, amount, op));
            }
        }
    }

    // --- ACTIVE SKILL LOGIC ---

    private static void executeBlink(ServerPlayer player, SkillData data) {
        String abilityId = "blink";
        int cost = 15;
        int cooldownTicks = 20;

        // cooldown check
        long currentTime = player.level().getGameTime();
        long lastUsed = data.getCooldown(abilityId);

        if (currentTime < lastUsed + cooldownTicks) {
            long remaining = (lastUsed + cooldownTicks - currentTime + 19) / 20;
            player.displayClientMessage(Component.literal("Wait " + remaining + "s").withStyle(ChatFormatting.RED), true);
            return;
        }

        // mana logic
        ManaData mana = player.getData(ManaAttachment.MANA.get());
        if (mana.currentMana() >= cost) {

            ManaData newMana = mana.consume(cost);

            player.setData(ManaAttachment.MANA.get(), newMana);

            player.displayClientMessage(Component.literal("Blink: -15 mana").withStyle(ChatFormatting.RED), true);


            SkillData updatedData = data.setCooldown("blink", currentTime);
            player.setData(MagicUngathering.SKILL_DATA, updatedData);

            double range = data.hasSkill("upgrade_blink_range") ? 12.0 : 8.0;
            Vec3 look = player.getLookAngle();
            player.teleportTo(player.getX() + look.x * range, player.getY(), player.getZ() + look.z * range);
            player.level().playSound(null, player.blockPosition(), SoundEvents.PLAYER_TELEPORT, SoundSource.PLAYERS, 1.0f, 1.2f);

    } else {

        player.displayClientMessage(Component.literal("Not enough mana!").withStyle(ChatFormatting.BLUE), true);

    }}

    private static void executeMark(ServerPlayer player, SkillData data) {
        String abilityId = "mark";
        double range = 40.0;
        int amplifier = data.hasSkill("upgrade_mark_reduced_cost") ? 5 : 10;
        int cost = amplifier;
        int cooldownTicks = 10;

        // TARGET SEARCH
        Vec3 eyePos = player.getEyePosition();
        Vec3 lookVec = player.getLookAngle();
        Vec3 traceVec = eyePos.add(lookVec.x * range, lookVec.y * range, lookVec.z * range);
        AABB searchBox = player.getBoundingBox().expandTowards(lookVec.scale(range)).inflate(1.0);

        LivingEntity target = null;
        double closestDistance = range;

        for (Entity entity : player.level().getEntities(player, searchBox, e -> e instanceof LivingEntity)) {
            AABB entityBox = entity.getBoundingBox().inflate(0.5);
            if (entityBox.clip(eyePos, traceVec).isPresent()) {
                double dist = eyePos.distanceTo(entity.position());
                if (dist < closestDistance) {
                    closestDistance = dist;
                    target = (LivingEntity) entity;
                }
            }
        }

        // FAIL-FAST: If no target hit, stop here, no mana cost.
        if (target == null) {
            player.displayClientMessage(Component.literal("No target reached!").withStyle(ChatFormatting.RED), true);
            return;
        }

        // COOLDOWN CHECK
        long currentTime = player.level().getGameTime();
        long lastUsed = data.getCooldown(abilityId);
        if (currentTime < lastUsed + cooldownTicks) {
            long remaining = (lastUsed + cooldownTicks - currentTime + 19) / 20;
            player.displayClientMessage(Component.literal("Wait " + remaining + "s").withStyle(ChatFormatting.RED), true);
            return;
        }

        // MANA CHECK
        ManaData mana = player.getData(ManaAttachment.MANA.get());
        if (mana.currentMana() < cost) {
            player.displayClientMessage(Component.literal("Not enough mana!").withStyle(ChatFormatting.BLUE), true);
            return;
        }

        // EXECUTION (Target is confirmed, Mana is available, Cooldown is ready)
        // Consume Mana and Set Cooldown
        player.setData(ManaAttachment.MANA.get(), mana.consume(cost));
        player.setData(MagicUngathering.SKILL_DATA, data.setCooldown(abilityId, currentTime));

        // Calculate Duration
        int durationTicks = 200;
        if (data.hasSkill("upgrade_mark_increased_time")) {
            durationTicks += 60;
        }

        // Apply Mark
        target.addEffect(new MobEffectInstance(MobEffects.GLOWING, durationTicks, 0));
        target.setData(MagicUngathering.MARK_DATA, new MarkData(player.getUUID()));

        player.displayClientMessage(Component.literal("Mark: -" + cost + " mana").withStyle(ChatFormatting.RED), true);


        // this logic tells the player what enemy was marked. not really needed but could be interesting? maybe a gui with heads?
        // player.displayClientMessage(Component.literal("Target Marked: " + target.getName().getString()), true);

        player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 1.0f, 0.5f);
    }

    private static void executeExecute(ServerPlayer player, SkillData data) {

        int cost = 50;
        int cooldownTicks = 20;
        long currentTime = player.level().getGameTime();
        long lastUsed = data.getCooldown("execute");
        ManaData mana = player.getData(ManaAttachment.MANA.get());

        if (currentTime < lastUsed + cooldownTicks) {
            long remaining = (lastUsed + cooldownTicks - currentTime + 19) / 20;
            player.displayClientMessage(Component.literal("Wait " + remaining + "s").withStyle(ChatFormatting.RED), true);
            return;
        }

        int amplifier = data.hasSkill("upgrade_execute_level") ? 2 : 1;
        int durationTicks = 40;

        if (data.hasSkill("upgrade_execute_time")) durationTicks += 30; // +1.5s
        if (data.hasSkill("upgrade_execute_time2")) durationTicks += 50; // +2.5s

        if (mana.currentMana() >= cost) {

            ManaData newMana = mana.consume(cost);

            player.setData(ManaAttachment.MANA.get(), newMana);

            player.displayClientMessage(Component.literal("Execute: -50 mana").withStyle(ChatFormatting.RED), true);

            SkillData updatedData = data.setCooldown("execute", currentTime);
            player.setData(MagicUngathering.SKILL_DATA, updatedData);

            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, durationTicks, amplifier));
            player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.IRON_GOLEM_ATTACK, SoundSource.PLAYERS, 1.0f, 1.5f);
        } else {

            player.displayClientMessage(Component.literal("Not enough mana!").withStyle(ChatFormatting.BLUE), true);

        }
    }

    private static void executeSmokeScreen(ServerPlayer player, SkillData data) {

        int cost = 20;
        int cooldownTicks = 20;
        long currentTime = player.level().getGameTime();
        long lastUsed = data.getCooldown("smoke_screen");
        ManaData mana = player.getData(ManaAttachment.MANA.get());

        if (currentTime < lastUsed + cooldownTicks) {
            long remaining = (lastUsed + cooldownTicks - currentTime + 19) / 20;
            player.displayClientMessage(Component.literal("Wait " + remaining + "s").withStyle(ChatFormatting.RED), true);
            return;
        }

        if (mana.currentMana() >= cost) {

            ManaData newMana = mana.consume(cost);

            player.setData(ManaAttachment.MANA.get(), newMana);

            SkillData updatedData = data.setCooldown("smoke_screen", currentTime);

            player.displayClientMessage(Component.literal("Smoke Screen: -20 mana").withStyle(ChatFormatting.RED), true);

            player.setData(MagicUngathering.SKILL_DATA, updatedData);

            Level level = player.level();
            SmokeGrenadeEntity grenade = new SmokeGrenadeEntity(ModEntities.SMOKE_GRENADE.get(), level);

            grenade.setOwner(player);
            grenade.setSkillData(data);

            grenade.setPos(player.getX(), player.getEyeY() - 0.1, player.getZ());
            grenade.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 0.7F, 1.0F);

            level.addFreshEntity(grenade);

            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.SNOWBALL_THROW, SoundSource.PLAYERS, 1.0f, 0.8f);
        } else {

            player.displayClientMessage(Component.literal("Not enough mana!").withStyle(ChatFormatting.BLUE), true);

        }
    }

    private static void executeHaemorrhage(ServerPlayer player, SkillData data) {

        int cost = 25;
        int cooldownTicks = 20;
        long currentTime = player.level().getGameTime();
        long lastUsed = data.getCooldown("haemorrhage");
        ManaData mana = player.getData(ManaAttachment.MANA.get());

        double windowSeconds = 1.0;
        if (data.hasSkill("upgrade_haemorrhage_time")) windowSeconds = 2.5;

        if (data.hasSkill("upgrade_haemorrhage_damage")) {
            windowSeconds = 1.25;
        }

        if (currentTime < lastUsed + cooldownTicks) {
            long remaining = (lastUsed + cooldownTicks - currentTime + 19) / 20;
            player.displayClientMessage(Component.literal("Wait " + remaining + "s").withStyle(ChatFormatting.RED), true);
            return;
        }

        if (mana.currentMana() >= cost) {

            ManaData newMana = mana.consume(cost);

            player.setData(ManaAttachment.MANA.get(), newMana);

            player.displayClientMessage(Component.literal("Haemorrhage: -25 mana").withStyle(ChatFormatting.RED), true);
            SkillData updatedData = data.setCooldown("haemorrhage", currentTime);
            player.setData(MagicUngathering.SKILL_DATA, updatedData);

            player.addEffect(new MobEffectInstance(ModEffects.HAEMORRHAGE_WINDOW, (int) (windowSeconds * 20), 0, false, false, true));

            player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 1.0f, 0.5f);
        } else {

            player.displayClientMessage(Component.literal("Not enough mana!").withStyle(ChatFormatting.BLUE), true);

        }
    }

    private static void executeVanish(ServerPlayer player, SkillData data) {

        int duration = 80; // 4 seconds
        int cost = 40;
        long currentTime = player.level().getGameTime();
        long lastUsed = data.getCooldown("vanish");
        int cooldownTicks = 20;
        ManaData mana = player.getData(ManaAttachment.MANA.get());

        if (currentTime < lastUsed + cooldownTicks) {
            long remaining = (lastUsed + cooldownTicks - currentTime + 19) / 20;
            player.displayClientMessage(Component.literal("Wait " + remaining + "s").withStyle(ChatFormatting.RED), true);
            return;
        }

        if (mana.currentMana() >= cost) {

            ManaData newMana = mana.consume(cost);

            player.setData(ManaAttachment.MANA.get(), newMana);

            SkillData updatedData = data.setCooldown("vanish", currentTime);
            player.setData(MagicUngathering.SKILL_DATA, updatedData);

            // Base Effects
            player.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, duration, 0, false, false));
            player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, duration, 1, false, false)); // Speed 2

            // Upgrade: No Weakness
            if (!data.hasSkill("upgrade_vanish_no_weakness")) {
                player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, duration, 0, false, false));
            }

            player.displayClientMessage(Component.literal("Vanish: -40 mana").withStyle(ChatFormatting.RED), true);

            data.setCooldown("vanish", currentTime);

            // Sound effect for vanishing
            player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.ILLUSIONER_MIRROR_MOVE, SoundSource.PLAYERS, 1.0f, 1.2f);
        } else {

            player.displayClientMessage(Component.literal("Not enough mana!").withStyle(ChatFormatting.BLUE), true);

        }
    }

    private static void executeShadowStep(ServerPlayer player, SkillData data) {
        String abilityId = "shadow_step";
        double searchRadius = 64.0;
        int cost = 30;
        int cooldownTicks = 20;

        long currentTime = player.level().getGameTime();
        long lastUsed = data.getCooldown(abilityId);
        ManaData mana = player.getData(ManaAttachment.MANA.get());

        // Cooldown Check
        if (currentTime < lastUsed + cooldownTicks) {
            long remaining = (lastUsed + cooldownTicks - currentTime + 19) / 20;
            player.displayClientMessage(Component.literal("Wait " + remaining + "s").withStyle(ChatFormatting.RED), true);
            return;
        }

        // Mana Check
        if (mana.currentMana() < cost) {
            player.displayClientMessage(Component.literal("Not enough mana!").withStyle(ChatFormatting.BLUE), true);
            return;
        }

        AABB searchBox = player.getBoundingBox().inflate(searchRadius);
        LivingEntity target = player.level().getEntitiesOfClass(LivingEntity.class, searchBox, entity -> {
                    if (entity == player) return false;
                    if (!entity.hasEffect(MobEffects.GLOWING)) return false;
                    if (entity.hasData(MagicUngathering.MARK_DATA)) {
                        MarkData mark = entity.getData(MagicUngathering.MARK_DATA);
                        return mark.markerId() != null && mark.markerId().equals(player.getUUID());
                    }
                    return false;
                }).stream()
                .min(java.util.Comparator.comparingDouble(e -> e.distanceToSqr(player)))
                .orElse(null);

        if (target != null) {
            // Execution: Deduct Mana and Set Cooldown
            player.setData(ManaAttachment.MANA.get(), mana.consume(cost));

            player.displayClientMessage(Component.literal("Shadow Step: -30 mana").withStyle(ChatFormatting.RED), true);


            player.setData(MagicUngathering.SKILL_DATA, data.setCooldown(abilityId, currentTime));

            // Teleport Logic
            Vec3 oldPos = player.position();
            ServerLevel serverLevel = (ServerLevel) player.level();
            Vec3 lookAngle = target.getLookAngle().normalize();

            double tpX = target.getX() - (lookAngle.x * 1.5);
            double tpZ = target.getZ() - (lookAngle.z * 1.5);
            double tpY = target.getY();

            player.teleportTo(tpX, tpY, tpZ);
            player.lookAt(EntityAnchorArgument.Anchor.EYES, target.getEyePosition());

            // FX and Cleanup
            serverLevel.sendParticles(ParticleTypes.REVERSE_PORTAL, oldPos.x, oldPos.y + 1, oldPos.z, 20, 0.2, 0.5, 0.2, 0.05);
            serverLevel.sendParticles(ParticleTypes.REVERSE_PORTAL, tpX, tpY + 1, tpZ, 20, 0.2, 0.5, 0.2, 0.05);

            target.removeEffect(MobEffects.GLOWING);
            target.setData(MagicUngathering.MARK_DATA, new MarkData(null));

            player.level().playSound(null, tpX, tpY, tpZ, SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0f, 1.5f);
        } else {
            player.displayClientMessage(Component.literal("Shadow Step: No target found!").withStyle(ChatFormatting.RED), true);
        }
    }

    private static void executeSlam(ServerPlayer player) {
        player.level().playSound(null, player.blockPosition(), SoundEvents.GENERIC_EXPLODE.value(), SoundSource.PLAYERS, 1.0f, 1.0f);
    }

    private static void executeDash(ServerPlayer player) {
        Vec3 look = player.getLookAngle();
        player.setDeltaMovement(look.x * 1.5, 0.2, look.z * 1.5);
        player.hurtMarked = true;
    }
}