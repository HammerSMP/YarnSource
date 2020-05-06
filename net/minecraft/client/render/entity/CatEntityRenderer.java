/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.feature.CatCollarFeatureRenderer;
import net.minecraft.client.render.entity.model.CatEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class CatEntityRenderer
extends MobEntityRenderer<CatEntity, CatEntityModel<CatEntity>> {
    public CatEntityRenderer(EntityRenderDispatcher arg) {
        super(arg, new CatEntityModel(0.0f), 0.4f);
        this.addFeature(new CatCollarFeatureRenderer(this));
    }

    @Override
    public Identifier getTexture(CatEntity arg) {
        return arg.getTexture();
    }

    @Override
    protected void scale(CatEntity arg, MatrixStack arg2, float f) {
        super.scale(arg, arg2, f);
        arg2.scale(0.8f, 0.8f, 0.8f);
    }

    @Override
    protected void setupTransforms(CatEntity arg, MatrixStack arg2, float f, float g, float h) {
        super.setupTransforms(arg, arg2, f, g, h);
        float i = arg.getSleepAnimation(h);
        if (i > 0.0f) {
            arg2.translate(0.4f * i, 0.15f * i, 0.1f * i);
            arg2.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(MathHelper.lerpAngleDegrees(i, 0.0f, 90.0f)));
            BlockPos lv = arg.getBlockPos();
            List<PlayerEntity> list = arg.world.getNonSpectatingEntities(PlayerEntity.class, new Box(lv).expand(2.0, 2.0, 2.0));
            for (PlayerEntity lv2 : list) {
                if (!lv2.isSleeping()) continue;
                arg2.translate(0.15f * i, 0.0, 0.0);
                break;
            }
        }
    }
}

