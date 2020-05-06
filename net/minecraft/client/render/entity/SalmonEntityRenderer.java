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
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.SalmonEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.passive.SalmonEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class SalmonEntityRenderer
extends MobEntityRenderer<SalmonEntity, SalmonEntityModel<SalmonEntity>> {
    private static final Identifier TEXTURE = new Identifier("textures/entity/fish/salmon.png");

    public SalmonEntityRenderer(EntityRenderDispatcher arg) {
        super(arg, new SalmonEntityModel(), 0.4f);
    }

    @Override
    public Identifier getTexture(SalmonEntity arg) {
        return TEXTURE;
    }

    @Override
    protected void setupTransforms(SalmonEntity arg, MatrixStack arg2, float f, float g, float h) {
        super.setupTransforms(arg, arg2, f, g, h);
        float i = 1.0f;
        float j = 1.0f;
        if (!arg.isTouchingWater()) {
            i = 1.3f;
            j = 1.7f;
        }
        float k = i * 4.3f * MathHelper.sin(j * 0.6f * f);
        arg2.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(k));
        arg2.translate(0.0, 0.0, -0.4f);
        if (!arg.isTouchingWater()) {
            arg2.translate(0.2f, 0.1f, 0.0);
            arg2.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(90.0f));
        }
    }
}

