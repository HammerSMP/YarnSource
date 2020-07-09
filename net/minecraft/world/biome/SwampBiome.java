/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.world.biome;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5464;
import net.minecraft.class_5470;
import net.minecraft.class_5471;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.sound.BiomeMoodSound;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeEffects;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.DefaultBiomeFeatures;

public final class SwampBiome
extends Biome {
    public SwampBiome() {
        super(new Biome.Settings().configureSurfaceBuilder(class_5471.SWAMP).precipitation(Biome.Precipitation.RAIN).category(Biome.Category.SWAMP).depth(-0.2f).scale(0.1f).temperature(0.8f).downfall(0.9f).effects(new BiomeEffects.Builder().waterColor(6388580).waterFogColor(2302743).fogColor(12638463).moodSound(BiomeMoodSound.CAVE).build()).parent(null));
        this.addStructureFeature(class_5470.SWAMP_HUT);
        this.addStructureFeature(class_5470.MINESHAFT);
        this.addStructureFeature(class_5470.RUINED_PORTAL_SWAMP);
        DefaultBiomeFeatures.addLandCarvers(this);
        DefaultBiomeFeatures.addFossils(this);
        DefaultBiomeFeatures.addDefaultLakes(this);
        DefaultBiomeFeatures.addDungeons(this);
        DefaultBiomeFeatures.addMineables(this);
        DefaultBiomeFeatures.addDefaultOres(this);
        DefaultBiomeFeatures.addClay(this);
        DefaultBiomeFeatures.addSwampFeatures(this);
        DefaultBiomeFeatures.addDefaultMushrooms(this);
        DefaultBiomeFeatures.addSwampVegetation(this);
        DefaultBiomeFeatures.addSprings(this);
        this.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, class_5464.SEAGRASS_SWAMP);
        DefaultBiomeFeatures.addFrozenTopLayer(this);
        DefaultBiomeFeatures.method_30580(this);
        DefaultBiomeFeatures.method_30581(this);
        this.addSpawn(SpawnGroup.MONSTER, new Biome.SpawnEntry(EntityType.SLIME, 1, 1, 1));
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public int getGrassColorAt(double d, double e) {
        double f = FOLIAGE_NOISE.sample(d * 0.0225, e * 0.0225, false);
        if (f < -0.1) {
            return 5011004;
        }
        return 6975545;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public int getFoliageColor() {
        return 6975545;
    }
}

