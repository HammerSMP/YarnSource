/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.gen.chunk;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.Function;
import net.minecraft.util.dynamic.NumberCodecs;

public class StructureConfig {
    public static final Codec<StructureConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)NumberCodecs.rangedInt(0, 4096).fieldOf("spacing").forGetter(arg -> arg.spacing), (App)NumberCodecs.rangedInt(0, 4096).fieldOf("separation").forGetter(arg -> arg.separation), (App)NumberCodecs.rangedInt(0, Integer.MAX_VALUE).fieldOf("salt").forGetter(arg -> arg.salt)).apply((Applicative)instance, StructureConfig::new)).comapFlatMap(arg -> {
        if (arg.spacing <= arg.separation) {
            return DataResult.error((String)"Spacing has to be smaller than separation");
        }
        return DataResult.success((Object)arg);
    }, Function.identity());
    private final int spacing;
    private final int separation;
    private final int salt;

    public StructureConfig(int i, int j, int k) {
        this.spacing = i;
        this.separation = j;
        this.salt = k;
    }

    public int getSpacing() {
        return this.spacing;
    }

    public int getSeparation() {
        return this.separation;
    }

    public int getSalt() {
        return this.salt;
    }
}

