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
import net.minecraft.client.render.entity.model.AnimalModel;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class OcelotEntityModel<T extends Entity>
extends AnimalModel<T> {
    protected final ModelPart leftBackLeg;
    protected final ModelPart rightBackLeg;
    protected final ModelPart leftFrontLeg;
    protected final ModelPart rightFrontLeg;
    protected final ModelPart upperTail;
    protected final ModelPart lowerTail;
    protected final ModelPart head = new ModelPart(this);
    protected final ModelPart torso;
    protected int animationState = 1;

    public OcelotEntityModel(float f) {
        super(true, 10.0f, 4.0f);
        this.head.addCuboid("main", -2.5f, -2.0f, -3.0f, 5, 4, 5, f, 0, 0);
        this.head.addCuboid("nose", -1.5f, 0.0f, -4.0f, 3, 2, 2, f, 0, 24);
        this.head.addCuboid("ear1", -2.0f, -3.0f, 0.0f, 1, 1, 2, f, 0, 10);
        this.head.addCuboid("ear2", 1.0f, -3.0f, 0.0f, 1, 1, 2, f, 6, 10);
        this.head.setPivot(0.0f, 15.0f, -9.0f);
        this.torso = new ModelPart(this, 20, 0);
        this.torso.addCuboid(-2.0f, 3.0f, -8.0f, 4.0f, 16.0f, 6.0f, f);
        this.torso.setPivot(0.0f, 12.0f, -10.0f);
        this.upperTail = new ModelPart(this, 0, 15);
        this.upperTail.addCuboid(-0.5f, 0.0f, 0.0f, 1.0f, 8.0f, 1.0f, f);
        this.upperTail.pitch = 0.9f;
        this.upperTail.setPivot(0.0f, 15.0f, 8.0f);
        this.lowerTail = new ModelPart(this, 4, 15);
        this.lowerTail.addCuboid(-0.5f, 0.0f, 0.0f, 1.0f, 8.0f, 1.0f, f);
        this.lowerTail.setPivot(0.0f, 20.0f, 14.0f);
        this.leftBackLeg = new ModelPart(this, 8, 13);
        this.leftBackLeg.addCuboid(-1.0f, 0.0f, 1.0f, 2.0f, 6.0f, 2.0f, f);
        this.leftBackLeg.setPivot(1.1f, 18.0f, 5.0f);
        this.rightBackLeg = new ModelPart(this, 8, 13);
        this.rightBackLeg.addCuboid(-1.0f, 0.0f, 1.0f, 2.0f, 6.0f, 2.0f, f);
        this.rightBackLeg.setPivot(-1.1f, 18.0f, 5.0f);
        this.leftFrontLeg = new ModelPart(this, 40, 0);
        this.leftFrontLeg.addCuboid(-1.0f, 0.0f, 0.0f, 2.0f, 10.0f, 2.0f, f);
        this.leftFrontLeg.setPivot(1.2f, 14.1f, -5.0f);
        this.rightFrontLeg = new ModelPart(this, 40, 0);
        this.rightFrontLeg.addCuboid(-1.0f, 0.0f, 0.0f, 2.0f, 10.0f, 2.0f, f);
        this.rightFrontLeg.setPivot(-1.2f, 14.1f, -5.0f);
    }

    @Override
    protected Iterable<ModelPart> getHeadParts() {
        return ImmutableList.of((Object)this.head);
    }

    @Override
    protected Iterable<ModelPart> getBodyParts() {
        return ImmutableList.of((Object)this.torso, (Object)this.leftBackLeg, (Object)this.rightBackLeg, (Object)this.leftFrontLeg, (Object)this.rightFrontLeg, (Object)this.upperTail, (Object)this.lowerTail);
    }

    @Override
    public void setAngles(T arg, float f, float g, float h, float i, float j) {
        this.head.pitch = j * ((float)Math.PI / 180);
        this.head.yaw = i * ((float)Math.PI / 180);
        if (this.animationState != 3) {
            this.torso.pitch = 1.5707964f;
            if (this.animationState == 2) {
                this.leftBackLeg.pitch = MathHelper.cos(f * 0.6662f) * g;
                this.rightBackLeg.pitch = MathHelper.cos(f * 0.6662f + 0.3f) * g;
                this.leftFrontLeg.pitch = MathHelper.cos(f * 0.6662f + (float)Math.PI + 0.3f) * g;
                this.rightFrontLeg.pitch = MathHelper.cos(f * 0.6662f + (float)Math.PI) * g;
                this.lowerTail.pitch = 1.7278761f + 0.31415927f * MathHelper.cos(f) * g;
            } else {
                this.leftBackLeg.pitch = MathHelper.cos(f * 0.6662f) * g;
                this.rightBackLeg.pitch = MathHelper.cos(f * 0.6662f + (float)Math.PI) * g;
                this.leftFrontLeg.pitch = MathHelper.cos(f * 0.6662f + (float)Math.PI) * g;
                this.rightFrontLeg.pitch = MathHelper.cos(f * 0.6662f) * g;
                this.lowerTail.pitch = this.animationState == 1 ? 1.7278761f + 0.7853982f * MathHelper.cos(f) * g : 1.7278761f + 0.47123894f * MathHelper.cos(f) * g;
            }
        }
    }

    @Override
    public void animateModel(T arg, float f, float g, float h) {
        this.torso.pivotY = 12.0f;
        this.torso.pivotZ = -10.0f;
        this.head.pivotY = 15.0f;
        this.head.pivotZ = -9.0f;
        this.upperTail.pivotY = 15.0f;
        this.upperTail.pivotZ = 8.0f;
        this.lowerTail.pivotY = 20.0f;
        this.lowerTail.pivotZ = 14.0f;
        this.leftFrontLeg.pivotY = 14.1f;
        this.leftFrontLeg.pivotZ = -5.0f;
        this.rightFrontLeg.pivotY = 14.1f;
        this.rightFrontLeg.pivotZ = -5.0f;
        this.leftBackLeg.pivotY = 18.0f;
        this.leftBackLeg.pivotZ = 5.0f;
        this.rightBackLeg.pivotY = 18.0f;
        this.rightBackLeg.pivotZ = 5.0f;
        this.upperTail.pitch = 0.9f;
        if (((Entity)arg).isInSneakingPose()) {
            this.torso.pivotY += 1.0f;
            this.head.pivotY += 2.0f;
            this.upperTail.pivotY += 1.0f;
            this.lowerTail.pivotY += -4.0f;
            this.lowerTail.pivotZ += 2.0f;
            this.upperTail.pitch = 1.5707964f;
            this.lowerTail.pitch = 1.5707964f;
            this.animationState = 0;
        } else if (((Entity)arg).isSprinting()) {
            this.lowerTail.pivotY = this.upperTail.pivotY;
            this.lowerTail.pivotZ += 2.0f;
            this.upperTail.pitch = 1.5707964f;
            this.lowerTail.pitch = 1.5707964f;
            this.animationState = 2;
        } else {
            this.animationState = 1;
        }
    }
}

