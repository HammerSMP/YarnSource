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
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.MagmaCubeEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.MagmaCubeEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class MagmaCubeEntityRenderer
extends MobEntityRenderer<MagmaCubeEntity, MagmaCubeEntityModel<MagmaCubeEntity>> {
    private static final Identifier TEXTURE = new Identifier("textures/entity/slime/magmacube.png");

    public MagmaCubeEntityRenderer(EntityRenderDispatcher arg) {
        super(arg, new MagmaCubeEntityModel(), 0.25f);
    }

    @Override
    protected int getBlockLight(MagmaCubeEntity arg, float f) {
        return 15;
    }

    @Override
    public Identifier getTexture(MagmaCubeEntity arg) {
        return TEXTURE;
    }

    @Override
    protected void scale(MagmaCubeEntity arg, MatrixStack arg2, float f) {
        int i = arg.getSize();
        float g = MathHelper.lerp(f, arg.lastStretch, arg.stretch) / ((float)i * 0.5f + 1.0f);
        float h = 1.0f / (g + 1.0f);
        arg2.scale(h * (float)i, 1.0f / h * (float)i, h * (float)i);
    }
}

