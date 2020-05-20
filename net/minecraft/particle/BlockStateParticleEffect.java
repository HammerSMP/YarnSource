/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.serialization.Codec
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.command.arguments.BlockArgumentParser;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.util.registry.Registry;

public class BlockStateParticleEffect
implements ParticleEffect {
    public static final ParticleEffect.Factory<BlockStateParticleEffect> PARAMETERS_FACTORY = new ParticleEffect.Factory<BlockStateParticleEffect>(){

        @Override
        public BlockStateParticleEffect read(ParticleType<BlockStateParticleEffect> arg, StringReader stringReader) throws CommandSyntaxException {
            stringReader.expect(' ');
            return new BlockStateParticleEffect(arg, new BlockArgumentParser(stringReader, false).parse(false).getBlockState());
        }

        @Override
        public BlockStateParticleEffect read(ParticleType<BlockStateParticleEffect> arg, PacketByteBuf arg2) {
            return new BlockStateParticleEffect(arg, Block.STATE_IDS.get(arg2.readVarInt()));
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
    private final ParticleType<BlockStateParticleEffect> type;
    private final BlockState blockState;

    public static Codec<BlockStateParticleEffect> method_29128(ParticleType<BlockStateParticleEffect> arg3) {
        return BlockState.field_24734.xmap(arg2 -> new BlockStateParticleEffect(arg3, (BlockState)arg2), arg -> arg.blockState);
    }

    public BlockStateParticleEffect(ParticleType<BlockStateParticleEffect> arg, BlockState arg2) {
        this.type = arg;
        this.blockState = arg2;
    }

    @Override
    public void write(PacketByteBuf arg) {
        arg.writeVarInt(Block.STATE_IDS.getId(this.blockState));
    }

    @Override
    public String asString() {
        return Registry.PARTICLE_TYPE.getId(this.getType()) + " " + BlockArgumentParser.stringifyBlockState(this.blockState);
    }

    public ParticleType<BlockStateParticleEffect> getType() {
        return this.type;
    }

    @Environment(value=EnvType.CLIENT)
    public BlockState getBlockState() {
        return this.blockState;
    }
}

