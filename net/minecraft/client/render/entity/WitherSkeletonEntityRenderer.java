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
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.SkeletonEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.AbstractSkeletonEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class WitherSkeletonEntityRenderer
extends SkeletonEntityRenderer {
    private static final Identifier TEXTURE = new Identifier("textures/entity/skeleton/wither_skeleton.png");

    public WitherSkeletonEntityRenderer(EntityRenderDispatcher arg) {
        super(arg);
    }

    @Override
    public Identifier getTexture(AbstractSkeletonEntity arg) {
        return TEXTURE;
    }

    @Override
    protected void scale(AbstractSkeletonEntity arg, MatrixStack arg2, float f) {
        arg2.scale(1.2f, 1.2f, 1.2f);
    }
}

