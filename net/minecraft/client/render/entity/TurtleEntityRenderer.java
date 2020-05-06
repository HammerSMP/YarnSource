/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.TurtleEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.TurtleEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class TurtleEntityRenderer
extends MobEntityRenderer<TurtleEntity, TurtleEntityModel<TurtleEntity>> {
    private static final Identifier TEXTURE = new Identifier("textures/entity/turtle/big_sea_turtle.png");

    public TurtleEntityRenderer(EntityRenderDispatcher arg) {
        super(arg, new TurtleEntityModel(0.0f), 0.7f);
    }

    @Override
    public void render(TurtleEntity arg, float f, float g, MatrixStack arg2, VertexConsumerProvider arg3, int i) {
        if (arg.isBaby()) {
            this.shadowRadius *= 0.5f;
        }
        super.render(arg, f, g, arg2, arg3, i);
    }

    @Override
    public Identifier getTexture(TurtleEntity arg) {
        return TEXTURE;
    }
}

