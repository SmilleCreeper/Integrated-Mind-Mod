package com.integratedmind.client;

import com.integratedmind.NeuronBlockEntity;
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

public class NeuronBlockRenderer implements BlockEntityRenderer<NeuronBlockEntity> {
    private final Font font;

    public NeuronBlockRenderer(BlockEntityRendererProvider.Context context) {
        this.font = context.getFont();
    }

    @Override
    public void render(NeuronBlockEntity be, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        float value = be.getValue();
        String text = String.format("%.6f", value);
        Level level = be.getLevel();
        BlockPos pos = be.getBlockPos();

        if (level == null) return;
        BlockState state = level.getBlockState(pos);

        for (Direction dir : Direction.values()) {
            renderTextOnSide(poseStack, buffer, text, dir, packedLight, level, pos);
        }
    }

    private void renderTextOnSide(PoseStack poseStack, MultiBufferSource buffer, String text, Direction dir, int packedLight, Level level, BlockPos pos) {
        BlockPos adjacentPos = pos.relative(dir);
        if (level.getBlockState(adjacentPos).isSolidRender(level, adjacentPos)) return;

        poseStack.pushPose();

        switch (dir) {
            case NORTH -> {
                poseStack.translate(0.5, 0.5, 0.001);
                poseStack.mulPose(Axis.YP.rotationDegrees(180));
            }
            case SOUTH -> {
                poseStack.translate(0.5, 0.5, 0.999);
            }
            case EAST -> {
                poseStack.translate(0.999, 0.5, 0.5);
                poseStack.mulPose(Axis.YP.rotationDegrees(90));
            }
            case WEST -> {
                poseStack.translate(0.001, 0.5, 0.5);
                poseStack.mulPose(Axis.YP.rotationDegrees(-90));
            }
            case UP -> {
                poseStack.translate(0.5, 0.999, 0.5);
                poseStack.mulPose(Axis.XP.rotationDegrees(90));
            }
            case DOWN -> {
                poseStack.translate(0.5, 0.001, 0.5);
                poseStack.mulPose(Axis.XP.rotationDegrees(-90));
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
