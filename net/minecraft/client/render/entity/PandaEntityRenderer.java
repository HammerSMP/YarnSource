/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity;

import com.google.common.collect.Maps;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.feature.PandaHeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.PandaEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.passive.PandaEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class PandaEntityRenderer
extends MobEntityRenderer<PandaEntity, PandaEntityModel<PandaEntity>> {
    private static final Map<PandaEntity.Gene, Identifier> TEXTURES = Util.make(Maps.newEnumMap(PandaEntity.Gene.class), enumMap -> {
        enumMap.put(PandaEntity.Gene.NORMAL, new Identifier("textures/entity/panda/panda.png"));
        enumMap.put(PandaEntity.Gene.LAZY, new Identifier("textures/entity/panda/lazy_panda.png"));
        enumMap.put(PandaEntity.Gene.WORRIED, new Identifier("textures/entity/panda/worried_panda.png"));
        enumMap.put(PandaEntity.Gene.PLAYFUL, new Identifier("textures/entity/panda/playful_panda.png"));
        enumMap.put(PandaEntity.Gene.BROWN, new Identifier("textures/entity/panda/brown_panda.png"));
        enumMap.put(PandaEntity.Gene.WEAK, new Identifier("textures/entity/panda/weak_panda.png"));
        enumMap.put(PandaEntity.Gene.AGGRESSIVE, new Identifier("textures/entity/panda/aggressive_panda.png"));
    });

    public PandaEntityRenderer(EntityRenderDispatcher arg) {
        super(arg, new PandaEntityModel(9, 0.0f), 0.9f);
        this.addFeature(new PandaHeldItemFeatureRenderer(this));
    }

    @Override
    public Identifier getTexture(PandaEntity arg) {
        return TEXTURES.getOrDefault((Object)arg.getProductGene(), TEXTURES.get((Object)PandaEntity.Gene.NORMAL));
    }

    @Override
    protected void setupTransforms(PandaEntity arg, MatrixStack arg2, float f, float g, float h) {
        float ad;
        float ab;
        super.setupTransforms(arg, arg2, f, g, h);
        if (arg.playingTicks > 0) {
            float l;
            int i = arg.playingTicks;
            int j = i + 1;
            float k = 7.0f;
            float f2 = l = arg.isBaby() ? 0.3f : 0.8f;
            if (i < 8) {
                float m = (float)(90 * i) / 7.0f;
                float n = (float)(90 * j) / 7.0f;
                float o = this.method_4086(m, n, j, h, 8.0f);
                arg2.translate(0.0, (l + 0.2f) * (o / 90.0f), 0.0);
                arg2.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(-o));
            } else if (i < 16) {
                float p = ((float)i - 8.0f) / 7.0f;
                float q = 90.0f + 90.0f * p;
                float r = 90.0f + 90.0f * ((float)j - 8.0f) / 7.0f;
                float s = this.method_4086(q, r, j, h, 16.0f);
                arg2.translate(0.0, l + 0.2f + (l - 0.2f) * (s - 90.0f) / 90.0f, 0.0);
                arg2.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(-s));
            } else if ((float)i < 24.0f) {
                float t = ((float)i - 16.0f) / 7.0f;
                float u = 180.0f + 90.0f * t;
                float v = 180.0f + 90.0f * ((float)j - 16.0f) / 7.0f;
                float w = this.method_4086(u, v, j, h, 24.0f);
                arg2.translate(0.0, l + l * (270.0f - w) / 90.0f, 0.0);
                arg2.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(-w));
            } else if (i < 32) {
                float x = ((float)i - 24.0f) / 7.0f;
                float y = 270.0f + 90.0f * x;
                float z = 270.0f + 90.0f * ((float)j - 24.0f) / 7.0f;
                float aa = this.method_4086(y, z, j, h, 32.0f);
                arg2.translate(0.0, l * ((360.0f - aa) / 90.0f), 0.0);
                arg2.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(-aa));
            }
        }
        if ((ab = arg.getScaredAnimationProgress(h)) > 0.0f) {
            arg2.translate(0.0, 0.8f * ab, 0.0);
            arg2.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(MathHelper.lerp(ab, arg.pitch, arg.pitch + 90.0f)));
            arg2.translate(0.0, -1.0f * ab, 0.0);
            if (arg.isScaredByThunderstorm()) {
                float ac = (float)(Math.cos((double)arg.age * 1.25) * Math.PI * (double)0.05f);
                arg2.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(ac));
                if (arg.isBaby()) {
                    arg2.translate(0.0, 0.8f, 0.55f);
                }
            }
        }
        if ((ad = arg.getLieOnBackAnimationProgress(h)) > 0.0f) {
            float ae = arg.isBaby() ? 0.5f : 1.3f;
            arg2.translate(0.0, ae * ad, 0.0);
            arg2.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(MathHelper.lerp(ad, arg.pitch, arg.pitch + 180.0f)));
        }
    }

    private float method_4086(float f, float g, int i, float h, float j) {
        if ((float)i < j) {
            return MathHelper.lerp(h, f, g);
        }
        return f;
    }
}

