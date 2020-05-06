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
import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.CompositeEntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class GhastEntityModel<T extends Entity>
extends CompositeEntityModel<T> {
    private final ModelPart[] field_3372 = new ModelPart[9];
    private final ImmutableList<ModelPart> field_20929;

    public GhastEntityModel() {
        ImmutableList.Builder builder = ImmutableList.builder();
        ModelPart lv = new ModelPart(this, 0, 0);
        lv.addCuboid(-8.0f, -8.0f, -8.0f, 16.0f, 16.0f, 16.0f);
        lv.pivotY = 17.6f;
        builder.add((Object)lv);
        Random random = new Random(1660L);
        for (int i = 0; i < this.field_3372.length; ++i) {
            this.field_3372[i] = new ModelPart(this, 0, 0);
            float f = (((float)(i % 3) - (float)(i / 3 % 2) * 0.5f + 0.25f) / 2.0f * 2.0f - 1.0f) * 5.0f;
            float g = ((float)(i / 3) / 2.0f * 2.0f - 1.0f) * 5.0f;
            int j = random.nextInt(7) + 8;
            this.field_3372[i].addCuboid(-1.0f, 0.0f, -1.0f, 2.0f, j, 2.0f);
            this.field_3372[i].pivotX = f;
            this.field_3372[i].pivotZ = g;
            this.field_3372[i].pivotY = 24.6f;
            builder.add((Object)this.field_3372[i]);
        }
        this.field_20929 = builder.build();
    }

    @Override
    public void setAngles(T arg, float f, float g, float h, float i, float j) {
        for (int k = 0; k < this.field_3372.length; ++k) {
            this.field_3372[k].pitch = 0.2f * MathHelper.sin(h * 0.3f + (float)k) + 0.4f;
        }
    }

    @Override
    public Iterable<ModelPart> getParts() {
        return this.field_20929;
    }
}

