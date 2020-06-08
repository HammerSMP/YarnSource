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
import net.minecraft.util.dynamic.NumberCodecs;

public class StrongholdConfig {
    public static final Codec<StrongholdConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)NumberCodecs.rangedInt(0, 1023).fieldOf("distance").forGetter(StrongholdConfig::getDistance), (App)NumberCodecs.rangedInt(0, 1023).fieldOf("spread").forGetter(StrongholdConfig::getSpread), (App)NumberCodecs.rangedInt(1, 4095).fieldOf("count").forGetter(StrongholdConfig::getCount)).apply((Applicative)instance, StrongholdConfig::new));
    private final int distance;
    private final int spread;
    private final int count;

    public StrongholdConfig(int i, int j, int k) {
        this.distance = i;
        this.spread = j;
        this.count = k;
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

