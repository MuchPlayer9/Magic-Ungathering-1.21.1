package net.mp9.magicungathering.attachment;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

public record SkillData(List<String> unlockedSkills, int points, String currentClass, int totalPoints, Map<String, Long> cooldowns) {

    public static final Codec<SkillData> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.STRING.listOf().fieldOf("unlockedSkills").forGetter(SkillData::unlockedSkills),
                    Codec.INT.fieldOf("points").forGetter(SkillData::points),
                    Codec.STRING.fieldOf("currentClass").forGetter(SkillData::currentClass),
                    Codec.INT.fieldOf("totalPoints").forGetter(SkillData::totalPoints),
                    Codec.unboundedMap(Codec.STRING, Codec.LONG).fieldOf("cooldowns").forGetter(SkillData::cooldowns)
            ).apply(instance, SkillData::new)
    );

    // FIXED: StreamCodec must handle the Map and match the record constructor exactly
    public static final StreamCodec<RegistryFriendlyByteBuf, SkillData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs.list()), SkillData::unlockedSkills,
            ByteBufCodecs.VAR_INT, SkillData::points,
            ByteBufCodecs.STRING_UTF8, SkillData::currentClass,
            ByteBufCodecs.VAR_INT, SkillData::totalPoints,
            ByteBufCodecs.map(HashMap::new, ByteBufCodecs.STRING_UTF8, ByteBufCodecs.VAR_LONG), SkillData::cooldowns,
            SkillData::new
    );

    public boolean hasSkill(String skillId) {
        return unlockedSkills.contains(skillId);
    }

    // FIXED: Updated helper methods to pass along the cooldowns map
    public SkillData addPoints(int amount) {
        return new SkillData(this.unlockedSkills, this.points + amount, this.currentClass, this.totalPoints + amount, this.cooldowns);
    }

    public SkillData setClass(String newClass) {
        return new SkillData(this.unlockedSkills, this.points, newClass, this.totalPoints, this.cooldowns);
    }

    // NEW: Helper methods for cooldowns
    public long getCooldown(String abilityId) {
        return cooldowns.getOrDefault(abilityId, 0L);
    }

    public SkillData setCooldown(String abilityId, long worldTick) {
        Map<String, Long> newCooldowns = new HashMap<>(this.cooldowns);
        newCooldowns.put(abilityId, worldTick);
        return new SkillData(unlockedSkills, points, currentClass, totalPoints, newCooldowns);
    }

    public String getCurrentClass() {
        return currentClass != null ? currentClass : "Assassin";
    }
}