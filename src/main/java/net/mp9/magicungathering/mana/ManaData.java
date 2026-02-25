package net.mp9.magicungathering.mana;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record ManaData(int currentMana, int maxMana) {
    public static final Codec<ManaData> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.INT.fieldOf("currentMana").forGetter(ManaData::currentMana),
                    Codec.INT.fieldOf("maxMana").forGetter(ManaData::maxMana)
            ).apply(instance, ManaData::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, ManaData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, ManaData::currentMana,
            ByteBufCodecs.VAR_INT, ManaData::maxMana,
            ManaData::new
    );

    // what to do when manadata add is triggered. i.e. when more mana is added such as natural regen
    public ManaData add(int amount) {
        return new ManaData(Math.min(currentMana + amount, maxMana), maxMana);
    }

    // what to do when manadata consume is triggered. i.e. when mana is taken from using an ability
    public ManaData consume(int amount) {
        return new ManaData(Math.max(currentMana - amount, 0), maxMana);
    }

    // updates the current mana to the client
    public int currentMana() {return currentMana;}

    // updates the current max mana to the client
    public int maxMana() {return maxMana;}
}
