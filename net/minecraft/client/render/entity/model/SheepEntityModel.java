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
import net.minecraft.client.render.entity.model.QuadrupedEntityModel;
import net.minecraft.entity.passive.SheepEntity;

@Environment(value=EnvType.CLIENT)
public class SheepEntityModel<T extends SheepEntity>
extends QuadrupedEntityModel<T> {
    private float field_3552;

    public SheepEntityModel() {
        super(12, 0.0f, false, 8.0f, 4.0f, 2.0f, 2.0f, 24);
        this.head = new ModelPart(this, 0, 0);
        this.head.addCuboid(-3.0f, -4.0f, -6.0f, 6.0f, 6.0f, 8.0f, 0.0f);
        this.head.setPivot(0.0f, 6.0f, -8.0f);
        this.torso = new ModelPart(this, 28, 8);
        this.torso.addCuboid(-4.0f, -10.0f, -7.0f, 8.0f, 16.0f, 6.0f, 0.0f);
        this.torso.setPivot(0.0f, 5.0f, 2.0f);
    }

    @Override
    public void animateModel(T arg, float f, float g, float h) {
        super.animateModel(arg, f, g, h);
        this.head.pivotY = 6.0f + ((SheepEntity)arg).method_6628(h) * 9.0f;
        this.field_3552 = ((SheepEntity)arg).method_6641(h);
    }

    @Override
    public void setAngles(T arg, float f, float g, float h, float i, float j) {
        super.setAngles(arg, f, g, h, i, j);
        this.head.pitch = this.field_3552;
    }
}

