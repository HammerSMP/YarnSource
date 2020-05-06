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
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.decoration.ArmorStandEntity;

@Environment(value=EnvType.CLIENT)
public class ArmorStandArmorEntityModel
extends BipedEntityModel<ArmorStandEntity> {
    public ArmorStandArmorEntityModel(float f) {
        this(f, 64, 32);
    }

    protected ArmorStandArmorEntityModel(float f, int i, int j) {
        super(f, 0.0f, i, j);
    }

    @Override
    public void setAngles(ArmorStandEntity arg, float f, float g, float h, float i, float j) {
        this.head.pitch = (float)Math.PI / 180 * arg.getHeadRotation().getPitch();
        this.head.yaw = (float)Math.PI / 180 * arg.getHeadRotation().getYaw();
        this.head.roll = (float)Math.PI / 180 * arg.getHeadRotation().getRoll();
        this.head.setPivot(0.0f, 1.0f, 0.0f);
        this.torso.pitch = (float)Math.PI / 180 * arg.getBodyRotation().getPitch();
        this.torso.yaw = (float)Math.PI / 180 * arg.getBodyRotation().getYaw();
        this.torso.roll = (float)Math.PI / 180 * arg.getBodyRotation().getRoll();
        this.leftArm.pitch = (float)Math.PI / 180 * arg.getLeftArmRotation().getPitch();
        this.leftArm.yaw = (float)Math.PI / 180 * arg.getLeftArmRotation().getYaw();
        this.leftArm.roll = (float)Math.PI / 180 * arg.getLeftArmRotation().getRoll();
        this.rightArm.pitch = (float)Math.PI / 180 * arg.getRightArmRotation().getPitch();
        this.rightArm.yaw = (float)Math.PI / 180 * arg.getRightArmRotation().getYaw();
        this.rightArm.roll = (float)Math.PI / 180 * arg.getRightArmRotation().getRoll();
        this.leftLeg.pitch = (float)Math.PI / 180 * arg.getLeftLegRotation().getPitch();
        this.leftLeg.yaw = (float)Math.PI / 180 * arg.getLeftLegRotation().getYaw();
        this.leftLeg.roll = (float)Math.PI / 180 * arg.getLeftLegRotation().getRoll();
        this.leftLeg.setPivot(1.9f, 11.0f, 0.0f);
        this.rightLeg.pitch = (float)Math.PI / 180 * arg.getRightLegRotation().getPitch();
        this.rightLeg.yaw = (float)Math.PI / 180 * arg.getRightLegRotation().getYaw();
        this.rightLeg.roll = (float)Math.PI / 180 * arg.getRightLegRotation().getRoll();
        this.rightLeg.setPivot(-1.9f, 11.0f, 0.0f);
        this.helmet.copyPositionAndRotation(this.head);
    }
}

