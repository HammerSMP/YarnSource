/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.world.biome;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.sound.BiomeMoodSound;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeEffects;
import net.minecraft.world.biome.DefaultBiomeFeatures;
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder;

public final class GiantTreeTaigaHillsBiome
extends Biome {
    public GiantTreeTaigaHillsBiome() {
        super(new Biome.Settings().configureSurfaceBuilder(SurfaceBuilder.GIANT_TREE_TAIGA, SurfaceBuilder.GRASS_CONFIG).precipitation(Biome.Precipitation.RAIN).category(Biome.Category.TAIGA).depth(0.45f).scale(0.3f).temperature(0.3f).downfall(0.8f).effects(new BiomeEffects.Builder().waterColor(4159204).waterFogColor(329011).fogColor(12638463).moodSound(BiomeMoodSound.CAVE).build()).parent(null));
        DefaultBiomeFeatures.method_28440(this);
        this.addStructureFeature(DefaultBiomeFeatures.field_24711);
        DefaultBiomeFeatures.addLandCarvers(this);
        DefaultBiomeFeatures.addDefaultLakes(this);
        DefaultBiomeFeatures.addDungeons(this);
        DefaultBiomeFeatures.addMossyRocks(this);
        DefaultBiomeFeatures.addLargeFerns(this);
        DefaultBiomeFeatures.addMineables(this);
        DefaultBiomeFeatures.addDefaultOres(this);
        DefaultBiomeFeatures.addDefaultDisks(this);
        DefaultBiomeFeatures.addGiantTreeTaigaTrees(this);
        DefaultBiomeFeatures.addDefaultFlowers(this);
        DefaultBiomeFeatures.addGiantTaigaGrass(this);
        DefaultBiomeFeatures.addDefaultMushrooms(this);
        DefaultBiomeFeatures.addDefaultVegetation(this);
        DefaultBiomeFeatures.addSprings(this);
        DefaultBiomeFeatures.addSweetBerryBushes(this);
        DefaultBiomeFeatures.addFrozenTopLayer(this);
        this.addSpawn(SpawnGroup.CREATURE, new Biome.SpawnEntry(EntityType.SHEEP, 12, 4, 4));
        this.addSpawn(SpawnGroup.CREATURE, new Biome.SpawnEntry(EntityType.PIG, 10, 4, 4));
        this.addSpawn(SpawnGroup.CREATURE, new Biome.SpawnEntry(EntityType.CHICKEN, 10, 4, 4));
        this.addSpawn(SpawnGroup.CREATURE, new Biome.SpawnEntry(EntityType.COW, 8, 4, 4));
        this.addSpawn(SpawnGroup.CREATURE, new Biome.SpawnEntry(EntityType.WOLF, 8, 4, 4));
        this.addSpawn(SpawnGroup.CREATURE, new Biome.SpawnEntry(EntityType.RABBIT, 4, 2, 3));
        this.addSpawn(SpawnGroup.CREATURE, new Biome.SpawnEntry(EntityType.FOX, 8, 2, 4));
        this.addSpawn(SpawnGroup.AMBIENT, new Biome.SpawnEntry(EntityType.BAT, 10, 8, 8));
        this.addSpawn(SpawnGroup.MONSTER, new Biome.SpawnEntry(EntityType.SPIDER, 100, 4, 4));
        this.addSpawn(SpawnGroup.MONSTER, new Biome.SpawnEntry(EntityType.ZOMBIE, 100, 4, 4));
        this.addSpawn(SpawnGroup.MONSTER, new Biome.SpawnEntry(EntityType.SKELETON, 100, 4, 4));
        this.addSpawn(SpawnGroup.MONSTER, new Biome.SpawnEntry(EntityType.ZOMBIE_VILLAGER, 25, 1, 1));
        this.addSpawn(SpawnGroup.MONSTER, new Biome.SpawnEntry(EntityType.CREEPER, 100, 4, 4));
        this.addSpawn(SpawnGroup.MONSTER, new Biome.SpawnEntry(EntityType.SLIME, 100, 4, 4));
        this.addSpawn(SpawnGroup.MONSTER, new Biome.SpawnEntry(EntityType.ENDERMAN, 10, 1, 4));
        this.addSpawn(SpawnGroup.MONSTER, new Biome.SpawnEntry(EntityType.WITCH, 5, 1, 1));
    }
}

