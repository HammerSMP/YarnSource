/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  org.apache.commons.lang3.mutable.MutableBoolean
 */
package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.class_5444;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.DecoratedFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import org.apache.commons.lang3.mutable.MutableBoolean;

public class DecoratedFeature
extends Feature<DecoratedFeatureConfig> {
    public DecoratedFeature(Codec<DecoratedFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(ServerWorldAccess arg, ChunkGenerator arg2, Random random, BlockPos arg3, DecoratedFeatureConfig arg42) {
        MutableBoolean mutableBoolean = new MutableBoolean();
        arg42.decorator.method_30444(new class_5444(arg, arg2), random, arg3).forEach(arg4 -> {
            if (arg.feature.get().generate(arg, arg2, random, (BlockPos)arg4)) {
                mutableBoolean.setTrue();
            }
        });
        return mutableBoolean.isTrue();
    }

    public String toString() {
        return String.format("< %s [%s] >", this.getClass().getSimpleName(), Registry.FEATURE.getId(this));
    }
}

