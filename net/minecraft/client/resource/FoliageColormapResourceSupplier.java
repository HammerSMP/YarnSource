/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.resource;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.color.world.FoliageColors;
import net.minecraft.client.util.RawTextureDataLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SinglePreparationResourceReloadListener;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

@Environment(value=EnvType.CLIENT)
public class FoliageColormapResourceSupplier
extends SinglePreparationResourceReloadListener<int[]> {
    private static final Identifier FOLIAGE_COLORMAP = new Identifier("textures/colormap/foliage.png");

    protected int[] reload(ResourceManager arg, Profiler arg2) {
        try {
            return RawTextureDataLoader.loadRawTextureData(arg, FOLIAGE_COLORMAP);
        }
        catch (IOException iOException) {
            throw new IllegalStateException("Failed to load foliage color texture", iOException);
        }
    }

    @Override
    protected void apply(int[] is, ResourceManager arg, Profiler arg2) {
        FoliageColors.setColorMap(is);
    }

    @Override
    protected /* synthetic */ Object prepare(ResourceManager arg, Profiler arg2) {
        return this.reload(arg, arg2);
    }
}

