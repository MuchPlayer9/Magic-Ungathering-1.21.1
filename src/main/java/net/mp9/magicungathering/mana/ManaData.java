package net.mp9.magicungathering.mana;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record ManaData(float currentMana) {

    public static final Codec<ManaData> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.FLOAT.fieldOf("currentMana").forGetter(ManaData::currentMana)
            ).apply(instance, ManaData::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, ManaData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.FLOAT, ManaData::currentMana,
            ManaData::new
    );

    // Simple addition. The clamping is now handled in ManaTickHandler using Attributes.
    public ManaData add(int amount) {
        return new ManaData(this.currentMana + amount);
    }

    // Simple consumption. We still clamp at 0 because mana can't be negative.
    public ManaData consume(int amount) {
        return new ManaData(Math.max(this.currentMana - amount, 0));
    }
}