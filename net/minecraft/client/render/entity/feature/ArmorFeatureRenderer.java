/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity.feature;

import com.google.common.collect.Maps;
import java.util.Map;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.AnimalModel;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.DyeableArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class ArmorFeatureRenderer<T extends LivingEntity, M extends BipedEntityModel<T>, A extends BipedEntityModel<T>>
extends FeatureRenderer<T, M> {
    private static final Map<String, Identifier> ARMOR_TEXTURE_CACHE = Maps.newHashMap();
    private final A leggingsModel;
    private final A bodyModel;

    public ArmorFeatureRenderer(FeatureRendererContext<T, M> context, A leggingsModel, A bodyModel) {
        super(context);
        this.leggingsModel = leggingsModel;
        this.bodyModel = bodyModel;
    }

    @Override
    public void render(MatrixStack arg, VertexConsumerProvider arg2, int i, T arg3, float f, float g, float h, float j, float k, float l) {
        this.renderArmor(arg, arg2, arg3, EquipmentSlot.CHEST, i, this.getArmor(EquipmentSlot.CHEST));
        this.renderArmor(arg, arg2, arg3, EquipmentSlot.LEGS, i, this.getArmor(EquipmentSlot.LEGS));
        this.renderArmor(arg, arg2, arg3, EquipmentSlot.FEET, i, this.getArmor(EquipmentSlot.FEET));
        this.renderArmor(arg, arg2, arg3, EquipmentSlot.HEAD, i, this.getArmor(EquipmentSlot.HEAD));
    }

    private void renderArmor(MatrixStack matrices, VertexConsumerProvider vertexConsumers, T arg3, EquipmentSlot arg4, int i, A arg5) {
        ItemStack lv = ((LivingEntity)arg3).getEquippedStack(arg4);
        if (!(lv.getItem() instanceof ArmorItem)) {
            return;
        }
        ArmorItem lv2 = (ArmorItem)lv.getItem();
        if (lv2.getSlotType() != arg4) {
            return;
        }
        ((BipedEntityModel)this.getContextModel()).setAttributes(arg5);
        this.setVisible(arg5, arg4);
        boolean bl = this.usesSecondLayer(arg4);
        boolean bl2 = lv.hasGlint();
        if (lv2 instanceof DyeableArmorItem) {
            int j = ((DyeableArmorItem)lv2).getColor(lv);
            float f = (float)(j >> 16 & 0xFF) / 255.0f;
            float g = (float)(j >> 8 & 0xFF) / 255.0f;
            float h = (float)(j & 0xFF) / 255.0f;
            this.renderArmorParts(matrices, vertexConsumers, i, lv2, bl2, arg5, bl, f, g, h, null);
            this.renderArmorParts(matrices, vertexConsumers, i, lv2, bl2, arg5, bl, 1.0f, 1.0f, 1.0f, "overlay");
        } else {
            this.renderArmorParts(matrices, vertexConsumers, i, lv2, bl2, arg5, bl, 1.0f, 1.0f, 1.0f, null);
        }
    }

    protected void setVisible(A bipedModel, EquipmentSlot slot) {
        ((BipedEntityModel)bipedModel).setVisible(false);
        switch (slot) {
            case HEAD: {
                bipedModel.head.visible = true;
                bipedModel.helmet.visible = true;
                break;
            }
            case CHEST: {
                bipedModel.torso.visible = true;
                bipedModel.rightArm.visible = true;
                bipedModel.leftArm.visible = true;
                break;
            }
            case LEGS: {
                bipedModel.torso.visible = true;
                bipedModel.rightLeg.visible = true;
                bipedModel.leftLeg.visible = true;
                break;
            }
            case FEET: {
                bipedModel.rightLeg.visible = true;
                bipedModel.leftLeg.visible = true;
            }
        }
    }

    private void renderArmorParts(MatrixStack arg, VertexConsumerProvider arg2, int i, ArmorItem arg3, boolean bl, A arg4, boolean bl2, float f, float g, float h, @Nullable String string) {
        VertexConsumer lv = ItemRenderer.getArmorVertexConsumer(arg2, RenderLayer.getArmorCutoutNoCull(this.getArmorTexture(arg3, bl2, string)), false, bl);
        ((AnimalModel)arg4).render(arg, lv, i, OverlayTexture.DEFAULT_UV, f, g, h, 1.0f);
    }

    private A getArmor(EquipmentSlot slot) {
        return this.usesSecondLayer(slot) ? this.leggingsModel : this.bodyModel;
    }

    private boolean usesSecondLayer(EquipmentSlot slot) {
        return slot == EquipmentSlot.LEGS;
    }

    private Identifier getArmorTexture(ArmorItem arg, boolean bl, @Nullable String string) {
        String string2 = "textures/models/armor/" + arg.getMaterial().getName() + "_layer_" + (bl ? 2 : 1) + (string == null ? "" : "_" + string) + ".png";
        return ARMOR_TEXTURE_CACHE.computeIfAbsent(string2, Identifier::new);
    }
}

