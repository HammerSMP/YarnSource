/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.gen.chunk;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class StrongholdConfig {
    public static final Codec<StrongholdConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Codec.intRange((int)0, (int)1023).fieldOf("distance").forGetter(StrongholdConfig::getDistance), (App)Codec.intRange((int)0, (int)1023).fieldOf("spread").forGetter(StrongholdConfig::getSpread), (App)Codec.intRange((int)1, (int)4095).fieldOf("count").forGetter(StrongholdConfig::getCount)).apply((Applicative)instance, StrongholdConfig::new));
    private final int distance;
    private final int spread;
    private final int count;

    public StrongholdConfig(int distance, int spread, int count) {
        this.distance = distance;
        this.spread = spread;
        this.count = count;
    }

    public int getDistance() {
        return this.distance;
    }

    public int getSpread() {
        return this.spread;
    }

    public int getCount() {
        return this.count;
    }
}

