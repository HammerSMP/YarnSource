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
import net.minecraft.client.render.entity.ZombieEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class HuskEntityRenderer
extends ZombieEntityRenderer {
    private static final Identifier TEXTURE = new Identifier("textures/entity/zombie/husk.png");

    public HuskEntityRenderer(EntityRenderDispatcher arg) {
        super(arg);
    }

    @Override
    protected void scale(ZombieEntity arg, MatrixStack arg2, float f) {
        float g = 1.0625f;
        arg2.scale(1.0625f, 1.0625f, 1.0625f);
        super.scale(arg, arg2, f);
    }

    @Override
    public Identifier getTexture(ZombieEntity arg) {
        return TEXTURE;
    }
}

