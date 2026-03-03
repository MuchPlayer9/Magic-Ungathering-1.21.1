package net.mp9.magicungathering.item.infusions;

import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.mp9.magicungathering.item.ModItems; // Make sure this is imported
import net.mp9.magicungathering.mana.ManaAttachment;
import net.mp9.magicungathering.mana.ManaData;

import java.util.List;

public class BasicManaStick extends SwordItem {

    public BasicManaStick(Properties durability) {
        super(Tiers.IRON, new Properties().stacksTo(1));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        // 1. Check if THIS item is on cooldown
        if (player.getCooldowns().isOnCooldown(this)) {
            return InteractionResultHolder.fail(stack);
        }

        ManaData mana = player.getData(ManaAttachment.MANA.get());

        float manaToGrant = 10.0f;
        float damageAmount = 2.0f;
        int cooldownTicks = 200;

        if (!level.isClientSide()) {
            ManaData newMana = mana.consume((int)(-manaToGrant));
            player.setData(ManaAttachment.MANA.get(), newMana);

            if (!player.isCreative()) {
                player.hurt(player.damageSources().fellOutOfWorld(), damageAmount);
            }

            stack.hurtAndBreak(1, player, player.getEquipmentSlotForItem(stack));

            // 2. MANUAL GROUP COOLDOWN
            // We apply the cooldown to all three Registry Objects specifically
            player.getCooldowns().addCooldown(ModItems.BASIC_MANA_STICK.get(), cooldownTicks);
            player.getCooldowns().addCooldown(ModItems.MEDIUM_MANA_STICK.get(), cooldownTicks);
            player.getCooldowns().addCooldown(ModItems.LARGE_MANA_STICK.get(), cooldownTicks);

            AreaEffectCloud cloud = new AreaEffectCloud(level, player.getX(), player.getY() + 0.2, player.getZ());
            cloud.setParticle(ParticleTypes.DAMAGE_INDICATOR);
            cloud.setRadius(2.0f);
            cloud.setDuration(40);
            level.addFreshEntity(cloud);
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("Tier: ").withStyle(ChatFormatting.GRAY)
                .append(Component.literal("Basic").withStyle(ChatFormatting.GREEN)));

        tooltip.add(Component.literal("Sacrifice health for mana.").withStyle(ChatFormatting.DARK_PURPLE, ChatFormatting.ITALIC));

        tooltip.add(Component.literal("Shared Cooldown: ").withStyle(ChatFormatting.RED)
                .append(Component.literal("10s").withStyle(ChatFormatting.RESET, ChatFormatting.GRAY)));

        super.appendHoverText(stack, context, tooltip, flag);
    }
}