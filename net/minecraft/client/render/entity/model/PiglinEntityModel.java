/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.CrossbowPosing;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PiglinEntity;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class PiglinEntityModel<T extends MobEntity>
extends PlayerEntityModel<T> {
    public final ModelPart rightEar;
    public final ModelPart leftEar;

    public PiglinEntityModel(float f, int i, int j) {
        super(f, false);
        this.textureWidth = i;
        this.textureHeight = j;
        this.torso = new ModelPart(this, 16, 16);
        this.torso.addCuboid(-4.0f, 0.0f, -2.0f, 8.0f, 12.0f, 4.0f, f);
        this.head = new ModelPart(this);
        this.head.setTextureOffset(0, 0).addCuboid(-5.0f, -8.0f, -4.0f, 10.0f, 8.0f, 8.0f, f);
        this.head.setTextureOffset(31, 1).addCuboid(-2.0f, -4.0f, -5.0f, 4.0f, 4.0f, 1.0f, f);
        this.head.setTextureOffset(2, 4).addCuboid(2.0f, -2.0f, -5.0f, 1.0f, 2.0f, 1.0f, f);
        this.head.setTextureOffset(2, 0).addCuboid(-3.0f, -2.0f, -5.0f, 1.0f, 2.0f, 1.0f, f);
        this.rightEar = new ModelPart(this);
        this.rightEar.setPivot(4.5f, -6.0f, 0.0f);
        this.rightEar.setTextureOffset(51, 6).addCuboid(0.0f, 0.0f, -2.0f, 1.0f, 5.0f, 4.0f, f);
        this.head.addChild(this.rightEar);
        this.leftEar = new ModelPart(this);
        this.leftEar.setPivot(-4.5f, -6.0f, 0.0f);
        this.leftEar.setTextureOffset(39, 6).addCuboid(-1.0f, 0.0f, -2.0f, 1.0f, 5.0f, 4.0f, f);
        this.head.addChild(this.leftEar);
        this.helmet = new ModelPart(this);
    }

    @Override
    public void setAngles(T arg, float f, float g, float h, float i, float j) {
        super.setAngles(arg, f, g, h, i, j);
        float k = 0.5235988f;
        float l = h * 0.1f + f * 0.5f;
        float m = 0.08f + g * 0.4f;
        this.rightEar.roll = -0.5235988f - MathHelper.cos(l * 1.2f) * m;
        this.leftEar.roll = 0.5235988f + MathHelper.cos(l) * m;
        if (arg instanceof PiglinEntity) {
            PiglinEntity lv = (PiglinEntity)arg;
            PiglinEntity.Activity lv2 = lv.getActivity();
            if (lv2 == PiglinEntity.Activity.CROSSBOW_HOLD) {
                CrossbowPosing.hold(this.rightArm, this.leftArm, this.head, !((MobEntity)arg).isLeftHanded());
            } else if (lv2 == PiglinEntity.Activity.CROSSBOW_CHARGE) {
                CrossbowPosing.charge(this.rightArm, this.leftArm, arg, !((MobEntity)arg).isLeftHanded());
            } else if (lv2 == PiglinEntity.Activity.ADMIRING_ITEM) {
                this.head.pitch = 0.5f;
                this.head.yaw = 0.0f;
                if (((MobEntity)arg).isLeftHanded()) {
                    this.rightArm.yaw = -0.5f;
                    this.rightArm.pitch = -0.9f;
                } else {
                    this.leftArm.yaw = 0.5f;
                    this.leftArm.pitch = -0.9f;
                }
            }
        }
    }
}

