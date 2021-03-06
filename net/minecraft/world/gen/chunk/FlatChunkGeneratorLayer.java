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
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.registry.Registry;

public class FlatChunkGeneratorLayer {
    public static final Codec<FlatChunkGeneratorLayer> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Codec.intRange((int)0, (int)256).fieldOf("height").forGetter(FlatChunkGeneratorLayer::getThickness), (App)Registry.BLOCK.fieldOf("block").orElse((Object)Blocks.AIR).forGetter(arg -> arg.getBlockState().getBlock())).apply((Applicative)instance, FlatChunkGeneratorLayer::new));
    private final BlockState blockState;
    private final int thickness;
    private int startY;

    public FlatChunkGeneratorLayer(int thickness, Block block) {
        this.thickness = thickness;
        this.blockState = block.getDefaultState();
    }

    public int getThickness() {
        return this.thickness;
    }

    public BlockState getBlockState() {
        return this.blockState;
    }

    public int getStartY() {
        return this.startY;
    }

    public void setStartY(int startY) {
        this.startY = startY;
    }

    public String toString() {
        return (this.thickness != 1 ? this.thickness + "*" : "") + Registry.BLOCK.getId(this.blockState.getBlock());
    }
}

