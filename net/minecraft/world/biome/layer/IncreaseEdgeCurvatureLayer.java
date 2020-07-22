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
    public int sample(LayerRandomnessSource context, int sw, int se, int ne, int nw, int center) {
        if (!(!BiomeLayers.isShallowOcean(center) || BiomeLayers.isShallowOcean(nw) && BiomeLayers.isShallowOcean(ne) && BiomeLayers.isShallowOcean(sw) && BiomeLayers.isShallowOcean(se))) {
            int n = 1;
            int o = 1;
            if (!BiomeLayers.isShallowOcean(nw) && context.nextInt(n++) == 0) {
                o = nw;
            }
            if (!BiomeLayers.isShallowOcean(ne) && context.nextInt(n++) == 0) {
                o = ne;
            }
            if (!BiomeLayers.isShallowOcean(sw) && context.nextInt(n++) == 0) {
                o = sw;
            }
            if (!BiomeLayers.isShallowOcean(se) && context.nextInt(n++) == 0) {
                o = se;
            }
            if (context.nextInt(3) == 0) {
                return o;
            }
            return o == 4 ? 4 : center;
        }
        if (!BiomeLayers.isShallowOcean(center) && (BiomeLayers.isShallowOcean(nw) || BiomeLayers.isShallowOcean(sw) || BiomeLayers.isShallowOcean(ne) || BiomeLayers.isShallowOcean(se)) && context.nextInt(5) == 0) {
            if (BiomeLayers.isShallowOcean(nw)) {
                return center == 4 ? 4 : nw;
            }
            if (BiomeLayers.isShallowOcean(sw)) {
                return center == 4 ? 4 : sw;
            }
            if (BiomeLayers.isShallowOcean(ne)) {
                return center == 4 ? 4 : ne;
            }
            if (BiomeLayers.isShallowOcean(se)) {
                return center == 4 ? 4 : se;
            }
        }
        return center;
    }
}

