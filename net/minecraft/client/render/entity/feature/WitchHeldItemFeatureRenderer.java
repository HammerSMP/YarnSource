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
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.VillagerHeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.WitchEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

@Environment(value=EnvType.CLIENT)
public class WitchHeldItemFeatureRenderer<T extends LivingEntity>
extends VillagerHeldItemFeatureRenderer<T, WitchEntityModel<T>> {
    public WitchHeldItemFeatureRenderer(FeatureRendererContext<T, WitchEntityModel<T>> arg) {
        super(arg);
    }

    @Override
    public void render(MatrixStack arg, VertexConsumerProvider arg2, int i, T arg3, float f, float g, float h, float j, float k, float l) {
        ItemStack lv = ((LivingEntity)arg3).getMainHandStack();
        arg.push();
        if (lv.getItem() == Items.POTION) {
            ((WitchEntityModel)this.getContextModel()).getHead().rotate(arg);
            ((WitchEntityModel)this.getContextModel()).getNose().rotate(arg);
            arg.translate(0.0625, 0.25, 0.0);
            arg.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(180.0f));
            arg.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(140.0f));
            arg.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(10.0f));
            arg.translate(0.0, -0.4f, 0.4f);
        }
        super.render(arg, arg2, i, arg3, f, g, h, j, k, l);
        arg.pop();
    }
}

