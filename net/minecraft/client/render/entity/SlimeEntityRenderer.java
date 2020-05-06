/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.feature.SlimeOverlayFeatureRenderer;
import net.minecraft.client.render.entity.model.SlimeEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class SlimeEntityRenderer
extends MobEntityRenderer<SlimeEntity, SlimeEntityModel<SlimeEntity>> {
    private static final Identifier TEXTURE = new Identifier("textures/entity/slime/slime.png");

    public SlimeEntityRenderer(EntityRenderDispatcher arg) {
        super(arg, new SlimeEntityModel(16), 0.25f);
        this.addFeature(new SlimeOverlayFeatureRenderer<SlimeEntity>(this));
    }

    @Override
    public void render(SlimeEntity arg, float f, float g, MatrixStack arg2, VertexConsumerProvider arg3, int i) {
        this.shadowRadius = 0.25f * (float)arg.getSize();
        super.render(arg, f, g, arg2, arg3, i);
    }

    @Override
    protected void scale(SlimeEntity arg, MatrixStack arg2, float f) {
        float g = 0.999f;
        arg2.scale(0.999f, 0.999f, 0.999f);
        arg2.translate(0.0, 0.001f, 0.0);
        float h = arg.getSize();
        float i = MathHelper.lerp(f, arg.lastStretch, arg.stretch) / (h * 0.5f + 1.0f);
        float j = 1.0f / (i + 1.0f);
        arg2.scale(j * h, 1.0f / j * h, j * h);
    }

    @Override
    public Identifier getTexture(SlimeEntity arg) {
        return TEXTURE;
    }
}

