package net.mp9.magicungathering;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.UUIDUtil;
import net.mp9.magicungathering.attachment.MarkData;
import net.mp9.magicungathering.attachment.SkillData;
import net.mp9.magicungathering.client.SkillTreeScreen;
import net.mp9.magicungathering.entity.ModEntities;
import net.mp9.magicungathering.item.ModCreativeModeTabs;
import net.mp9.magicungathering.item.ModItems;
import net.mp9.magicungathering.mana.ManaAttachment;
import net.mp9.magicungathering.registration.ModMenuTypes;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import java.util.ArrayList;
import java.util.HashMap;

@Mod(MagicUngathering.MOD_ID)
public class MagicUngathering {
    public static final String MOD_ID = "magicungathering";

    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
            DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, MOD_ID);

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<SkillData>> SKILL_DATA =
            ATTACHMENT_TYPES.register("skill_data", () -> AttachmentType.builder(() ->
                    new SkillData(new ArrayList<>(), 0, "Assassin", 0, new HashMap<>())
            ).serialize(SkillData.CODEC).build());

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<MarkData>> MARK_DATA =
            ATTACHMENT_TYPES.register("mark_data", () -> AttachmentType.builder(() -> new MarkData(null))
                    .serialize(RecordCodecBuilder.create(instance -> instance.group(
                            UUIDUtil.CODEC.fieldOf("markerId").forGetter(MarkData::markerId)
                    ).apply(instance, MarkData::new)))
                    .build());

    public MagicUngathering(IEventBus modEventBus, ModContainer modContainer) {
        net.mp9.magicungathering.attributes.ModAttributes.register(modEventBus);
        ModItems.register(modEventBus);

        ModEntities.register(modEventBus);

        ModCreativeModeTabs.register(modEventBus);
        ModMenuTypes.register(modEventBus);
        ATTACHMENT_TYPES.register(modEventBus);
        ManaAttachment.ATTACHMENT_TYPES.register(modEventBus);
        ModEffects.MOB_EFFECTS.register(modEventBus);

        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }
}