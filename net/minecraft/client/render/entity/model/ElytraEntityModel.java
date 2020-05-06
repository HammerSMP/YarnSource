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
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.model.AnimalModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Vec3d;

@Environment(value=EnvType.CLIENT)
public class ElytraEntityModel<T extends LivingEntity>
extends AnimalModel<T> {
    private final ModelPart field_3364;
    private final ModelPart field_3365 = new ModelPart(this, 22, 0);

    public ElytraEntityModel() {
        this.field_3365.addCuboid(-10.0f, 0.0f, 0.0f, 10.0f, 20.0f, 2.0f, 1.0f);
        this.field_3364 = new ModelPart(this, 22, 0);
        this.field_3364.mirror = true;
        this.field_3364.addCuboid(0.0f, 0.0f, 0.0f, 10.0f, 20.0f, 2.0f, 1.0f);
    }

    @Override
    protected Iterable<ModelPart> getHeadParts() {
        return ImmutableList.of();
    }

    @Override
    protected Iterable<ModelPart> getBodyParts() {
        return ImmutableList.of((Object)this.field_3365, (Object)this.field_3364);
    }

    @Override
    public void setAngles(T arg, float f, float g, float h, float i, float j) {
        float k = 0.2617994f;
        float l = -0.2617994f;
        float m = 0.0f;
        float n = 0.0f;
        if (((LivingEntity)arg).isFallFlying()) {
            float o = 1.0f;
            Vec3d lv = ((Entity)arg).getVelocity();
            if (lv.y < 0.0) {
                Vec3d lv2 = lv.normalize();
                o = 1.0f - (float)Math.pow(-lv2.y, 1.5);
            }
            k = o * 0.34906584f + (1.0f - o) * k;
            l = o * -1.5707964f + (1.0f - o) * l;
        } else if (((Entity)arg).isInSneakingPose()) {
            k = 0.6981317f;
            l = -0.7853982f;
            m = 3.0f;
            n = 0.08726646f;
        }
        this.field_3365.pivotX = 5.0f;
        this.field_3365.pivotY = m;
        if (arg instanceof AbstractClientPlayerEntity) {
            AbstractClientPlayerEntity lv3 = (AbstractClientPlayerEntity)arg;
            lv3.elytraPitch = (float)((double)lv3.elytraPitch + (double)(k - lv3.elytraPitch) * 0.1);
            lv3.elytraYaw = (float)((double)lv3.elytraYaw + (double)(n - lv3.elytraYaw) * 0.1);
            lv3.elytraRoll = (float)((double)lv3.elytraRoll + (double)(l - lv3.elytraRoll) * 0.1);
            this.field_3365.pitch = lv3.elytraPitch;
            this.field_3365.yaw = lv3.elytraYaw;
            this.field_3365.roll = lv3.elytraRoll;
        } else {
            this.field_3365.pitch = k;
            this.field_3365.roll = l;
            this.field_3365.yaw = n;
        }
        this.field_3364.pivotX = -this.field_3365.pivotX;
        this.field_3364.yaw = -this.field_3365.yaw;
        this.field_3364.pivotY = this.field_3365.pivotY;
        this.field_3364.pitch = this.field_3365.pitch;
        this.field_3364.roll = -this.field_3365.roll;
    }
}

