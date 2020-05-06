/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.Int2IntFunction
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.block.entity;

import it.unimi.dsi.fastutil.ints.Int2IntFunction;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.DoubleBlockProperties;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.WorldRenderer;

@Environment(value=EnvType.CLIENT)
public class LightmapCoordinatesRetriever<S extends BlockEntity>
implements DoubleBlockProperties.PropertyRetriever<S, Int2IntFunction> {
    @Override
    public Int2IntFunction getFromBoth(S arg, S arg2) {
        return i -> {
            int j = WorldRenderer.getLightmapCoordinates(arg.getWorld(), arg.getPos());
            int k = WorldRenderer.getLightmapCoordinates(arg2.getWorld(), arg2.getPos());
            int l = LightmapTextureManager.getBlockLightCoordinates(j);
            int m = LightmapTextureManager.getBlockLightCoordinates(k);
            int n = LightmapTextureManager.getSkyLightCoordinates(j);
            int o = LightmapTextureManager.getSkyLightCoordinates(k);
            return LightmapTextureManager.pack(Math.max(l, m), Math.max(n, o));
        };
    }

    @Override
    public Int2IntFunction getFrom(S arg) {
        return i -> i;
    }

    @Override
    public Int2IntFunction getFallback() {
        return i -> i;
    }

    @Override
    public /* synthetic */ Object getFallback() {
        return this.getFallback();
    }
}

