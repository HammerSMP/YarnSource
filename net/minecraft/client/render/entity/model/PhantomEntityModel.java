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
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class PhantomEntityModel<T extends Entity>
extends CompositeEntityModel<T> {
    private final ModelPart body;
    private final ModelPart leftWing;
    private final ModelPart leftWingTip;
    private final ModelPart rightWing;
    private final ModelPart rightWingTip;
    private final ModelPart tail;
    private final ModelPart lowerTail;

    public PhantomEntityModel() {
        this.textureWidth = 64;
        this.textureHeight = 64;
        this.body = new ModelPart(this, 0, 8);
        this.body.addCuboid(-3.0f, -2.0f, -8.0f, 5.0f, 3.0f, 9.0f);
        this.tail = new ModelPart(this, 3, 20);
        this.tail.addCuboid(-2.0f, 0.0f, 0.0f, 3.0f, 2.0f, 6.0f);
        this.tail.setPivot(0.0f, -2.0f, 1.0f);
        this.body.addChild(this.tail);
        this.lowerTail = new ModelPart(this, 4, 29);
        this.lowerTail.addCuboid(-1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 6.0f);
        this.lowerTail.setPivot(0.0f, 0.5f, 6.0f);
        this.tail.addChild(this.lowerTail);
        this.leftWing = new ModelPart(this, 23, 12);
        this.leftWing.addCuboid(0.0f, 0.0f, 0.0f, 6.0f, 2.0f, 9.0f);
        this.leftWing.setPivot(2.0f, -2.0f, -8.0f);
        this.leftWingTip = new ModelPart(this, 16, 24);
        this.leftWingTip.addCuboid(0.0f, 0.0f, 0.0f, 13.0f, 1.0f, 9.0f);
        this.leftWingTip.setPivot(6.0f, 0.0f, 0.0f);
        this.leftWing.addChild(this.leftWingTip);
        this.rightWing = new ModelPart(this, 23, 12);
        this.rightWing.mirror = true;
        this.rightWing.addCuboid(-6.0f, 0.0f, 0.0f, 6.0f, 2.0f, 9.0f);
        this.rightWing.setPivot(-3.0f, -2.0f, -8.0f);
        this.rightWingTip = new ModelPart(this, 16, 24);
        this.rightWingTip.mirror = true;
        this.rightWingTip.addCuboid(-13.0f, 0.0f, 0.0f, 13.0f, 1.0f, 9.0f);
        this.rightWingTip.setPivot(-6.0f, 0.0f, 0.0f);
        this.rightWing.addChild(this.rightWingTip);
        this.leftWing.roll = 0.1f;
        this.leftWingTip.roll = 0.1f;
        this.rightWing.roll = -0.1f;
        this.rightWingTip.roll = -0.1f;
        this.body.pitch = -0.1f;
        ModelPart lv = new ModelPart(this, 0, 0);
        lv.addCuboid(-4.0f, -2.0f, -5.0f, 7.0f, 3.0f, 5.0f);
        lv.setPivot(0.0f, 1.0f, -7.0f);
        lv.pitch = 0.2f;
        this.body.addChild(lv);
        this.body.addChild(this.leftWing);
        this.body.addChild(this.rightWing);
    }

    @Override
    public Iterable<ModelPart> getParts() {
        return ImmutableList.of((Object)this.body);
    }

    @Override
    public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        float k = ((float)(((Entity)entity).getEntityId() * 3) + animationProgress) * 0.13f;
        float l = 16.0f;
        this.leftWing.roll = MathHelper.cos(k) * 16.0f * ((float)Math.PI / 180);
        this.leftWingTip.roll = MathHelper.cos(k) * 16.0f * ((float)Math.PI / 180);
        this.rightWing.roll = -this.leftWing.roll;
        this.rightWingTip.roll = -this.leftWingTip.roll;
        this.tail.pitch = -(5.0f + MathHelper.cos(k * 2.0f) * 5.0f) * ((float)Math.PI / 180);
        this.lowerTail.pitch = -(5.0f + MathHelper.cos(k * 2.0f) * 5.0f) * ((float)Math.PI / 180);
    }
}

