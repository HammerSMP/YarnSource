/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.world.biome.layer;

import net.minecraft.world.biome.layer.BiomeLayers;
import net.minecraft.world.biome.layer.type.DiagonalCrossSamplingLayer;
import net.minecraft.world.biome.layer.util.LayerRandomnessSource;

public enum IncreaseEdgeCurvatureLayer implements DiagonalCrossSamplingLayer
{
    INSTANCE;


    @Override
    public int sample(LayerRandomnessSource arg, int i, int j, int k, int l, int m) {
        if (!(!BiomeLayers.isShallowOcean(m) || BiomeLayers.isShallowOcean(l) && BiomeLayers.isShallowOcean(k) && BiomeLayers.isShallowOcean(i) && BiomeLayers.isShallowOcean(j))) {
            int n = 1;
            int o = 1;
            if (!BiomeLayers.isShallowOcean(l) && arg.nextInt(n++) == 0) {
                o = l;
            }
            if (!BiomeLayers.isShallowOcean(k) && arg.nextInt(n++) == 0) {
                o = k;
            }
            if (!BiomeLayers.isShallowOcean(i) && arg.nextInt(n++) == 0) {
                o = i;
            }
            if (!BiomeLayers.isShallowOcean(j) && arg.nextInt(n++) == 0) {
                o = j;
            }
            if (arg.nextInt(3) == 0) {
                return o;
            }
            return o == 4 ? 4 : m;
        }
        if (!BiomeLayers.isShallowOcean(m) && (BiomeLayers.isShallowOcean(l) || BiomeLayers.isShallowOcean(i) || BiomeLayers.isShallowOcean(k) || BiomeLayers.isShallowOcean(j)) && arg.nextInt(5) == 0) {
            if (BiomeLayers.isShallowOcean(l)) {
                return m == 4 ? 4 : l;
            }
            if (BiomeLayers.isShallowOcean(i)) {
                return m == 4 ? 4 : i;
            }
            if (BiomeLayers.isShallowOcean(k)) {
                return m == 4 ? 4 : k;
            }
            if (BiomeLayers.isShallowOcean(j)) {
                return m == 4 ? 4 : j;
            }
        }
        return m;
    }
}

