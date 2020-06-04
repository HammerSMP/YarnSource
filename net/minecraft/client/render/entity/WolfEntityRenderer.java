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
import net.minecraft.client.render.entity.feature.WolfCollarFeatureRenderer;
import net.minecraft.client.render.entity.model.WolfEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class WolfEntityRenderer
extends MobEntityRenderer<WolfEntity, WolfEntityModel<WolfEntity>> {
    private static final Identifier WILD_TEXTURE = new Identifier("textures/entity/wolf/wolf.png");
    private static final Identifier TAMED_TEXTURE = new Identifier("textures/entity/wolf/wolf_tame.png");
    private static final Identifier ANGRY_TEXTURE = new Identifier("textures/entity/wolf/wolf_angry.png");

    public WolfEntityRenderer(EntityRenderDispatcher arg) {
        super(arg, new WolfEntityModel(), 0.5f);
        this.addFeature(new WolfCollarFeatureRenderer(this));
    }

    @Override
    protected float getAnimationProgress(WolfEntity arg, float f) {
        return arg.getTailAngle();
    }

    @Override
    public void render(WolfEntity arg, float f, float g, MatrixStack arg2, VertexConsumerProvider arg3, int i) {
        if (arg.isFurWet()) {
            float h = MathHelper.clamp(arg.getBrightnessAtEyes() * arg.getFurWetBrightnessMultiplier(g), 0.0f, 1.0f);
            ((WolfEntityModel)this.model).setColorMultiplier(h, h, h);
        }
        super.render(arg, f, g, arg2, arg3, i);
        if (arg.isFurWet()) {
            ((WolfEntityModel)this.model).setColorMultiplier(1.0f, 1.0f, 1.0f);
        }
    }

    @Override
    public Identifier getTexture(WolfEntity arg) {
        if (arg.isTamed()) {
            return TAMED_TEXTURE;
        }
        if (arg.method_29511()) {
            return ANGRY_TEXTURE;
        }
        return WILD_TEXTURE;
    }
}

