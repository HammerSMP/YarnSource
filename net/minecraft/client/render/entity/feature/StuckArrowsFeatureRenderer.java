/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.StuckObjectsFeatureRenderer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class StuckArrowsFeatureRenderer<T extends LivingEntity, M extends PlayerEntityModel<T>>
extends StuckObjectsFeatureRenderer<T, M> {
    private final EntityRenderDispatcher dispatcher;
    private ArrowEntity arrow;

    public StuckArrowsFeatureRenderer(LivingEntityRenderer<T, M> arg) {
        super(arg);
        this.dispatcher = arg.getRenderManager();
    }

    @Override
    protected int getObjectCount(T arg) {
        return ((LivingEntity)arg).getStuckArrowCount();
    }

    @Override
    protected void renderObject(MatrixStack arg, VertexConsumerProvider arg2, int i, Entity arg3, float f, float g, float h, float j) {
        float k = MathHelper.sqrt(f * f + h * h);
        this.arrow = new ArrowEntity(arg3.world, arg3.getX(), arg3.getY(), arg3.getZ());
        this.arrow.yaw = (float)(Math.atan2(f, h) * 57.2957763671875);
        this.arrow.pitch = (float)(Math.atan2(g, k) * 57.2957763671875);
        this.arrow.prevYaw = this.arrow.yaw;
        this.arrow.prevPitch = this.arrow.pitch;
        this.dispatcher.render(this.arrow, 0.0, 0.0, 0.0, 0.0f, j, arg, arg2, i);
    }
}

