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

public class StructureConfig {
    public static final Codec<StructureConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Codec.intRange((int)0, (int)4096).fieldOf("spacing").forGetter(config -> config.spacing), (App)Codec.intRange((int)0, (int)4096).fieldOf("separation").forGetter(config -> config.separation), (App)Codec.intRange((int)0, (int)Integer.MAX_VALUE).fieldOf("salt").forGetter(config -> config.salt)).apply((Applicative)instance, StructureConfig::new)).comapFlatMap(config -> {
        if (config.spacing <= config.separation) {
            return DataResult.error((String)"Spacing has to be smaller than separation");
        }
        return DataResult.success((Object)config);
    }, Function.identity());
    private final int spacing;
    private final int separation;
    private final int salt;

    public StructureConfig(int spacing, int separation, int salt) {
        this.spacing = spacing;
        this.separation = separation;
        this.salt = salt;
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

