/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.block.entity;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.EndPortalBlockEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Matrix4f;

@Environment(value=EnvType.CLIENT)
public class EndPortalBlockEntityRenderer<T extends EndPortalBlockEntity>
extends BlockEntityRenderer<T> {
    public static final Identifier SKY_TEXTURE = new Identifier("textures/environment/end_sky.png");
    public static final Identifier PORTAL_TEXTURE = new Identifier("textures/entity/end_portal.png");
    private static final Random RANDOM = new Random(31100L);
    private static final List<RenderLayer> field_21732 = (List)IntStream.range(0, 16).mapToObj(i -> RenderLayer.getEndPortal(i + 1)).collect(ImmutableList.toImmutableList());

    public EndPortalBlockEntityRenderer(BlockEntityRenderDispatcher arg) {
        super(arg);
    }

    @Override
    public void render(T arg, float f, MatrixStack arg2, VertexConsumerProvider arg3, int i, int j) {
        RANDOM.setSeed(31100L);
        double d = ((BlockEntity)arg).getPos().getSquaredDistance(this.dispatcher.camera.getPos(), true);
        int k = this.method_3592(d);
        float g = this.method_3594();
        Matrix4f lv = arg2.peek().getModel();
        this.method_23084(arg, g, 0.15f, lv, arg3.getBuffer(field_21732.get(0)));
        for (int l = 1; l < k; ++l) {
            this.method_23084(arg, g, 2.0f / (float)(18 - l), lv, arg3.getBuffer(field_21732.get(l)));
        }
    }

    private void method_23084(T arg, float f, float g, Matrix4f arg2, VertexConsumer arg3) {
        float h = (RANDOM.nextFloat() * 0.5f + 0.1f) * g;
        float i = (RANDOM.nextFloat() * 0.5f + 0.4f) * g;
        float j = (RANDOM.nextFloat() * 0.5f + 0.5f) * g;
        this.method_23085(arg, arg2, arg3, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, h, i, j, Direction.SOUTH);
        this.method_23085(arg, arg2, arg3, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, h, i, j, Direction.NORTH);
        this.method_23085(arg, arg2, arg3, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, h, i, j, Direction.EAST);
        this.method_23085(arg, arg2, arg3, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, h, i, j, Direction.WEST);
        this.method_23085(arg, arg2, arg3, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, h, i, j, Direction.DOWN);
        this.method_23085(arg, arg2, arg3, 0.0f, 1.0f, f, f, 1.0f, 1.0f, 0.0f, 0.0f, h, i, j, Direction.UP);
    }

    private void method_23085(T arg, Matrix4f arg2, VertexConsumer arg3, float f, float g, float h, float i, float j, float k, float l, float m, float n, float o, float p, Direction arg4) {
        if (((EndPortalBlockEntity)arg).shouldDrawSide(arg4)) {
            arg3.vertex(arg2, f, h, j).color(n, o, p, 1.0f).next();
            arg3.vertex(arg2, g, h, k).color(n, o, p, 1.0f).next();
            arg3.vertex(arg2, g, i, l).color(n, o, p, 1.0f).next();
            arg3.vertex(arg2, f, i, m).color(n, o, p, 1.0f).next();
        }
    }

    protected int method_3592(double d) {
        if (d > 36864.0) {
            return 1;
        }
        if (d > 25600.0) {
            return 3;
        }
        if (d > 16384.0) {
            return 5;
        }
        if (d > 9216.0) {
            return 7;
        }
        if (d > 4096.0) {
            return 9;
        }
        if (d > 1024.0) {
            return 11;
        }
        if (d > 576.0) {
            return 13;
        }
        if (d > 256.0) {
            return 14;
        }
        return 15;
    }

    protected float method_3594() {
        return 0.75f;
    }
}

