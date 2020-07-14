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
public class SalmonEntityModel<T extends Entity>
extends CompositeEntityModel<T> {
    private final ModelPart torso;
    private final ModelPart tail;
    private final ModelPart head;
    private final ModelPart rightFin;
    private final ModelPart leftFin;

    public SalmonEntityModel() {
        this.textureWidth = 32;
        this.textureHeight = 32;
        int i = 20;
        this.torso = new ModelPart(this, 0, 0);
        this.torso.addCuboid(-1.5f, -2.5f, 0.0f, 3.0f, 5.0f, 8.0f);
        this.torso.setPivot(0.0f, 20.0f, 0.0f);
        this.tail = new ModelPart(this, 0, 13);
        this.tail.addCuboid(-1.5f, -2.5f, 0.0f, 3.0f, 5.0f, 8.0f);
        this.tail.setPivot(0.0f, 20.0f, 8.0f);
        this.head = new ModelPart(this, 22, 0);
        this.head.addCuboid(-1.0f, -2.0f, -3.0f, 2.0f, 4.0f, 3.0f);
        this.head.setPivot(0.0f, 20.0f, 0.0f);
        ModelPart lv = new ModelPart(this, 20, 10);
        lv.addCuboid(0.0f, -2.5f, 0.0f, 0.0f, 5.0f, 6.0f);
        lv.setPivot(0.0f, 0.0f, 8.0f);
        this.tail.addChild(lv);
        ModelPart lv2 = new ModelPart(this, 2, 1);
        lv2.addCuboid(0.0f, 0.0f, 0.0f, 0.0f, 2.0f, 3.0f);
        lv2.setPivot(0.0f, -4.5f, 5.0f);
        this.torso.addChild(lv2);
        ModelPart lv3 = new ModelPart(this, 0, 2);
        lv3.addCuboid(0.0f, 0.0f, 0.0f, 0.0f, 2.0f, 4.0f);
        lv3.setPivot(0.0f, -4.5f, -1.0f);
        this.tail.addChild(lv3);
        this.rightFin = new ModelPart(this, -4, 0);
        this.rightFin.addCuboid(-2.0f, 0.0f, 0.0f, 2.0f, 0.0f, 2.0f);
        this.rightFin.setPivot(-1.5f, 21.5f, 0.0f);
        this.rightFin.roll = -0.7853982f;
        this.leftFin = new ModelPart(this, 0, 0);
        this.leftFin.addCuboid(0.0f, 0.0f, 0.0f, 2.0f, 0.0f, 2.0f);
        this.leftFin.setPivot(1.5f, 21.5f, 0.0f);
        this.leftFin.roll = 0.7853982f;
    }

    @Override
    public Iterable<ModelPart> getParts() {
        return ImmutableList.of((Object)this.torso, (Object)this.tail, (Object)this.head, (Object)this.rightFin, (Object)this.leftFin);
    }

    @Override
    public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        float k = 1.0f;
        float l = 1.0f;
        if (!((Entity)entity).isTouchingWater()) {
            k = 1.3f;
            l = 1.7f;
        }
        this.tail.yaw = -k * 0.25f * MathHelper.sin(l * 0.6f * animationProgress);
    }
}

