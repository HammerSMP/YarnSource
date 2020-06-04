/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.StructurePoolFeatureConfig;

public class BastionRemnantFeatureConfig
implements FeatureConfig {
    public static final Codec<BastionRemnantFeatureConfig> CODEC = StructurePoolFeatureConfig.CODEC.listOf().xmap(BastionRemnantFeatureConfig::new, arg -> arg.possibleConfigs);
    private final List<StructurePoolFeatureConfig> possibleConfigs;

    public BastionRemnantFeatureConfig(Map<String, Integer> map) {
        this(map.entrySet().stream().map(entry -> new StructurePoolFeatureConfig(new Identifier((String)entry.getKey()), (Integer)entry.getValue())).collect(Collectors.toList()));
    }

    private BastionRemnantFeatureConfig(List<StructurePoolFeatureConfig> list) {
        this.possibleConfigs = list;
    }

    public StructurePoolFeatureConfig getRandom(Random random) {
        return this.possibleConfigs.get(random.nextInt(this.possibleConfigs.size()));
    }
}

