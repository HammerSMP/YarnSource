/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity.feature;

import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public abstract class StuckObjectsFeatureRenderer<T extends LivingEntity, M extends PlayerEntityModel<T>>
extends FeatureRenderer<T, M> {
    public StuckObjectsFeatureRenderer(LivingEntityRenderer<T, M> arg) {
        super(arg);
    }

    protected abstract int getObjectCount(T var1);

    protected abstract void renderObject(MatrixStack var1, VertexConsumerProvider var2, int var3, Entity var4, float var5, float var6, float var7, float var8);

    @Override
    public void render(MatrixStack arg, VertexConsumerProvider arg2, int i, T arg3, float f, float g, float h, float j, float k, float l) {
        int m = this.getObjectCount(arg3);
        Random random = new Random(((Entity)arg3).getEntityId());
        if (m <= 0) {
            return;
        }
        for (int n = 0; n < m; ++n) {
            arg.push();
            ModelPart lv = ((PlayerEntityModel)this.getContextModel()).getRandomPart(random);
            ModelPart.Cuboid lv2 = lv.getRandomCuboid(random);
            lv.rotate(arg);
            float o = random.nextFloat();
            float p = random.nextFloat();
            float q = random.nextFloat();
            float r = MathHelper.lerp(o, lv2.minX, lv2.maxX) / 16.0f;
            float s = MathHelper.lerp(p, lv2.minY, lv2.maxY) / 16.0f;
            float t = MathHelper.lerp(q, lv2.minZ, lv2.maxZ) / 16.0f;
            arg.translate(r, s, t);
            o = -1.0f * (o * 2.0f - 1.0f);
            p = -1.0f * (p * 2.0f - 1.0f);
            q = -1.0f * (q * 2.0f - 1.0f);
            this.renderObject(arg, arg2, i, (Entity)arg3, o, p, q, h);
            arg.pop();
        }
    }
}

