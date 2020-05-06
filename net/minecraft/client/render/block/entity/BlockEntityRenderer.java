/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.block.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;

@Environment(value=EnvType.CLIENT)
public abstract class BlockEntityRenderer<T extends BlockEntity> {
    protected final BlockEntityRenderDispatcher dispatcher;

    public BlockEntityRenderer(BlockEntityRenderDispatcher arg) {
        this.dispatcher = arg;
    }

    public abstract void render(T var1, float var2, MatrixStack var3, VertexConsumerProvider var4, int var5, int var6);

    public boolean rendersOutsideBoundingBox(T arg) {
        return false;
    }
}

