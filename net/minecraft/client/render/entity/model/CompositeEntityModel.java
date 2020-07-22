/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity.model;

import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public abstract class CompositeEntityModel<E extends Entity>
extends EntityModel<E> {
    public CompositeEntityModel() {
        this(RenderLayer::getEntityCutoutNoCull);
    }

    public CompositeEntityModel(Function<Identifier, RenderLayer> function) {
        super(function);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        this.getParts().forEach(arg3 -> arg3.render(matrices, vertices, light, overlay, red, green, blue, alpha));
    }

    public abstract Iterable<ModelPart> getParts();
}

