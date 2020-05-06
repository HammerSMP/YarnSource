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
public class ShulkerBulletEntityModel<T extends Entity>
extends CompositeEntityModel<T> {
    private final ModelPart bullet;

    public ShulkerBulletEntityModel() {
        this.textureWidth = 64;
        this.textureHeight = 32;
        this.bullet = new ModelPart(this);
        this.bullet.setTextureOffset(0, 0).addCuboid(-4.0f, -4.0f, -1.0f, 8.0f, 8.0f, 2.0f, 0.0f);
        this.bullet.setTextureOffset(0, 10).addCuboid(-1.0f, -4.0f, -4.0f, 2.0f, 8.0f, 8.0f, 0.0f);
        this.bullet.setTextureOffset(20, 0).addCuboid(-4.0f, -1.0f, -4.0f, 8.0f, 2.0f, 8.0f, 0.0f);
        this.bullet.setPivot(0.0f, 0.0f, 0.0f);
    }

    @Override
    public Iterable<ModelPart> getParts() {
        return ImmutableList.of((Object)this.bullet);
    }

    @Override
    public void setAngles(T arg, float f, float g, float h, float i, float j) {
        this.bullet.yaw = i * ((float)Math.PI / 180);
        this.bullet.pitch = j * ((float)Math.PI / 180);
    }
}

