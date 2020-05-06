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
import net.minecraft.client.render.entity.GuardianEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.ElderGuardianEntity;
import net.minecraft.entity.mob.GuardianEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class ElderGuardianEntityRenderer
extends GuardianEntityRenderer {
    public static final Identifier TEXTURE = new Identifier("textures/entity/guardian_elder.png");

    public ElderGuardianEntityRenderer(EntityRenderDispatcher arg) {
        super(arg, 1.2f);
    }

    @Override
    protected void scale(GuardianEntity arg, MatrixStack arg2, float f) {
        arg2.scale(ElderGuardianEntity.SCALE, ElderGuardianEntity.SCALE, ElderGuardianEntity.SCALE);
    }

    @Override
    public Identifier getTexture(GuardianEntity arg) {
        return TEXTURE;
    }
}

