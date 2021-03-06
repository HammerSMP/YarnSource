/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.CompositeEntityModel;
import net.minecraft.entity.passive.BatEntity;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class BatEntityModel
extends CompositeEntityModel<BatEntity> {
    private final ModelPart head;
    private final ModelPart body;
    private final ModelPart rightWing;
    private final ModelPart leftWing;
    private final ModelPart rightWingTip;
    private final ModelPart leftWingTip;

    public BatEntityModel() {
        this.textureWidth = 64;
        this.textureHeight = 64;
        this.head = new ModelPart(this, 0, 0);
        this.head.addCuboid(-3.0f, -3.0f, -3.0f, 6.0f, 6.0f, 6.0f);
        ModelPart lv = new ModelPart(this, 24, 0);
        lv.addCuboid(-4.0f, -6.0f, -2.0f, 3.0f, 4.0f, 1.0f);
        this.head.addChild(lv);
        ModelPart lv2 = new ModelPart(this, 24, 0);
        lv2.mirror = true;
        lv2.addCuboid(1.0f, -6.0f, -2.0f, 3.0f, 4.0f, 1.0f);
        this.head.addChild(lv2);
        this.body = new ModelPart(this, 0, 16);
        this.body.addCuboid(-3.0f, 4.0f, -3.0f, 6.0f, 12.0f, 6.0f);
        this.body.setTextureOffset(0, 34).addCuboid(-5.0f, 16.0f, 0.0f, 10.0f, 6.0f, 1.0f);
        this.rightWing = new ModelPart(this, 42, 0);
        this.rightWing.addCuboid(-12.0f, 1.0f, 1.5f, 10.0f, 16.0f, 1.0f);
        this.rightWingTip = new ModelPart(this, 24, 16);
        this.rightWingTip.setPivot(-12.0f, 1.0f, 1.5f);
        this.rightWingTip.addCuboid(-8.0f, 1.0f, 0.0f, 8.0f, 12.0f, 1.0f);
        this.leftWing = new ModelPart(this, 42, 0);
        this.leftWing.mirror = true;
        this.leftWing.addCuboid(2.0f, 1.0f, 1.5f, 10.0f, 16.0f, 1.0f);
        this.leftWingTip = new ModelPart(this, 24, 16);
        this.leftWingTip.mirror = true;
        this.leftWingTip.setPivot(12.0f, 1.0f, 1.5f);
        this.leftWingTip.addCuboid(0.0f, 1.0f, 0.0f, 8.0f, 12.0f, 1.0f);
        this.body.addChild(this.rightWing);
        this.body.addChild(this.leftWing);
        this.rightWing.addChild(this.rightWingTip);
        this.leftWing.addChild(this.leftWingTip);
    }

    @Override
    public Iterable<ModelPart> getParts() {
        return ImmutableList.of((Object)this.head, (Object)this.body);
    }

    @Override
    public void setAngles(BatEntity arg, float f, float g, float h, float i, float j) {
        if (arg.isRoosting()) {
            this.head.pitch = j * ((float)Math.PI / 180);
            this.head.yaw = (float)Math.PI - i * ((float)Math.PI / 180);
            this.head.roll = (float)Math.PI;
            this.head.setPivot(0.0f, -2.0f, 0.0f);
            this.rightWing.setPivot(-3.0f, 0.0f, 3.0f);
            this.leftWing.setPivot(3.0f, 0.0f, 3.0f);
            this.body.pitch = (float)Math.PI;
            this.rightWing.pitch = -0.15707964f;
            this.rightWing.yaw = -1.2566371f;
            this.rightWingTip.yaw = -1.7278761f;
            this.leftWing.pitch = this.rightWing.pitch;
            this.leftWing.yaw = -this.rightWing.yaw;
            this.leftWingTip.yaw = -this.rightWingTip.yaw;
        } else {
            this.head.pitch = j * ((float)Math.PI / 180);
            this.head.yaw = i * ((float)Math.PI / 180);
            this.head.roll = 0.0f;
            this.head.setPivot(0.0f, 0.0f, 0.0f);
            this.rightWing.setPivot(0.0f, 0.0f, 0.0f);
            this.leftWing.setPivot(0.0f, 0.0f, 0.0f);
            this.body.pitch = 0.7853982f + MathHelper.cos(h * 0.1f) * 0.15f;
            this.body.yaw = 0.0f;
            this.rightWing.yaw = MathHelper.cos(h * 1.3f) * (float)Math.PI * 0.25f;
            this.leftWing.yaw = -this.rightWing.yaw;
            this.rightWingTip.yaw = this.rightWing.yaw * 0.5f;
            this.leftWingTip.yaw = -this.rightWing.yaw * 0.5f;
        }
    }
}

