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
public abstract class ArmorFeatureRenderer<T extends LivingEntity, M extends BipedEntityModel<T>, A extends BipedEntityModel<T>>
extends FeatureRenderer<T, M> {
    protected final A leggingsModel;
    protected final A bodyModel;
    protected static final Map<String, Identifier> ARMOR_TEXTURE_CACHE = Maps.newHashMap();

    protected ArmorFeatureRenderer(FeatureRendererContext<T, M> arg, A arg2, A arg3) {
        super(arg);
        this.leggingsModel = arg2;
        this.bodyModel = arg3;
    }

    @Override
    public void render(MatrixStack arg, VertexConsumerProvider arg2, int i, T arg3, float f, float g, float h, float j, float k, float l) {
        this.renderArmor(arg, arg2, arg3, f, g, h, j, k, l, EquipmentSlot.CHEST, i, this.getArmor(EquipmentSlot.CHEST));
        this.renderArmor(arg, arg2, arg3, f, g, h, j, k, l, EquipmentSlot.LEGS, i, this.getArmor(EquipmentSlot.LEGS));
        this.renderArmor(arg, arg2, arg3, f, g, h, j, k, l, EquipmentSlot.FEET, i, this.getArmor(EquipmentSlot.FEET));
        this.renderArmor(arg, arg2, arg3, f, g, h, j, k, l, EquipmentSlot.HEAD, i, this.getArmor(EquipmentSlot.HEAD));
    }

    private void renderArmor(MatrixStack arg, VertexConsumerProvider arg2, T arg3, float f, float g, float h, float i, float j, float k, EquipmentSlot arg4, int l, A arg5) {
        ItemStack lv = ((LivingEntity)arg3).getEquippedStack(arg4);
        if (!(lv.getItem() instanceof ArmorItem)) {
            return;
        }
        ArmorItem lv2 = (ArmorItem)lv.getItem();
        if (lv2.getSlotType() != arg4) {
            return;
        }
        ((BipedEntityModel)this.getContextModel()).setAttributes(arg5);
        ((BipedEntityModel)arg5).animateModel(arg3, f, g, h);
        this.setVisible(arg5, arg4);
        ((BipedEntityModel)arg5).setAngles(arg3, f, g, i, j, k);
        boolean bl = this.usesSecondLayer(arg4);
        boolean bl2 = lv.hasEnchantmentGlint();
        if (lv2 instanceof DyeableArmorItem) {
            int m = ((DyeableArmorItem)lv2).getColor(lv);
            float n = (float)(m >> 16 & 0xFF) / 255.0f;
            float o = (float)(m >> 8 & 0xFF) / 255.0f;
            float p = (float)(m & 0xFF) / 255.0f;
            this.renderArmorParts(arg4, arg, arg2, l, lv2, bl2, arg5, bl, n, o, p, null);
            this.renderArmorParts(arg4, arg, arg2, l, lv2, bl2, arg5, bl, 1.0f, 1.0f, 1.0f, "overlay");
        } else {
            this.renderArmorParts(arg4, arg, arg2, l, lv2, bl2, arg5, bl, 1.0f, 1.0f, 1.0f, null);
        }
    }

    private void renderArmorParts(EquipmentSlot arg, MatrixStack arg2, VertexConsumerProvider arg3, int i, ArmorItem arg4, boolean bl, A arg5, boolean bl2, float f, float g, float h, @Nullable String string) {
        VertexConsumer lv = ItemRenderer.getArmorVertexConsumer(arg3, RenderLayer.getArmorCutoutNoCull(this.getArmorTexture(arg, arg4, bl2, string)), false, bl);
        ((AnimalModel)arg5).render(arg2, lv, i, OverlayTexture.DEFAULT_UV, f, g, h, 1.0f);
    }

    public A getArmor(EquipmentSlot arg) {
        return this.usesSecondLayer(arg) ? this.leggingsModel : this.bodyModel;
    }

    private boolean usesSecondLayer(EquipmentSlot arg) {
        return arg == EquipmentSlot.LEGS;
    }

    protected Identifier getArmorTexture(EquipmentSlot arg, ArmorItem arg2, boolean bl, @Nullable String string) {
        String string2 = "textures/models/armor/" + arg2.getMaterial().getName() + "_layer_" + (bl ? 2 : 1) + (string == null ? "" : "_" + string) + ".png";
        return ARMOR_TEXTURE_CACHE.computeIfAbsent(string2, Identifier::new);
    }

    protected abstract void setVisible(A var1, EquipmentSlot var2);

    protected abstract void setInvisible(A var1);
}

