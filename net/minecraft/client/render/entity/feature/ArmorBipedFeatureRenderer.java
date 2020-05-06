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
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;

@Environment(value=EnvType.CLIENT)
public class ArmorBipedFeatureRenderer<T extends LivingEntity, M extends BipedEntityModel<T>, A extends BipedEntityModel<T>>
extends ArmorFeatureRenderer<T, M, A> {
    public ArmorBipedFeatureRenderer(FeatureRendererContext<T, M> arg, A arg2, A arg3) {
        super(arg, arg2, arg3);
    }

    @Override
    protected void setVisible(A arg, EquipmentSlot arg2) {
        this.setInvisible(arg);
        switch (arg2) {
            case HEAD: {
                arg.head.visible = true;
                arg.helmet.visible = true;
                break;
            }
            case CHEST: {
                arg.torso.visible = true;
                arg.rightArm.visible = true;
                arg.leftArm.visible = true;
                break;
            }
            case LEGS: {
                arg.torso.visible = true;
                arg.rightLeg.visible = true;
                arg.leftLeg.visible = true;
                break;
            }
            case FEET: {
                arg.rightLeg.visible = true;
                arg.leftLeg.visible = true;
            }
        }
    }

    @Override
    protected void setInvisible(A arg) {
        ((BipedEntityModel)arg).setVisible(false);
    }
}

