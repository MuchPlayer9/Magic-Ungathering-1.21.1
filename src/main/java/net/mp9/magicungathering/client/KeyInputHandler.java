package net.mp9.magicungathering.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import net.mp9.magicungathering.MagicUngathering;
import org.lwjgl.glfw.GLFW;

@EventBusSubscriber(modid = MagicUngathering.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class KeyInputHandler {
    public static final String KEY_CATEGORY_MAGIC = "key.category.magicungathering.spells";

    public static final KeyMapping OPEN_MAGIC_MENU = new KeyMapping(
            "key.magicungathering.open_skill_tree_menu",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_G,
            KEY_CATEGORY_MAGIC
    );

    public static final KeyMapping MOVEMENT = new KeyMapping(
            "key.magicungathering.movement",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_K,
            KEY_CATEGORY_MAGIC
    );

    public static final KeyMapping ABILITY_1 = new KeyMapping(
            "key.magicungathering.ability_1",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_H,
            KEY_CATEGORY_MAGIC
    );

    public static final KeyMapping ABILITY_2 = new KeyMapping(
            "key.magicungathering.ability_2",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_I,
            KEY_CATEGORY_MAGIC
    );

    public static final KeyMapping ABILITY_3 = new KeyMapping(
            "key.magicungathering.ability_3",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_J,
            KEY_CATEGORY_MAGIC
    );

    @SubscribeEvent
    public static void registerKeys(RegisterKeyMappingsEvent event) {
        event.register(OPEN_MAGIC_MENU);
        event.register(MOVEMENT);
        event.register(ABILITY_1);
        event.register(ABILITY_2);
        event.register(ABILITY_3);
    }
}