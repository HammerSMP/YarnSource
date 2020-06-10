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
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.ElytraEntityModel;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class ElytraFeatureRenderer<T extends LivingEntity, M extends EntityModel<T>>
extends FeatureRenderer<T, M> {
    private static final Identifier SKIN = new Identifier("textures/entity/elytra.png");
    private final ElytraEntityModel<T> elytra = new ElytraEntityModel();

    public ElytraFeatureRenderer(FeatureRendererContext<T, M> arg) {
        super(arg);
    }

    @Override
    public void render(MatrixStack arg, VertexConsumerProvider arg2, int i, T arg3, float f, float g, float h, float j, float k, float l) {
        Identifier lv6;
        ItemStack lv = ((LivingEntity)arg3).getEquippedStack(EquipmentSlot.CHEST);
        if (lv.getItem() != Items.ELYTRA) {
            return;
        }
        if (arg3 instanceof AbstractClientPlayerEntity) {
            AbstractClientPlayerEntity lv2 = (AbstractClientPlayerEntity)arg3;
            if (lv2.canRenderElytraTexture() && lv2.getElytraTexture() != null) {
                Identifier lv3 = lv2.getElytraTexture();
            } else if (lv2.canRenderCapeTexture() && lv2.getCapeTexture() != null && lv2.isPartVisible(PlayerModelPart.CAPE)) {
                Identifier lv4 = lv2.getCapeTexture();
            } else {
                Identifier lv5 = SKIN;
            }
        } else {
            lv6 = SKIN;
        }
        arg.push();
        arg.translate(0.0, 0.0, 0.125);
        ((EntityModel)this.getContextModel()).copyStateTo(this.elytra);
        this.elytra.setAngles(arg3, f, g, j, k, l);
        VertexConsumer lv7 = ItemRenderer.method_29711(arg2, this.elytra.getLayer(lv6), false, lv.hasEnchantmentGlint());
        this.elytra.render(arg, lv7, i, OverlayTexture.DEFAULT_UV, 1.0f, 1.0f, 1.0f, 1.0f);
        arg.pop();
    }
}

