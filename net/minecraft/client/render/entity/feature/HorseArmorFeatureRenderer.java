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
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.HorseEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.HorseEntity;
import net.minecraft.item.DyeableHorseArmorItem;
import net.minecraft.item.HorseArmorItem;
import net.minecraft.item.ItemStack;

@Environment(value=EnvType.CLIENT)
public class HorseArmorFeatureRenderer
extends FeatureRenderer<HorseEntity, HorseEntityModel<HorseEntity>> {
    private final HorseEntityModel<HorseEntity> model = new HorseEntityModel(0.1f);

    public HorseArmorFeatureRenderer(FeatureRendererContext<HorseEntity, HorseEntityModel<HorseEntity>> arg) {
        super(arg);
    }

    @Override
    public void render(MatrixStack arg, VertexConsumerProvider arg2, int i, HorseEntity arg3, float f, float g, float h, float j, float k, float l) {
        float s;
        float r;
        float q;
        ItemStack lv = arg3.getArmorType();
        if (!(lv.getItem() instanceof HorseArmorItem)) {
            return;
        }
        HorseArmorItem lv2 = (HorseArmorItem)lv.getItem();
        ((HorseEntityModel)this.getContextModel()).copyStateTo(this.model);
        this.model.animateModel(arg3, f, g, h);
        this.model.setAngles(arg3, f, g, j, k, l);
        if (lv2 instanceof DyeableHorseArmorItem) {
            int m = ((DyeableHorseArmorItem)lv2).getColor(lv);
            float n = (float)(m >> 16 & 0xFF) / 255.0f;
            float o = (float)(m >> 8 & 0xFF) / 255.0f;
            float p = (float)(m & 0xFF) / 255.0f;
        } else {
            q = 1.0f;
            r = 1.0f;
            s = 1.0f;
        }
        VertexConsumer lv3 = arg2.getBuffer(RenderLayer.getEntityCutoutNoCull(lv2.getEntityTexture()));
        this.model.render(arg, lv3, i, OverlayTexture.DEFAULT_UV, q, r, s, 1.0f);
    }
}

