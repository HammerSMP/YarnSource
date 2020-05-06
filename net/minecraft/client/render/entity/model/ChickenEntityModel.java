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
public class ChickenEntityModel<T extends Entity>
extends AnimalModel<T> {
    private final ModelPart head;
    private final ModelPart torso;
    private final ModelPart rightLeg;
    private final ModelPart leftLeg;
    private final ModelPart rightWing;
    private final ModelPart leftWing;
    private final ModelPart beak;
    private final ModelPart wattle;

    public ChickenEntityModel() {
        int i = 16;
        this.head = new ModelPart(this, 0, 0);
        this.head.addCuboid(-2.0f, -6.0f, -2.0f, 4.0f, 6.0f, 3.0f, 0.0f);
        this.head.setPivot(0.0f, 15.0f, -4.0f);
        this.beak = new ModelPart(this, 14, 0);
        this.beak.addCuboid(-2.0f, -4.0f, -4.0f, 4.0f, 2.0f, 2.0f, 0.0f);
        this.beak.setPivot(0.0f, 15.0f, -4.0f);
        this.wattle = new ModelPart(this, 14, 4);
        this.wattle.addCuboid(-1.0f, -2.0f, -3.0f, 2.0f, 2.0f, 2.0f, 0.0f);
        this.wattle.setPivot(0.0f, 15.0f, -4.0f);
        this.torso = new ModelPart(this, 0, 9);
        this.torso.addCuboid(-3.0f, -4.0f, -3.0f, 6.0f, 8.0f, 6.0f, 0.0f);
        this.torso.setPivot(0.0f, 16.0f, 0.0f);
        this.rightLeg = new ModelPart(this, 26, 0);
        this.rightLeg.addCuboid(-1.0f, 0.0f, -3.0f, 3.0f, 5.0f, 3.0f);
        this.rightLeg.setPivot(-2.0f, 19.0f, 1.0f);
        this.leftLeg = new ModelPart(this, 26, 0);
        this.leftLeg.addCuboid(-1.0f, 0.0f, -3.0f, 3.0f, 5.0f, 3.0f);
        this.leftLeg.setPivot(1.0f, 19.0f, 1.0f);
        this.rightWing = new ModelPart(this, 24, 13);
        this.rightWing.addCuboid(0.0f, 0.0f, -3.0f, 1.0f, 4.0f, 6.0f);
        this.rightWing.setPivot(-4.0f, 13.0f, 0.0f);
        this.leftWing = new ModelPart(this, 24, 13);
        this.leftWing.addCuboid(-1.0f, 0.0f, -3.0f, 1.0f, 4.0f, 6.0f);
        this.leftWing.setPivot(4.0f, 13.0f, 0.0f);
    }

    @Override
    protected Iterable<ModelPart> getHeadParts() {
        return ImmutableList.of((Object)this.head, (Object)this.beak, (Object)this.wattle);
    }

    @Override
    protected Iterable<ModelPart> getBodyParts() {
        return ImmutableList.of((Object)this.torso, (Object)this.rightLeg, (Object)this.leftLeg, (Object)this.rightWing, (Object)this.leftWing);
    }

    @Override
    public void setAngles(T arg, float f, float g, float h, float i, float j) {
        this.head.pitch = j * ((float)Math.PI / 180);
        this.head.yaw = i * ((float)Math.PI / 180);
        this.beak.pitch = this.head.pitch;
        this.beak.yaw = this.head.yaw;
        this.wattle.pitch = this.head.pitch;
        this.wattle.yaw = this.head.yaw;
        this.torso.pitch = 1.5707964f;
        this.rightLeg.pitch = MathHelper.cos(f * 0.6662f) * 1.4f * g;
        this.leftLeg.pitch = MathHelper.cos(f * 0.6662f + (float)Math.PI) * 1.4f * g;
        this.rightWing.roll = h;
        this.leftWing.roll = -h;
    }
}

