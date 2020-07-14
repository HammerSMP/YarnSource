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
import net.minecraft.client.render.entity.IllagerEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.IllagerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.IllusionerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

@Environment(value=EnvType.CLIENT)
public class IllusionerEntityRenderer
extends IllagerEntityRenderer<IllusionerEntity> {
    private static final Identifier TEXTURE = new Identifier("textures/entity/illager/illusioner.png");

    public IllusionerEntityRenderer(EntityRenderDispatcher arg) {
        super(arg, new IllagerEntityModel(0.0f, 0.0f, 64, 64), 0.5f);
        this.addFeature(new HeldItemFeatureRenderer<IllusionerEntity, IllagerEntityModel<IllusionerEntity>>((FeatureRendererContext)this){

            @Override
            public void render(MatrixStack arg, VertexConsumerProvider arg2, int i, IllusionerEntity arg3, float f, float g, float h, float j, float k, float l) {
                if (arg3.isSpellcasting() || arg3.isAttacking()) {
                    super.render(arg, arg2, i, arg3, f, g, h, j, k, l);
                }
            }
        });
        ((IllagerEntityModel)this.model).method_2812().visible = true;
    }

    @Override
    public Identifier getTexture(IllusionerEntity arg) {
        return TEXTURE;
    }

    @Override
    public void render(IllusionerEntity arg, float f, float g, MatrixStack arg2, VertexConsumerProvider arg3, int i) {
        if (arg.isInvisible()) {
            Vec3d[] lvs = arg.method_7065(g);
            float h = this.getAnimationProgress(arg, g);
            for (int j = 0; j < lvs.length; ++j) {
                arg2.push();
                arg2.translate(lvs[j].x + (double)MathHelper.cos((float)j + h * 0.5f) * 0.025, lvs[j].y + (double)MathHelper.cos((float)j + h * 0.75f) * 0.0125, lvs[j].z + (double)MathHelper.cos((float)j + h * 0.7f) * 0.025);
                super.render(arg, f, g, arg2, arg3, i);
                arg2.pop();
            }
        } else {
            super.render(arg, f, g, arg2, arg3, i);
        }
    }

    @Override
    protected boolean isVisible(IllusionerEntity arg) {
        return true;
    }

    @Override
    protected /* synthetic */ boolean isVisible(LivingEntity entity) {
        return this.isVisible((IllusionerEntity)entity);
    }
}

