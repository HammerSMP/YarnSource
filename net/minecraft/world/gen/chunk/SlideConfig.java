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

public class SlideConfig {
    public static final Codec<SlideConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Codec.INT.fieldOf("target").forGetter(SlideConfig::getTarget), (App)NumberCodecs.rangedInt(0, 256).fieldOf("size").forGetter(SlideConfig::getSize), (App)Codec.INT.fieldOf("offset").forGetter(SlideConfig::getOffset)).apply((Applicative)instance, SlideConfig::new));
    private final int target;
    private final int size;
    private final int offset;

    public SlideConfig(int i, int j, int k) {
        this.target = i;
        this.size = j;
        this.offset = k;
    }

    public int getTarget() {
        return this.target;
    }

    public int getSize() {
        return this.size;
    }

    public int getOffset() {
        return this.offset;
    }
}

