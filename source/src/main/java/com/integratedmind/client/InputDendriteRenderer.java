package com.integratedmind.client;

import com.integratedmind.InputDendriteBlockEntity;
import com.integratedmind.InputDendriteBlockEntity.ConnectionData;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class InputDendriteRenderer implements BlockEntityRenderer<InputDendriteBlockEntity> {
    public InputDendriteRenderer(BlockEntityRendererProvider.Context ctx) {
    }

    @Override
    public void render(InputDendriteBlockEntity entity, float partialTick, PoseStack pose, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        List<ConnectionData> connections = entity.getConnections();
        if (connections.isEmpty()) return;

        VertexConsumer consumer = buffer.getBuffer(RenderType.LINES);

        double cx = 0.5;
        double cy = 0.5;
        double cz = 0.5;

        for (ConnectionData conn : connections) {
            double tx = conn.outputPos.getX() - entity.getBlockPos().getX() + 0.5;
            double ty = conn.outputPos.getY() - entity.getBlockPos().getY() + 0.5;
            double tz = conn.outputPos.getZ() - entity.getBlockPos().getZ() + 0.5;

            double dx = tx - cx;
            double dy = ty - cy;
            double dz = tz - cz;
            double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);
            int segments = Math.max(4, (int) (dist * 3));

            float r, g, b;
            if (Math.abs(conn.weight - 1.0f) < 0.001f) {
                r = 0.3f; g = 0.4f; b = 1.0f;
            } else {
                r = conn.weight > 0 ? 0.6f : 0.8f;
                g = conn.weight > 0 ? 0.8f : 0.5f;
                b = 0.6f;
            }

            for (int i = 0; i < segments; i++) {
                double t0 = (double) i / segments;
                double t1 = (double) (i + 1) / segments;

                float x0 = (float) (cx + dx * t0);
                float y0 = (float) (cy + dy * t0);
                float z0 = (float) (cz + dz * t0);
                float x1 = (float) (cx + dx * t1);
                float y1 = (float) (cy + dy * t1);
                float z1 = (float) (cz + dz * t1);

                consumer.addVertex(pose.last(), x0, y0, z0)
                        .setColor(r, g, b, 0.9f)
                        .setNormal((float) (dx / dist), (float) (dy / dist), (float) (dz / dist))
                        .setLight(0x00F000F0);
                consumer.addVertex(pose.last(), x1, y1, z1)
                        .setColor(r, g, b, 0.9f)
                        .setNormal((float) (dx / dist), (float) (dy / dist), (float) (dz / dist))
                        .setLight(0x00F000F0);
            }
        }
    }

    @Override
    public boolean shouldRenderOffScreen(InputDendriteBlockEntity entity) {
        return true;
    }
}
