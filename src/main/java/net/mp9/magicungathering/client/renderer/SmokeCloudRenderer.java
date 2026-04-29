package net.mp9.magicungathering.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.mp9.magicungathering.entity.SmokeCloudEntity;

public class SmokeCloudRenderer extends EntityRenderer<SmokeCloudEntity> {
    private final BlockRenderDispatcher blockRenderer;

    public SmokeCloudRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.blockRenderer = context.getBlockRenderDispatcher();
    }

    @Override
    public void render(SmokeCloudEntity entity, float yaw, float partialTicks, PoseStack pose, MultiBufferSource buffer, int packedLight) {
        pose.pushPose();

        float size = 5.0f;
        pose.scale(size, size, size);
        pose.translate(-0.5D, 0.0D, -0.5D);

        BlockState state = Blocks.GRAY_STAINED_GLASS.defaultBlockState();

        // Render Outside
        this.blockRenderer.renderSingleBlock(state, pose, buffer, packedLight, OverlayTexture.NO_OVERLAY);

        // Render Inside
        pose.pushPose();
        pose.translate(0.5D, 0.5D, 0.5D);
        pose.scale(-0.999f, -0.999f, -0.999f);
        pose.translate(-0.5D, -0.5D, -0.5D);
        this.blockRenderer.renderSingleBlock(state, pose, buffer, packedLight, OverlayTexture.NO_OVERLAY);
        pose.popPose();

        pose.popPose();
        super.render(entity, yaw, partialTicks, pose, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(SmokeCloudEntity entity) {
        return ResourceLocation.fromNamespaceAndPath("minecraft", "textures/block/gray_stained_glass.png");
    }
}