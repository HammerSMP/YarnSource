/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.debug;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.Util;
import net.minecraft.util.shape.VoxelShape;

@Environment(value=EnvType.CLIENT)
public class CollisionDebugRenderer
implements DebugRenderer.Renderer {
    private final MinecraftClient client;
    private double lastUpdateTime = Double.MIN_VALUE;
    private List<VoxelShape> collisions = Collections.emptyList();

    public CollisionDebugRenderer(MinecraftClient arg) {
        this.client = arg;
    }

    @Override
    public void render(MatrixStack arg2, VertexConsumerProvider arg22, double d, double e, double f) {
        double g = Util.getMeasuringTimeNano();
        if (g - this.lastUpdateTime > 1.0E8) {
            this.lastUpdateTime = g;
            Entity lv = this.client.gameRenderer.getCamera().getFocusedEntity();
            this.collisions = lv.world.getCollisions(lv, lv.getBoundingBox().expand(6.0), arg -> true).collect(Collectors.toList());
        }
        VertexConsumer lv2 = arg22.getBuffer(RenderLayer.getLines());
        for (VoxelShape lv3 : this.collisions) {
            WorldRenderer.method_22983(arg2, lv2, lv3, -d, -e, -f, 1.0f, 1.0f, 1.0f, 1.0f);
        }
    }
}

