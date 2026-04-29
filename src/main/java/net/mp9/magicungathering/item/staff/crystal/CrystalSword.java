package net.mp9.magicungathering.item.staff.crystal;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import net.mp9.magicungathering.mana.ManaAttachment;
import net.mp9.magicungathering.mana.ManaData;
import net.mp9.magicungathering.entity.ModEntities;

public class CrystalSword extends SwordItem {

    public CrystalSword() {
        super(Tiers.IRON, new Properties().attributes(SwordItem.createAttributes(Tiers.IRON, 3, -2.4F)).stacksTo(1));
    }

    // hurtEnemy override removed so left-click deals standard physical damage

    @Override
    public ItemAttributeModifiers getDefaultAttributeModifiers() {
        ItemAttributeModifiers.Builder builder = ItemAttributeModifiers.builder();

        // add diamond damage
        builder.add(Attributes.ATTACK_DAMAGE, new AttributeModifier(
                        BASE_ATTACK_DAMAGE_ID, 6.0, AttributeModifier.Operation.ADD_VALUE),
                EquipmentSlotGroup.MAINHAND);

        builder.add(Attributes.ATTACK_SPEED, new AttributeModifier(
                        BASE_ATTACK_SPEED_ID, -2.4, AttributeModifier.Operation.ADD_VALUE),
                EquipmentSlotGroup.MAINHAND);

        return builder.build();
    }

    @Override
    public int getEnchantmentValue() {
        return 10;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (player.getCooldowns().isOnCooldown(this)) {
            return InteractionResultHolder.fail(player.getItemInHand(hand));
        }

        ItemStack stack = player.getItemInHand(hand);
        ManaData mana = player.getData(ManaAttachment.MANA.get());

        int cost = 50;
        int cooldownTicks = 120;

        if (mana.currentMana() >= cost) {
            if (!level.isClientSide()) {
                ManaData newMana = mana.consume(cost);
                player.setData(ManaAttachment.MANA.get(), newMana);

                TemporaryCrystal crystal = ModEntities.TEMP_CRYSTAL.get().create(level);
                if (crystal != null) {
                    crystal.moveTo(player.getX(), player.getEyeY() - 1.0, player.getZ(), 0, 0);
                    crystal.setShowBottom(false);
                    crystal.setInvulnerable(true);

                    // The crystal exists now; it will use TemporaryCrystal logic when hurt/timed out
                    level.addFreshEntity(crystal);
                }

                player.getCooldowns().addCooldown(this, cooldownTicks);
            }
            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
        }

        return InteractionResultHolder.pass(stack);
    }
}