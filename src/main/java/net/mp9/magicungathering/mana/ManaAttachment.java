package net.mp9.magicungathering.mana;

import net.mp9.magicungathering.MagicUngathering;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.attachment.AttachmentType;

import java.util.function.Supplier;

public class ManaAttachment {

    // creates attachment types
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
            DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, MagicUngathering.MOD_ID);

    // creates an attachment called mana, which is a string of (current mana, max mana). (sets both to 100, for new players?).
    public static final Supplier<AttachmentType<ManaData>> MANA =
            ATTACHMENT_TYPES.register("mana", () ->
                    AttachmentType.builder(() -> new ManaData(0))
                            .serialize(ManaData.CODEC)
                            .sync(ManaData.STREAM_CODEC)
                            .copyOnDeath() // mana persists after dying
                            .build()
            );

}
