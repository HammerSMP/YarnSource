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
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.feature.FeatureConfig;

public class StructurePoolFeatureConfig
implements FeatureConfig {
    public static final Codec<StructurePoolFeatureConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Identifier.CODEC.fieldOf("start_pool").forGetter(StructurePoolFeatureConfig::getStartPool), (App)Codec.INT.fieldOf("size").forGetter(StructurePoolFeatureConfig::getSize)).apply((Applicative)instance, StructurePoolFeatureConfig::new));
    public final Identifier startPool;
    public final int size;

    public StructurePoolFeatureConfig(Identifier arg, int i) {
        this.startPool = arg;
        this.size = i;
    }

    public int getSize() {
        return this.size;
    }

    public Identifier getStartPool() {
        return this.startPool;
    }
}

