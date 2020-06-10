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
public class LargePufferfishEntityModel<T extends Entity>
extends CompositeEntityModel<T> {
    private final ModelPart field_3493;
    private final ModelPart field_3499;
    private final ModelPart field_3494;
    private final ModelPart field_3490;
    private final ModelPart field_3496;
    private final ModelPart field_3495;
    private final ModelPart field_3489;
    private final ModelPart field_3497;
    private final ModelPart field_3491;
    private final ModelPart field_3487;
    private final ModelPart field_3492;
    private final ModelPart field_3498;
    private final ModelPart field_3488;

    public LargePufferfishEntityModel() {
        this.textureWidth = 32;
        this.textureHeight = 32;
        int i = 22;
        this.field_3493 = new ModelPart(this, 0, 0);
        this.field_3493.addCuboid(-4.0f, -8.0f, -4.0f, 8.0f, 8.0f, 8.0f);
        this.field_3493.setPivot(0.0f, 22.0f, 0.0f);
        this.field_3499 = new ModelPart(this, 24, 0);
        this.field_3499.addCuboid(-2.0f, 0.0f, -1.0f, 2.0f, 1.0f, 2.0f);
        this.field_3499.setPivot(-4.0f, 15.0f, -2.0f);
        this.field_3494 = new ModelPart(this, 24, 3);
        this.field_3494.addCuboid(0.0f, 0.0f, -1.0f, 2.0f, 1.0f, 2.0f);
        this.field_3494.setPivot(4.0f, 15.0f, -2.0f);
        this.field_3490 = new ModelPart(this, 15, 17);
        this.field_3490.addCuboid(-4.0f, -1.0f, 0.0f, 8.0f, 1.0f, 0.0f);
        this.field_3490.setPivot(0.0f, 14.0f, -4.0f);
        this.field_3490.pitch = 0.7853982f;
        this.field_3496 = new ModelPart(this, 14, 16);
        this.field_3496.addCuboid(-4.0f, -1.0f, 0.0f, 8.0f, 1.0f, 1.0f);
        this.field_3496.setPivot(0.0f, 14.0f, 0.0f);
        this.field_3495 = new ModelPart(this, 23, 18);
        this.field_3495.addCuboid(-4.0f, -1.0f, 0.0f, 8.0f, 1.0f, 0.0f);
        this.field_3495.setPivot(0.0f, 14.0f, 4.0f);
        this.field_3495.pitch = -0.7853982f;
        this.field_3489 = new ModelPart(this, 5, 17);
        this.field_3489.addCuboid(-1.0f, -8.0f, 0.0f, 1.0f, 8.0f, 0.0f);
        this.field_3489.setPivot(-4.0f, 22.0f, -4.0f);
        this.field_3489.yaw = -0.7853982f;
        this.field_3497 = new ModelPart(this, 1, 17);
        this.field_3497.addCuboid(0.0f, -8.0f, 0.0f, 1.0f, 8.0f, 0.0f);
        this.field_3497.setPivot(4.0f, 22.0f, -4.0f);
        this.field_3497.yaw = 0.7853982f;
        this.field_3491 = new ModelPart(this, 15, 20);
        this.field_3491.addCuboid(-4.0f, 0.0f, 0.0f, 8.0f, 1.0f, 0.0f);
        this.field_3491.setPivot(0.0f, 22.0f, -4.0f);
        this.field_3491.pitch = -0.7853982f;
        this.field_3492 = new ModelPart(this, 15, 20);
        this.field_3492.addCuboid(-4.0f, 0.0f, 0.0f, 8.0f, 1.0f, 0.0f);
        this.field_3492.setPivot(0.0f, 22.0f, 0.0f);
        this.field_3487 = new ModelPart(this, 15, 20);
        this.field_3487.addCuboid(-4.0f, 0.0f, 0.0f, 8.0f, 1.0f, 0.0f);
        this.field_3487.setPivot(0.0f, 22.0f, 4.0f);
        this.field_3487.pitch = 0.7853982f;
        this.field_3498 = new ModelPart(this, 9, 17);
        this.field_3498.addCuboid(-1.0f, -8.0f, 0.0f, 1.0f, 8.0f, 0.0f);
        this.field_3498.setPivot(-4.0f, 22.0f, 4.0f);
        this.field_3498.yaw = 0.7853982f;
        this.field_3488 = new ModelPart(this, 9, 17);
        this.field_3488.addCuboid(0.0f, -8.0f, 0.0f, 1.0f, 8.0f, 0.0f);
        this.field_3488.setPivot(4.0f, 22.0f, 4.0f);
        this.field_3488.yaw = -0.7853982f;
    }

    @Override
    public Iterable<ModelPart> getParts() {
        return ImmutableList.of((Object)this.field_3493, (Object)this.field_3499, (Object)this.field_3494, (Object)this.field_3490, (Object)this.field_3496, (Object)this.field_3495, (Object)this.field_3489, (Object)this.field_3497, (Object)this.field_3491, (Object)this.field_3492, (Object)this.field_3487, (Object)this.field_3498, (Object[])new ModelPart[]{this.field_3488});
    }

    @Override
    public void setAngles(T arg, float f, float g, float h, float i, float j) {
        this.field_3499.roll = -0.2f + 0.4f * MathHelper.sin(h * 0.2f);
        this.field_3494.roll = 0.2f - 0.4f * MathHelper.sin(h * 0.2f);
    }
}

