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
import net.minecraft.client.render.entity.model.HorseEntityModel;
import net.minecraft.entity.passive.AbstractDonkeyEntity;

@Environment(value=EnvType.CLIENT)
public class DonkeyEntityModel<T extends AbstractDonkeyEntity>
extends HorseEntityModel<T> {
    private final ModelPart leftChest = new ModelPart(this, 26, 21);
    private final ModelPart rightChest;

    public DonkeyEntityModel(float f) {
        super(f);
        this.leftChest.addCuboid(-4.0f, 0.0f, -2.0f, 8.0f, 8.0f, 3.0f);
        this.rightChest = new ModelPart(this, 26, 21);
        this.rightChest.addCuboid(-4.0f, 0.0f, -2.0f, 8.0f, 8.0f, 3.0f);
        this.leftChest.yaw = -1.5707964f;
        this.rightChest.yaw = 1.5707964f;
        this.leftChest.setPivot(6.0f, -8.0f, 0.0f);
        this.rightChest.setPivot(-6.0f, -8.0f, 0.0f);
        this.torso.addChild(this.leftChest);
        this.torso.addChild(this.rightChest);
    }

    @Override
    protected void method_2789(ModelPart arg) {
        ModelPart lv = new ModelPart(this, 0, 12);
        lv.addCuboid(-1.0f, -7.0f, 0.0f, 2.0f, 7.0f, 1.0f);
        lv.setPivot(1.25f, -10.0f, 4.0f);
        ModelPart lv2 = new ModelPart(this, 0, 12);
        lv2.addCuboid(-1.0f, -7.0f, 0.0f, 2.0f, 7.0f, 1.0f);
        lv2.setPivot(-1.25f, -10.0f, 4.0f);
        lv.pitch = 0.2617994f;
        lv.roll = 0.2617994f;
        lv2.pitch = 0.2617994f;
        lv2.roll = -0.2617994f;
        arg.addChild(lv);
        arg.addChild(lv2);
    }

    @Override
    public void setAngles(T arg, float f, float g, float h, float i, float j) {
        super.setAngles(arg, f, g, h, i, j);
        if (((AbstractDonkeyEntity)arg).hasChest()) {
            this.leftChest.visible = true;
            this.rightChest.visible = true;
        } else {
            this.leftChest.visible = false;
            this.rightChest.visible = false;
        }
    }
}

