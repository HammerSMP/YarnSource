/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import java.util.Arrays;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.CompositeEntityModel;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class BoatEntityModel
extends CompositeEntityModel<BoatEntity> {
    private final ModelPart[] paddles = new ModelPart[2];
    private final ModelPart bottom;
    private final ImmutableList<ModelPart> parts;

    public BoatEntityModel() {
        ModelPart[] lvs = new ModelPart[]{new ModelPart(this, 0, 0).setTextureSize(128, 64), new ModelPart(this, 0, 19).setTextureSize(128, 64), new ModelPart(this, 0, 27).setTextureSize(128, 64), new ModelPart(this, 0, 35).setTextureSize(128, 64), new ModelPart(this, 0, 43).setTextureSize(128, 64)};
        int i = 32;
        int j = 6;
        int k = 20;
        int l = 4;
        int m = 28;
        lvs[0].addCuboid(-14.0f, -9.0f, -3.0f, 28.0f, 16.0f, 3.0f, 0.0f);
        lvs[0].setPivot(0.0f, 3.0f, 1.0f);
        lvs[1].addCuboid(-13.0f, -7.0f, -1.0f, 18.0f, 6.0f, 2.0f, 0.0f);
        lvs[1].setPivot(-15.0f, 4.0f, 4.0f);
        lvs[2].addCuboid(-8.0f, -7.0f, -1.0f, 16.0f, 6.0f, 2.0f, 0.0f);
        lvs[2].setPivot(15.0f, 4.0f, 0.0f);
        lvs[3].addCuboid(-14.0f, -7.0f, -1.0f, 28.0f, 6.0f, 2.0f, 0.0f);
        lvs[3].setPivot(0.0f, 4.0f, -9.0f);
        lvs[4].addCuboid(-14.0f, -7.0f, -1.0f, 28.0f, 6.0f, 2.0f, 0.0f);
        lvs[4].setPivot(0.0f, 4.0f, 9.0f);
        lvs[0].pitch = 1.5707964f;
        lvs[1].yaw = 4.712389f;
        lvs[2].yaw = 1.5707964f;
        lvs[3].yaw = (float)Math.PI;
        this.paddles[0] = this.makePaddle(true);
        this.paddles[0].setPivot(3.0f, -5.0f, 9.0f);
        this.paddles[1] = this.makePaddle(false);
        this.paddles[1].setPivot(3.0f, -5.0f, -9.0f);
        this.paddles[1].yaw = (float)Math.PI;
        this.paddles[0].roll = 0.19634955f;
        this.paddles[1].roll = 0.19634955f;
        this.bottom = new ModelPart(this, 0, 0).setTextureSize(128, 64);
        this.bottom.addCuboid(-14.0f, -9.0f, -3.0f, 28.0f, 16.0f, 3.0f, 0.0f);
        this.bottom.setPivot(0.0f, -3.0f, 1.0f);
        this.bottom.pitch = 1.5707964f;
        ImmutableList.Builder builder = ImmutableList.builder();
        builder.addAll(Arrays.asList(lvs));
        builder.addAll(Arrays.asList(this.paddles));
        this.parts = builder.build();
    }

    @Override
    public void setAngles(BoatEntity arg, float f, float g, float h, float i, float j) {
        this.setPaddleAngle(arg, 0, f);
        this.setPaddleAngle(arg, 1, f);
    }

    public ImmutableList<ModelPart> getParts() {
        return this.parts;
    }

    public ModelPart getBottom() {
        return this.bottom;
    }

    protected ModelPart makePaddle(boolean bl) {
        ModelPart lv = new ModelPart(this, 62, bl ? 0 : 20).setTextureSize(128, 64);
        int i = 20;
        int j = 7;
        int k = 6;
        float f = -5.0f;
        lv.addCuboid(-1.0f, 0.0f, -5.0f, 2.0f, 2.0f, 18.0f);
        lv.addCuboid(bl ? -1.001f : 0.001f, -3.0f, 8.0f, 1.0f, 6.0f, 7.0f);
        return lv;
    }

    protected void setPaddleAngle(BoatEntity arg, int i, float f) {
        float g = arg.interpolatePaddlePhase(i, f);
        ModelPart lv = this.paddles[i];
        lv.pitch = (float)MathHelper.clampedLerp(-1.0471975803375244, -0.2617993950843811, (MathHelper.sin(-g) + 1.0f) / 2.0f);
        lv.yaw = (float)MathHelper.clampedLerp(-0.7853981852531433, 0.7853981852531433, (MathHelper.sin(-g + 1.0f) + 1.0f) / 2.0f);
        if (i == 1) {
            lv.yaw = (float)Math.PI - lv.yaw;
        }
    }

    @Override
    public /* synthetic */ Iterable getParts() {
        return this.getParts();
    }
}

