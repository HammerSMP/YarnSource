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
public class DolphinEntityModel<T extends Entity>
extends CompositeEntityModel<T> {
    private final ModelPart body;
    private final ModelPart tail;
    private final ModelPart flukes;

    public DolphinEntityModel() {
        this.textureWidth = 64;
        this.textureHeight = 64;
        float f = 18.0f;
        float g = -8.0f;
        this.body = new ModelPart(this, 22, 0);
        this.body.addCuboid(-4.0f, -7.0f, 0.0f, 8.0f, 7.0f, 13.0f);
        this.body.setPivot(0.0f, 22.0f, -5.0f);
        ModelPart lv = new ModelPart(this, 51, 0);
        lv.addCuboid(-0.5f, 0.0f, 8.0f, 1.0f, 4.0f, 5.0f);
        lv.pitch = 1.0471976f;
        this.body.addChild(lv);
        ModelPart lv2 = new ModelPart(this, 48, 20);
        lv2.mirror = true;
        lv2.addCuboid(-0.5f, -4.0f, 0.0f, 1.0f, 4.0f, 7.0f);
        lv2.setPivot(2.0f, -2.0f, 4.0f);
        lv2.pitch = 1.0471976f;
        lv2.roll = 2.0943952f;
        this.body.addChild(lv2);
        ModelPart lv3 = new ModelPart(this, 48, 20);
        lv3.addCuboid(-0.5f, -4.0f, 0.0f, 1.0f, 4.0f, 7.0f);
        lv3.setPivot(-2.0f, -2.0f, 4.0f);
        lv3.pitch = 1.0471976f;
        lv3.roll = -2.0943952f;
        this.body.addChild(lv3);
        this.tail = new ModelPart(this, 0, 19);
        this.tail.addCuboid(-2.0f, -2.5f, 0.0f, 4.0f, 5.0f, 11.0f);
        this.tail.setPivot(0.0f, -2.5f, 11.0f);
        this.tail.pitch = -0.10471976f;
        this.body.addChild(this.tail);
        this.flukes = new ModelPart(this, 19, 20);
        this.flukes.addCuboid(-5.0f, -0.5f, 0.0f, 10.0f, 1.0f, 6.0f);
        this.flukes.setPivot(0.0f, 0.0f, 9.0f);
        this.flukes.pitch = 0.0f;
        this.tail.addChild(this.flukes);
        ModelPart lv4 = new ModelPart(this, 0, 0);
        lv4.addCuboid(-4.0f, -3.0f, -3.0f, 8.0f, 7.0f, 6.0f);
        lv4.setPivot(0.0f, -4.0f, -3.0f);
        ModelPart lv5 = new ModelPart(this, 0, 13);
        lv5.addCuboid(-1.0f, 2.0f, -7.0f, 2.0f, 2.0f, 4.0f);
        lv4.addChild(lv5);
        this.body.addChild(lv4);
    }

    @Override
    public Iterable<ModelPart> getParts() {
        return ImmutableList.of((Object)this.body);
    }

    @Override
    public void setAngles(T arg, float f, float g, float h, float i, float j) {
        this.body.pitch = j * ((float)Math.PI / 180);
        this.body.yaw = i * ((float)Math.PI / 180);
        if (Entity.squaredHorizontalLength(((Entity)arg).getVelocity()) > 1.0E-7) {
            this.body.pitch += -0.05f + -0.05f * MathHelper.cos(h * 0.3f);
            this.tail.pitch = -0.1f * MathHelper.cos(h * 0.3f);
            this.flukes.pitch = -0.2f * MathHelper.cos(h * 0.3f);
        }
    }
}

