/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.world.biome;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;

public class BiomeParticleConfig {
    public static final Codec<BiomeParticleConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)ParticleTypes.field_25125.fieldOf("options").forGetter(arg -> arg.field_24676), (App)Codec.FLOAT.fieldOf("probability").forGetter(arg -> Float.valueOf(arg.chance))).apply((Applicative)instance, BiomeParticleConfig::new));
    private final ParticleEffect field_24676;
    private final float chance;

    public BiomeParticleConfig(ParticleEffect arg, float f) {
        this.field_24676 = arg;
        this.chance = f;
    }

    @Environment(value=EnvType.CLIENT)
    public ParticleEffect getParticleType() {
        return this.field_24676;
    }

    @Environment(value=EnvType.CLIENT)
    public boolean shouldAddParticle(Random random) {
        return random.nextFloat() <= this.chance;
    }
}

