/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Iterables
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.ArmorStandArmorEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.util.Arm;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class ArmorStandEntityModel
extends ArmorStandArmorEntityModel {
    private final ModelPart rightTorso;
    private final ModelPart leftTorso;
    private final ModelPart hip;
    private final ModelPart plate;

    public ArmorStandEntityModel() {
        this(0.0f);
    }

    public ArmorStandEntityModel(float f) {
        super(f, 64, 64);
        this.head = new ModelPart(this, 0, 0);
        this.head.addCuboid(-1.0f, -7.0f, -1.0f, 2.0f, 7.0f, 2.0f, f);
        this.head.setPivot(0.0f, 0.0f, 0.0f);
        this.torso = new ModelPart(this, 0, 26);
        this.torso.addCuboid(-6.0f, 0.0f, -1.5f, 12.0f, 3.0f, 3.0f, f);
        this.torso.setPivot(0.0f, 0.0f, 0.0f);
        this.rightArm = new ModelPart(this, 24, 0);
        this.rightArm.addCuboid(-2.0f, -2.0f, -1.0f, 2.0f, 12.0f, 2.0f, f);
        this.rightArm.setPivot(-5.0f, 2.0f, 0.0f);
        this.leftArm = new ModelPart(this, 32, 16);
        this.leftArm.mirror = true;
        this.leftArm.addCuboid(0.0f, -2.0f, -1.0f, 2.0f, 12.0f, 2.0f, f);
        this.leftArm.setPivot(5.0f, 2.0f, 0.0f);
        this.rightLeg = new ModelPart(this, 8, 0);
        this.rightLeg.addCuboid(-1.0f, 0.0f, -1.0f, 2.0f, 11.0f, 2.0f, f);
        this.rightLeg.setPivot(-1.9f, 12.0f, 0.0f);
        this.leftLeg = new ModelPart(this, 40, 16);
        this.leftLeg.mirror = true;
        this.leftLeg.addCuboid(-1.0f, 0.0f, -1.0f, 2.0f, 11.0f, 2.0f, f);
        this.leftLeg.setPivot(1.9f, 12.0f, 0.0f);
        this.rightTorso = new ModelPart(this, 16, 0);
        this.rightTorso.addCuboid(-3.0f, 3.0f, -1.0f, 2.0f, 7.0f, 2.0f, f);
        this.rightTorso.setPivot(0.0f, 0.0f, 0.0f);
        this.rightTorso.visible = true;
        this.leftTorso = new ModelPart(this, 48, 16);
        this.leftTorso.addCuboid(1.0f, 3.0f, -1.0f, 2.0f, 7.0f, 2.0f, f);
        this.leftTorso.setPivot(0.0f, 0.0f, 0.0f);
        this.hip = new ModelPart(this, 0, 48);
        this.hip.addCuboid(-4.0f, 10.0f, -1.0f, 8.0f, 2.0f, 2.0f, f);
        this.hip.setPivot(0.0f, 0.0f, 0.0f);
        this.plate = new ModelPart(this, 0, 32);
        this.plate.addCuboid(-6.0f, 11.0f, -6.0f, 12.0f, 1.0f, 12.0f, f);
        this.plate.setPivot(0.0f, 12.0f, 0.0f);
        this.helmet.visible = false;
    }

    @Override
    public void animateModel(ArmorStandEntity arg, float f, float g, float h) {
        this.plate.pitch = 0.0f;
        this.plate.yaw = (float)Math.PI / 180 * -MathHelper.lerpAngleDegrees(h, arg.prevYaw, arg.yaw);
        this.plate.roll = 0.0f;
    }

    @Override
    public void setAngles(ArmorStandEntity arg, float f, float g, float h, float i, float j) {
        super.setAngles(arg, f, g, h, i, j);
        this.leftArm.visible = arg.shouldShowArms();
        this.rightArm.visible = arg.shouldShowArms();
        this.plate.visible = !arg.shouldHideBasePlate();
        this.leftLeg.setPivot(1.9f, 12.0f, 0.0f);
        this.rightLeg.setPivot(-1.9f, 12.0f, 0.0f);
        this.rightTorso.pitch = (float)Math.PI / 180 * arg.getBodyRotation().getPitch();
        this.rightTorso.yaw = (float)Math.PI / 180 * arg.getBodyRotation().getYaw();
        this.rightTorso.roll = (float)Math.PI / 180 * arg.getBodyRotation().getRoll();
        this.leftTorso.pitch = (float)Math.PI / 180 * arg.getBodyRotation().getPitch();
        this.leftTorso.yaw = (float)Math.PI / 180 * arg.getBodyRotation().getYaw();
        this.leftTorso.roll = (float)Math.PI / 180 * arg.getBodyRotation().getRoll();
        this.hip.pitch = (float)Math.PI / 180 * arg.getBodyRotation().getPitch();
        this.hip.yaw = (float)Math.PI / 180 * arg.getBodyRotation().getYaw();
        this.hip.roll = (float)Math.PI / 180 * arg.getBodyRotation().getRoll();
    }

    @Override
    protected Iterable<ModelPart> getBodyParts() {
        return Iterables.concat(super.getBodyParts(), (Iterable)ImmutableList.of((Object)this.rightTorso, (Object)this.leftTorso, (Object)this.hip, (Object)this.plate));
    }

    @Override
    public void setArmAngle(Arm arm, MatrixStack matrices) {
        ModelPart lv = this.getArm(arm);
        boolean bl = lv.visible;
        lv.visible = true;
        super.setArmAngle(arm, matrices);
        lv.visible = bl;
    }
}

