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
import net.minecraft.client.render.entity.feature.TropicalFishColorFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.LargeTropicalFishEntityModel;
import net.minecraft.client.render.entity.model.SmallTropicalFishEntityModel;
import net.minecraft.client.render.entity.model.TintableCompositeModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.passive.TropicalFishEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class TropicalFishEntityRenderer
extends MobEntityRenderer<TropicalFishEntity, EntityModel<TropicalFishEntity>> {
    private final SmallTropicalFishEntityModel<TropicalFishEntity> smallModel = new SmallTropicalFishEntityModel(0.0f);
    private final LargeTropicalFishEntityModel<TropicalFishEntity> largeModel = new LargeTropicalFishEntityModel(0.0f);

    public TropicalFishEntityRenderer(EntityRenderDispatcher arg) {
        super(arg, new SmallTropicalFishEntityModel(0.0f), 0.15f);
        this.addFeature(new TropicalFishColorFeatureRenderer(this));
    }

    @Override
    public Identifier getTexture(TropicalFishEntity arg) {
        return arg.getShapeId();
    }

    @Override
    public void render(TropicalFishEntity arg, float f, float g, MatrixStack arg2, VertexConsumerProvider arg3, int i) {
        TintableCompositeModel lv;
        this.model = lv = arg.getShape() == 0 ? this.smallModel : this.largeModel;
        float[] fs = arg.getBaseColorComponents();
        lv.setColorMultiplier(fs[0], fs[1], fs[2]);
        super.render(arg, f, g, arg2, arg3, i);
        lv.setColorMultiplier(1.0f, 1.0f, 1.0f);
    }

    @Override
    protected void setupTransforms(TropicalFishEntity arg, MatrixStack arg2, float f, float g, float h) {
        super.setupTransforms(arg, arg2, f, g, h);
        float i = 4.3f * MathHelper.sin(0.6f * f);
        arg2.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(i));
        if (!arg.isTouchingWater()) {
            arg2.translate(0.2f, 0.1f, 0.0);
            arg2.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(90.0f));
        }
    }
}

