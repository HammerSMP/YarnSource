/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity.model;

import java.util.Arrays;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.CompositeEntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class EndermiteEntityModel<T extends Entity>
extends CompositeEntityModel<T> {
    private static final int[][] field_3366 = new int[][]{{4, 3, 2}, {6, 4, 5}, {3, 3, 1}, {1, 2, 1}};
    private static final int[][] field_3369 = new int[][]{{0, 0}, {0, 5}, {0, 14}, {0, 18}};
    private static final int field_3367 = field_3366.length;
    private final ModelPart[] field_3368 = new ModelPart[field_3367];

    public EndermiteEntityModel() {
        float f = -3.5f;
        for (int i = 0; i < this.field_3368.length; ++i) {
            this.field_3368[i] = new ModelPart(this, field_3369[i][0], field_3369[i][1]);
            this.field_3368[i].addCuboid((float)field_3366[i][0] * -0.5f, 0.0f, (float)field_3366[i][2] * -0.5f, field_3366[i][0], field_3366[i][1], field_3366[i][2]);
            this.field_3368[i].setPivot(0.0f, 24 - field_3366[i][1], f);
            if (i >= this.field_3368.length - 1) continue;
            f += (float)(field_3366[i][2] + field_3366[i + 1][2]) * 0.5f;
        }
    }

    @Override
    public Iterable<ModelPart> getParts() {
        return Arrays.asList(this.field_3368);
    }

    @Override
    public void setAngles(T arg, float f, float g, float h, float i, float j) {
        for (int k = 0; k < this.field_3368.length; ++k) {
            this.field_3368[k].yaw = MathHelper.cos(h * 0.9f + (float)k * 0.15f * (float)Math.PI) * (float)Math.PI * 0.01f * (float)(1 + Math.abs(k - 2));
            this.field_3368[k].pivotX = MathHelper.sin(h * 0.9f + (float)k * 0.15f * (float)Math.PI) * (float)Math.PI * 0.1f * (float)Math.abs(k - 2);
        }
    }
}

