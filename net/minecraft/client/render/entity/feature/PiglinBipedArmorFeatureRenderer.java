/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity.feature;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.feature.ArmorBipedFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class PiglinBipedArmorFeatureRenderer<T extends LivingEntity, M extends BipedEntityModel<T>, A extends BipedEntityModel<T>>
extends ArmorBipedFeatureRenderer<T, M, A> {
    private final A helmetModel;

    public PiglinBipedArmorFeatureRenderer(FeatureRendererContext<T, M> arg, A arg2, A arg3, A arg4) {
        super(arg, arg2, arg3);
        this.helmetModel = arg4;
    }

    @Override
    public A getArmor(EquipmentSlot arg) {
        if (arg == EquipmentSlot.HEAD) {
            return this.helmetModel;
        }
        return super.getArmor(arg);
    }

    @Override
    protected Identifier getArmorTexture(EquipmentSlot arg, ArmorItem arg2, boolean bl, @Nullable String string) {
        if (arg == EquipmentSlot.HEAD) {
            String string2 = string == null ? "" : "_" + string;
            String string3 = "textures/models/armor/" + arg2.getMaterial().getName() + "_piglin_helmet" + string2 + ".png";
            return ARMOR_TEXTURE_CACHE.computeIfAbsent(string3, Identifier::new);
        }
        return super.getArmorTexture(arg, arg2, bl, string);
    }
}

