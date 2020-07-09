/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.world.biome.layer;

import net.minecraft.class_5458;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.layer.BiomeLayers;
import net.minecraft.world.biome.layer.type.IdentitySamplingLayer;
import net.minecraft.world.biome.layer.util.LayerRandomnessSource;

public class SetBaseBiomesLayer
implements IdentitySamplingLayer {
    private static final int BIRCH_FOREST_ID = class_5458.field_25933.getRawId(Biomes.BIRCH_FOREST);
    private static final int DESERT_ID = class_5458.field_25933.getRawId(Biomes.DESERT);
    private static final int MOUNTAINS_ID = class_5458.field_25933.getRawId(Biomes.MOUNTAINS);
    private static final int FOREST_ID = class_5458.field_25933.getRawId(Biomes.FOREST);
    private static final int SNOWY_TUNDRA_ID = class_5458.field_25933.getRawId(Biomes.SNOWY_TUNDRA);
    private static final int JUNGLE_ID = class_5458.field_25933.getRawId(Biomes.JUNGLE);
    private static final int BADLANDS_PLATEAU_ID = class_5458.field_25933.getRawId(Biomes.BADLANDS_PLATEAU);
    private static final int WOODED_BADLANDS_PLATEAU_ID = class_5458.field_25933.getRawId(Biomes.WOODED_BADLANDS_PLATEAU);
    private static final int MUSHROOM_FIELDS_ID = class_5458.field_25933.getRawId(Biomes.MUSHROOM_FIELDS);
    private static final int PLAINS_ID = class_5458.field_25933.getRawId(Biomes.PLAINS);
    private static final int GIANT_TREE_TAIGA_ID = class_5458.field_25933.getRawId(Biomes.GIANT_TREE_TAIGA);
    private static final int DARK_FOREST_ID = class_5458.field_25933.getRawId(Biomes.DARK_FOREST);
    private static final int SAVANNA_ID = class_5458.field_25933.getRawId(Biomes.SAVANNA);
    private static final int SWAMP_ID = class_5458.field_25933.getRawId(Biomes.SWAMP);
    private static final int TAIGA_ID = class_5458.field_25933.getRawId(Biomes.TAIGA);
    private static final int SNOWY_TAIGA_ID = class_5458.field_25933.getRawId(Biomes.SNOWY_TAIGA);
    private static final int[] OLD_GROUP_1 = new int[]{DESERT_ID, FOREST_ID, MOUNTAINS_ID, SWAMP_ID, PLAINS_ID, TAIGA_ID};
    private static final int[] DRY_BIOMES = new int[]{DESERT_ID, DESERT_ID, DESERT_ID, SAVANNA_ID, SAVANNA_ID, PLAINS_ID};
    private static final int[] TEMPERATE_BIOMES = new int[]{FOREST_ID, DARK_FOREST_ID, MOUNTAINS_ID, PLAINS_ID, BIRCH_FOREST_ID, SWAMP_ID};
    private static final int[] COOL_BIOMES = new int[]{FOREST_ID, MOUNTAINS_ID, TAIGA_ID, PLAINS_ID};
    private static final int[] SNOWY_BIOMES = new int[]{SNOWY_TUNDRA_ID, SNOWY_TUNDRA_ID, SNOWY_TUNDRA_ID, SNOWY_TAIGA_ID};
    private int[] chosenGroup1 = DRY_BIOMES;

    public SetBaseBiomesLayer(boolean bl) {
        if (bl) {
            this.chosenGroup1 = OLD_GROUP_1;
        }
    }

    @Override
    public int sample(LayerRandomnessSource arg, int i) {
        int j = (i & 0xF00) >> 8;
        if (BiomeLayers.isOcean(i &= 0xFFFFF0FF) || i == MUSHROOM_FIELDS_ID) {
            return i;
        }
        switch (i) {
            case 1: {
                if (j > 0) {
                    return arg.nextInt(3) == 0 ? BADLANDS_PLATEAU_ID : WOODED_BADLANDS_PLATEAU_ID;
                }
                return this.chosenGroup1[arg.nextInt(this.chosenGroup1.length)];
            }
            case 2: {
                if (j > 0) {
                    return JUNGLE_ID;
                }
                return TEMPERATE_BIOMES[arg.nextInt(TEMPERATE_BIOMES.length)];
            }
            case 3: {
                if (j > 0) {
                    return GIANT_TREE_TAIGA_ID;
                }
                return COOL_BIOMES[arg.nextInt(COOL_BIOMES.length)];
            }
            case 4: {
                return SNOWY_BIOMES[arg.nextInt(SNOWY_BIOMES.length)];
            }
        }
        return MUSHROOM_FIELDS_ID;
    }
}

