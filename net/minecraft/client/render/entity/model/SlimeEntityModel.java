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

@Environment(value=EnvType.CLIENT)
public class SlimeEntityModel<T extends Entity>
extends CompositeEntityModel<T> {
    private final ModelPart innerCube;
    private final ModelPart rightEye;
    private final ModelPart leftEye;
    private final ModelPart mouth;

    public SlimeEntityModel(int size) {
        this.innerCube = new ModelPart(this, 0, size);
        this.rightEye = new ModelPart(this, 32, 0);
        this.leftEye = new ModelPart(this, 32, 4);
        this.mouth = new ModelPart(this, 32, 8);
        if (size > 0) {
            this.innerCube.addCuboid(-3.0f, 17.0f, -3.0f, 6.0f, 6.0f, 6.0f);
            this.rightEye.addCuboid(-3.25f, 18.0f, -3.5f, 2.0f, 2.0f, 2.0f);
            this.leftEye.addCuboid(1.25f, 18.0f, -3.5f, 2.0f, 2.0f, 2.0f);
            this.mouth.addCuboid(0.0f, 21.0f, -3.5f, 1.0f, 1.0f, 1.0f);
        } else {
            this.innerCube.addCuboid(-4.0f, 16.0f, -4.0f, 8.0f, 8.0f, 8.0f);
        }
    }

    @Override
    public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
    }

    @Override
    public Iterable<ModelPart> getParts() {
        return ImmutableList.of((Object)this.innerCube, (Object)this.rightEye, (Object)this.leftEye, (Object)this.mouth);
    }
}

