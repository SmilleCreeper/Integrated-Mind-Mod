package com.integratedmind.client;

import com.integratedmind.DoctrineWatcherBlock;
import com.integratedmind.DoctrineWatcherBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Matrix4f;

public class DoctrineWatcherRenderer implements BlockEntityRenderer<DoctrineWatcherBlockEntity> {
    private final Font font;

    public DoctrineWatcherRenderer(BlockEntityRendererProvider.Context context) {
        this.font = context.getFont();
    }

    @Override
    public void render(DoctrineWatcherBlockEntity be, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        float value = be.getStoredValue();
        String text = String.format("%.6f", value);
        Level level = be.getLevel();
        BlockPos pos = be.getBlockPos();

        if (level == null) return;
        BlockState state = level.getBlockState(pos);
        if (!(state.getBlock() instanceof DoctrineWatcherBlock)) return;

        Direction facing = state.getValue(DoctrineWatcherBlock.FACING);
        Direction left = facing.getCounterClockWise();
        Direction right = facing.getClockWise();

        renderTextOnSide(poseStack, buffer, text, left, packedLight, level, pos);
        renderTextOnSide(poseStack, buffer, text, right, packedLight, level, pos);
    }

    private void renderTextOnSide(PoseStack poseStack, MultiBufferSource buffer, String text, Direction dir, int packedLight, Level level, BlockPos pos) {
        BlockPos adjacentPos = pos.relative(dir);
        if (level.getBlockState(adjacentPos).isSolidRender(level, adjacentPos)) return;

        poseStack.pushPose();

        float centerX = 0.5f;
        float centerY = 0.5f;
        float centerZ = 0.5f;

        switch (dir) {
            case NORTH -> {
                poseStack.translate(centerX, centerY, 0.001f);
                poseStack.mulPose(Axis.YP.rotationDegrees(180));
            }
            case SOUTH -> {
                poseStack.translate(centerX, centerY, 1.0f - 0.001f);
            }
            case EAST -> {
                poseStack.translate(1.0f - 0.001f, centerY, centerZ);
                poseStack.mulPose(Axis.YP.rotationDegrees(90));
            }
            case WEST -> {
                poseStack.translate(0.001f, centerY, centerZ);
                poseStack.mulPose(Axis.YP.rotationDegrees(-90));
            }
        }

        float scale = 0.01f;
        poseStack.scale(scale, -scale, scale);

        int textWidth = font.width(text);
        float textX = -textWidth / 2.0f;
        float textY = -font.lineHeight / 2.0f;

        poseStack.translate(0, 0, 0.1f);

        Matrix4f matrix = poseStack.last().pose();
        font.drawInBatch(Component.literal(text), textX, textY, 0xFFFFFF,
                false, matrix, buffer, Font.DisplayMode.SEE_THROUGH, 0x40000000, packedLight);

        poseStack.popPose();
    }
}
