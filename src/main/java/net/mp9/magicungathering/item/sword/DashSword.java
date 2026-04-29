package net.mp9.magicungathering.item.sword;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.mp9.magicungathering.mana.ManaAttachment;
import net.mp9.magicungathering.mana.ManaData;

public class DashSword extends SwordItem {
    public DashSword() {
        super(Tiers.IRON, new Item.Properties().stacksTo(1));
    }

    // Change to STATIC so DashPacket can call it directly
    public static void performDash(Player player, Item item) {
        Level level = player.level();

        // Check cooldown ONLY if an item was provided (Sword right-click)
        if (item != null && player.getCooldowns().isOnCooldown(item)) return;
        if (player.isFallFlying()) return;

        ManaData mana = player.getData(ManaAttachment.MANA.get());
        int cost = 40;
        int cooldownTicks = 20;

        if (mana.currentMana() >= cost) {
            if (!level.isClientSide()) {
                player.setData(ManaAttachment.MANA.get(), mana.consume(cost));

                // Only apply item cooldown if an item exists
                if (item != null) {
                    player.getCooldowns().addCooldown(item, cooldownTicks);
                }

                ((ServerLevel)level).sendParticles(ParticleTypes.POOF,
                        player.getX(), player.getY(), player.getZ(),
                        15, 0.2, 0.2, 0.2, 0.1);
            }

            // EXACT SAME MOVEMENT LOGIC
            Vec3 lookVec = player.getLookAngle();
            double dashPower = 2.5;

            player.setDeltaMovement(lookVec.x * dashPower, lookVec.y * (dashPower / 2), lookVec.z * dashPower);
            player.hurtMarked = true;
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        // Pass 'this' so it triggers item cooldowns
        performDash(player, this);
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    @Override
    public ItemAttributeModifiers getDefaultAttributeModifiers() {
        ItemAttributeModifiers.Builder builder = ItemAttributeModifiers.builder();
        // Diamond Damage (6.0 + 1.0 base = 7.0 total)
        builder.add(Attributes.ATTACK_DAMAGE, new AttributeModifier(
                        BASE_ATTACK_DAMAGE_ID, 6.0, AttributeModifier.Operation.ADD_VALUE),
                EquipmentSlotGroup.MAINHAND);

        // Standard Sword Speed
        builder.add(Attributes.ATTACK_SPEED, new AttributeModifier(
                        BASE_ATTACK_SPEED_ID, -2.4, AttributeModifier.Operation.ADD_VALUE),
                EquipmentSlotGroup.MAINHAND);

        return builder.build();
    }

    @Override
    public int getEnchantmentValue() {
        return 10;
    }
}