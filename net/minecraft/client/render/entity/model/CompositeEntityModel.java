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
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;

@Environment(value=EnvType.CLIENT)
public abstract class CompositeEntityModel<E extends Entity>
extends EntityModel<E> {
    @Override
    public void render(MatrixStack arg, VertexConsumer arg2, int i, int j, float f, float g, float h, float k) {
        this.getParts().forEach(arg3 -> arg3.render(arg, arg2, i, j, f, g, h, k));
    }

    public abstract Iterable<ModelPart> getParts();
}

