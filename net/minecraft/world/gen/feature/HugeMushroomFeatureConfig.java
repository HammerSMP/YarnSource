/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.gen.feature;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;

public class HugeMushroomFeatureConfig
implements FeatureConfig {
    public static final Codec<HugeMushroomFeatureConfig> field_24885 = RecordCodecBuilder.create(instance -> instance.group((App)BlockStateProvider.field_24937.fieldOf("cap_provider").forGetter(arg -> arg.capProvider), (App)BlockStateProvider.field_24937.fieldOf("stem_provider").forGetter(arg -> arg.stemProvider), (App)Codec.INT.fieldOf("foliage_radius").withDefault((Object)2).forGetter(arg -> arg.capSize)).apply((Applicative)instance, HugeMushroomFeatureConfig::new));
    public final BlockStateProvider capProvider;
    public final BlockStateProvider stemProvider;
    public final int capSize;

    public HugeMushroomFeatureConfig(BlockStateProvider arg, BlockStateProvider arg2, int i) {
        this.capProvider = arg;
        this.stemProvider = arg2;
        this.capSize = i;
    }
}

