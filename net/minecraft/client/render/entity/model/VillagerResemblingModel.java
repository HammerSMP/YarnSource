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
import net.minecraft.client.render.entity.model.ModelWithHat;
import net.minecraft.client.render.entity.model.ModelWithHead;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.AbstractTraderEntity;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class VillagerResemblingModel<T extends Entity>
extends CompositeEntityModel<T>
implements ModelWithHead,
ModelWithHat {
    protected ModelPart head;
    protected ModelPart field_17141;
    protected final ModelPart field_17142;
    protected final ModelPart torso;
    protected final ModelPart robe;
    protected final ModelPart arms;
    protected final ModelPart rightLeg;
    protected final ModelPart leftLeg;
    protected final ModelPart nose;

    public VillagerResemblingModel(float f) {
        this(f, 64, 64);
    }

    public VillagerResemblingModel(float f, int i, int j) {
        float g = 0.5f;
        this.head = new ModelPart(this).setTextureSize(i, j);
        this.head.setPivot(0.0f, 0.0f, 0.0f);
        this.head.setTextureOffset(0, 0).addCuboid(-4.0f, -10.0f, -4.0f, 8.0f, 10.0f, 8.0f, f);
        this.field_17141 = new ModelPart(this).setTextureSize(i, j);
        this.field_17141.setPivot(0.0f, 0.0f, 0.0f);
        this.field_17141.setTextureOffset(32, 0).addCuboid(-4.0f, -10.0f, -4.0f, 8.0f, 10.0f, 8.0f, f + 0.5f);
        this.head.addChild(this.field_17141);
        this.field_17142 = new ModelPart(this).setTextureSize(i, j);
        this.field_17142.setPivot(0.0f, 0.0f, 0.0f);
        this.field_17142.setTextureOffset(30, 47).addCuboid(-8.0f, -8.0f, -6.0f, 16.0f, 16.0f, 1.0f, f);
        this.field_17142.pitch = -1.5707964f;
        this.field_17141.addChild(this.field_17142);
        this.nose = new ModelPart(this).setTextureSize(i, j);
        this.nose.setPivot(0.0f, -2.0f, 0.0f);
        this.nose.setTextureOffset(24, 0).addCuboid(-1.0f, -1.0f, -6.0f, 2.0f, 4.0f, 2.0f, f);
        this.head.addChild(this.nose);
        this.torso = new ModelPart(this).setTextureSize(i, j);
        this.torso.setPivot(0.0f, 0.0f, 0.0f);
        this.torso.setTextureOffset(16, 20).addCuboid(-4.0f, 0.0f, -3.0f, 8.0f, 12.0f, 6.0f, f);
        this.robe = new ModelPart(this).setTextureSize(i, j);
        this.robe.setPivot(0.0f, 0.0f, 0.0f);
        this.robe.setTextureOffset(0, 38).addCuboid(-4.0f, 0.0f, -3.0f, 8.0f, 18.0f, 6.0f, f + 0.5f);
        this.torso.addChild(this.robe);
        this.arms = new ModelPart(this).setTextureSize(i, j);
        this.arms.setPivot(0.0f, 2.0f, 0.0f);
        this.arms.setTextureOffset(44, 22).addCuboid(-8.0f, -2.0f, -2.0f, 4.0f, 8.0f, 4.0f, f);
        this.arms.setTextureOffset(44, 22).addCuboid(4.0f, -2.0f, -2.0f, 4.0f, 8.0f, 4.0f, f, true);
        this.arms.setTextureOffset(40, 38).addCuboid(-4.0f, 2.0f, -2.0f, 8.0f, 4.0f, 4.0f, f);
        this.rightLeg = new ModelPart(this, 0, 22).setTextureSize(i, j);
        this.rightLeg.setPivot(-2.0f, 12.0f, 0.0f);
        this.rightLeg.addCuboid(-2.0f, 0.0f, -2.0f, 4.0f, 12.0f, 4.0f, f);
        this.leftLeg = new ModelPart(this, 0, 22).setTextureSize(i, j);
        this.leftLeg.mirror = true;
        this.leftLeg.setPivot(2.0f, 12.0f, 0.0f);
        this.leftLeg.addCuboid(-2.0f, 0.0f, -2.0f, 4.0f, 12.0f, 4.0f, f);
    }

    @Override
    public Iterable<ModelPart> getParts() {
        return ImmutableList.of((Object)this.head, (Object)this.torso, (Object)this.rightLeg, (Object)this.leftLeg, (Object)this.arms);
    }

    @Override
    public void setAngles(T arg, float f, float g, float h, float i, float j) {
        boolean bl = false;
        if (arg instanceof AbstractTraderEntity) {
            bl = ((AbstractTraderEntity)arg).getHeadRollingTimeLeft() > 0;
        }
        this.head.yaw = i * ((float)Math.PI / 180);
        this.head.pitch = j * ((float)Math.PI / 180);
        if (bl) {
            this.head.roll = 0.3f * MathHelper.sin(0.45f * h);
            this.head.pitch = 0.4f;
        } else {
            this.head.roll = 0.0f;
        }
        this.arms.pivotY = 3.0f;
        this.arms.pivotZ = -1.0f;
        this.arms.pitch = -0.75f;
        this.rightLeg.pitch = MathHelper.cos(f * 0.6662f) * 1.4f * g * 0.5f;
        this.leftLeg.pitch = MathHelper.cos(f * 0.6662f + (float)Math.PI) * 1.4f * g * 0.5f;
        this.rightLeg.yaw = 0.0f;
        this.leftLeg.yaw = 0.0f;
    }

    @Override
    public ModelPart getHead() {
        return this.head;
    }

    @Override
    public void setHatVisible(boolean bl) {
        this.head.visible = bl;
        this.field_17141.visible = bl;
        this.field_17142.visible = bl;
    }
}

