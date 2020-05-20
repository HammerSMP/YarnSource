/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.particle;

import com.mojang.serialization.Codec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.particle.ParticleEffect;

public abstract class ParticleType<T extends ParticleEffect> {
    private final boolean shouldAlwaysSpawn;
    private final ParticleEffect.Factory<T> parametersFactory;

    protected ParticleType(boolean bl, ParticleEffect.Factory<T> arg) {
        this.shouldAlwaysSpawn = bl;
        this.parametersFactory = arg;
    }

    @Environment(value=EnvType.CLIENT)
    public boolean shouldAlwaysSpawn() {
        return this.shouldAlwaysSpawn;
    }

    public ParticleEffect.Factory<T> getParametersFactory() {
        return this.parametersFactory;
    }

    public abstract Codec<T> method_29138();
}

