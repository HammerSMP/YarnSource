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
import net.minecraft.client.render.entity.model.VillagerResemblingModel;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class WitchEntityModel<T extends Entity>
extends VillagerResemblingModel<T> {
    private boolean liftingNose;
    private final ModelPart mole = new ModelPart(this).setTextureSize(64, 128);

    public WitchEntityModel(float f) {
        super(f, 64, 128);
        this.mole.setPivot(0.0f, -2.0f, 0.0f);
        this.mole.setTextureOffset(0, 0).addCuboid(0.0f, 3.0f, -6.75f, 1.0f, 1.0f, 1.0f, -0.25f);
        this.nose.addChild(this.mole);
        this.head = new ModelPart(this).setTextureSize(64, 128);
        this.head.setPivot(0.0f, 0.0f, 0.0f);
        this.head.setTextureOffset(0, 0).addCuboid(-4.0f, -10.0f, -4.0f, 8.0f, 10.0f, 8.0f, f);
        this.field_17141 = new ModelPart(this).setTextureSize(64, 128);
        this.field_17141.setPivot(-5.0f, -10.03125f, -5.0f);
        this.field_17141.setTextureOffset(0, 64).addCuboid(0.0f, 0.0f, 0.0f, 10.0f, 2.0f, 10.0f);
        this.head.addChild(this.field_17141);
        this.head.addChild(this.nose);
        ModelPart lv = new ModelPart(this).setTextureSize(64, 128);
        lv.setPivot(1.75f, -4.0f, 2.0f);
        lv.setTextureOffset(0, 76).addCuboid(0.0f, 0.0f, 0.0f, 7.0f, 4.0f, 7.0f);
        lv.pitch = -0.05235988f;
        lv.roll = 0.02617994f;
        this.field_17141.addChild(lv);
        ModelPart lv2 = new ModelPart(this).setTextureSize(64, 128);
        lv2.setPivot(1.75f, -4.0f, 2.0f);
        lv2.setTextureOffset(0, 87).addCuboid(0.0f, 0.0f, 0.0f, 4.0f, 4.0f, 4.0f);
        lv2.pitch = -0.10471976f;
        lv2.roll = 0.05235988f;
        lv.addChild(lv2);
        ModelPart lv3 = new ModelPart(this).setTextureSize(64, 128);
        lv3.setPivot(1.75f, -2.0f, 2.0f);
        lv3.setTextureOffset(0, 95).addCuboid(0.0f, 0.0f, 0.0f, 1.0f, 2.0f, 1.0f, 0.25f);
        lv3.pitch = -0.20943952f;
        lv3.roll = 0.10471976f;
        lv2.addChild(lv3);
    }

    @Override
    public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        super.setAngles(entity, limbAngle, limbDistance, animationProgress, headYaw, headPitch);
        this.nose.setPivot(0.0f, -2.0f, 0.0f);
        float k = 0.01f * (float)(((Entity)entity).getEntityId() % 10);
        this.nose.pitch = MathHelper.sin((float)((Entity)entity).age * k) * 4.5f * ((float)Math.PI / 180);
        this.nose.yaw = 0.0f;
        this.nose.roll = MathHelper.cos((float)((Entity)entity).age * k) * 2.5f * ((float)Math.PI / 180);
        if (this.liftingNose) {
            this.nose.setPivot(0.0f, 1.0f, -1.5f);
            this.nose.pitch = -0.9f;
        }
    }

    public ModelPart getNose() {
        return this.nose;
    }

    public void setLiftingNose(boolean bl) {
        this.liftingNose = bl;
    }
}

