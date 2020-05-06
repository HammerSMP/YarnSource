/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.world.biome;

import java.util.Random;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.particle.DefaultParticleType;

public class BiomeParticleConfig {
    private final DefaultParticleType type;
    private final float chance;
    private final Function<Random, Double> velocityXFactory;
    private final Function<Random, Double> velocityYFactory;
    private final Function<Random, Double> velocityZFactory;

    public BiomeParticleConfig(DefaultParticleType arg, float f, Function<Random, Double> function, Function<Random, Double> function2, Function<Random, Double> function3) {
        this.type = arg;
        this.chance = f;
        this.velocityXFactory = function;
        this.velocityYFactory = function2;
        this.velocityZFactory = function3;
    }

    @Environment(value=EnvType.CLIENT)
    public DefaultParticleType getParticleType() {
        return this.type;
    }

    @Environment(value=EnvType.CLIENT)
    public boolean shouldAddParticle(Random random) {
        return random.nextFloat() <= this.chance;
    }

    @Environment(value=EnvType.CLIENT)
    public double generateVelocityX(Random random) {
        return this.velocityXFactory.apply(random);
    }

    @Environment(value=EnvType.CLIENT)
    public double generateVelocityY(Random random) {
        return this.velocityYFactory.apply(random);
    }

    @Environment(value=EnvType.CLIENT)
    public double generateVelocityZ(Random random) {
        return this.velocityZFactory.apply(random);
    }
}

