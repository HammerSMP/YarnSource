/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 */
package net.minecraft.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.util.registry.Registry;

public class DefaultParticleType
extends ParticleType<DefaultParticleType>
implements ParticleEffect {
    private static final ParticleEffect.Factory<DefaultParticleType> PARAMETER_FACTORY = new ParticleEffect.Factory<DefaultParticleType>(){

        @Override
        public DefaultParticleType read(ParticleType<DefaultParticleType> arg, StringReader stringReader) throws CommandSyntaxException {
            return (DefaultParticleType)arg;
        }

        @Override
        public DefaultParticleType read(ParticleType<DefaultParticleType> arg, PacketByteBuf arg2) {
            return (DefaultParticleType)arg;
        }

        @Override
        public /* synthetic */ ParticleEffect read(ParticleType arg, PacketByteBuf arg2) {
            return this.read(arg, arg2);
        }

        @Override
        public /* synthetic */ ParticleEffect read(ParticleType arg, StringReader stringReader) throws CommandSyntaxException {
            return this.read(arg, stringReader);
        }
    };

    protected DefaultParticleType(boolean bl) {
        super(bl, PARAMETER_FACTORY);
    }

    public ParticleType<DefaultParticleType> getType() {
        return this;
    }

    @Override
    public void write(PacketByteBuf arg) {
    }

    @Override
    public String asString() {
        return Registry.PARTICLE_TYPE.getId(this).toString();
    }
}

