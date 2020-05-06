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
public class EvokerFangsEntityModel<T extends Entity>
extends CompositeEntityModel<T> {
    private final ModelPart field_3374 = new ModelPart(this, 0, 0);
    private final ModelPart field_3376;
    private final ModelPart field_3375;

    public EvokerFangsEntityModel() {
        this.field_3374.setPivot(-5.0f, 22.0f, -5.0f);
        this.field_3374.addCuboid(0.0f, 0.0f, 0.0f, 10.0f, 12.0f, 10.0f);
        this.field_3376 = new ModelPart(this, 40, 0);
        this.field_3376.setPivot(1.5f, 22.0f, -4.0f);
        this.field_3376.addCuboid(0.0f, 0.0f, 0.0f, 4.0f, 14.0f, 8.0f);
        this.field_3375 = new ModelPart(this, 40, 0);
        this.field_3375.setPivot(-1.5f, 22.0f, 4.0f);
        this.field_3375.addCuboid(0.0f, 0.0f, 0.0f, 4.0f, 14.0f, 8.0f);
    }

    @Override
    public void setAngles(T arg, float f, float g, float h, float i, float j) {
        float k = f * 2.0f;
        if (k > 1.0f) {
            k = 1.0f;
        }
        k = 1.0f - k * k * k;
        this.field_3376.roll = (float)Math.PI - k * 0.35f * (float)Math.PI;
        this.field_3375.roll = (float)Math.PI + k * 0.35f * (float)Math.PI;
        this.field_3375.yaw = (float)Math.PI;
        float l = (f + MathHelper.sin(f * 2.7f)) * 0.6f * 12.0f;
        this.field_3375.pivotY = this.field_3376.pivotY = 24.0f - l;
        this.field_3374.pivotY = this.field_3376.pivotY;
    }

    @Override
    public Iterable<ModelPart> getParts() {
        return ImmutableList.of((Object)this.field_3374, (Object)this.field_3376, (Object)this.field_3375);
    }
}

