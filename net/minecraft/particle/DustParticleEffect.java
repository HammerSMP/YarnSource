/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Locale;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;

public class DustParticleEffect
implements ParticleEffect {
    public static final DustParticleEffect RED = new DustParticleEffect(1.0f, 0.0f, 0.0f, 1.0f);
    public static final Codec<DustParticleEffect> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Codec.FLOAT.fieldOf("r").forGetter(arg -> Float.valueOf(arg.red)), (App)Codec.FLOAT.fieldOf("g").forGetter(arg -> Float.valueOf(arg.green)), (App)Codec.FLOAT.fieldOf("b").forGetter(arg -> Float.valueOf(arg.blue)), (App)Codec.FLOAT.fieldOf("scale").forGetter(arg -> Float.valueOf(arg.scale))).apply((Applicative)instance, DustParticleEffect::new));
    public static final ParticleEffect.Factory<DustParticleEffect> PARAMETERS_FACTORY = new ParticleEffect.Factory<DustParticleEffect>(){

        @Override
        public DustParticleEffect read(ParticleType<DustParticleEffect> arg, StringReader stringReader) throws CommandSyntaxException {
            stringReader.expect(' ');
            float f = (float)stringReader.readDouble();
            stringReader.expect(' ');
            float g = (float)stringReader.readDouble();
            stringReader.expect(' ');
            float h = (float)stringReader.readDouble();
            stringReader.expect(' ');
            float i = (float)stringReader.readDouble();
            return new DustParticleEffect(f, g, h, i);
        }

        @Override
        public DustParticleEffect read(ParticleType<DustParticleEffect> arg, PacketByteBuf arg2) {
            return new DustParticleEffect(arg2.readFloat(), arg2.readFloat(), arg2.readFloat(), arg2.readFloat());
        }

        @Override
        public /* synthetic */ ParticleEffect read(ParticleType type, PacketByteBuf buf) {
            return this.read(type, buf);
        }

        @Override
        public /* synthetic */ ParticleEffect read(ParticleType type, StringReader reader) throws CommandSyntaxException {
            return this.read(type, reader);
        }
    };
    private final float red;
    private final float green;
    private final float blue;
    private final float scale;

    public DustParticleEffect(float red, float green, float blue, float scale) {
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.scale = MathHelper.clamp(scale, 0.01f, 4.0f);
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeFloat(this.red);
        buf.writeFloat(this.green);
        buf.writeFloat(this.blue);
        buf.writeFloat(this.scale);
    }

    @Override
    public String asString() {
        return String.format(Locale.ROOT, "%s %.2f %.2f %.2f %.2f", Registry.PARTICLE_TYPE.getId(this.getType()), Float.valueOf(this.red), Float.valueOf(this.green), Float.valueOf(this.blue), Float.valueOf(this.scale));
    }

    public ParticleType<DustParticleEffect> getType() {
        return ParticleTypes.DUST;
    }

    @Environment(value=EnvType.CLIENT)
    public float getRed() {
        return this.red;
    }

    @Environment(value=EnvType.CLIENT)
    public float getGreen() {
        return this.green;
    }

    @Environment(value=EnvType.CLIENT)
    public float getBlue() {
        return this.blue;
    }

    @Environment(value=EnvType.CLIENT)
    public float getScale() {
        return this.scale;
    }
}

