package net.mp9.magicungathering.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.mp9.magicungathering.menu.ClassSelectionMenu;

public class ClassSelectionScreen extends AbstractContainerScreen<ClassSelectionMenu> {
    // This texture contains both the top and bottom of chests.
    private static final ResourceLocation CONTAINER_BACKGROUND = ResourceLocation.withDefaultNamespace("textures/gui/container/generic_54.png");

    public ClassSelectionScreen(ClassSelectionMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 166; // Height for a 3-row container
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;

        // 1. Draw the Header + the first 3 rows of slots
        // (x, y, uOffset, vOffset, width, height)
        // 71 is the height from the top of the GUI to the end of the 3rd row
        guiGraphics.blit(CONTAINER_BACKGROUND, x, y, 0, 0, this.imageWidth, 71);

        // 2. Draw the Player Inventory (skipping the middle 3 empty rows)
        // 126 is where the player inventory starts in the generic_54 texture
        // 96 is the height of the player inventory section
        guiGraphics.blit(CONTAINER_BACKGROUND, x, y + 71, 0, 126, this.imageWidth, 96);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }
}