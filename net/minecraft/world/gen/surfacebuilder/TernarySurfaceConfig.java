/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.gen.surfacebuilder;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.world.gen.surfacebuilder.SurfaceConfig;

public class TernarySurfaceConfig
implements SurfaceConfig {
    public static final Codec<TernarySurfaceConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)BlockState.field_24734.fieldOf("top_material").forGetter(arg -> arg.topMaterial), (App)BlockState.field_24734.fieldOf("under_material").forGetter(arg -> arg.underMaterial), (App)BlockState.field_24734.fieldOf("underwater_material").forGetter(arg -> arg.underwaterMaterial)).apply((Applicative)instance, TernarySurfaceConfig::new));
    private final BlockState topMaterial;
    private final BlockState underMaterial;
    private final BlockState underwaterMaterial;

    public TernarySurfaceConfig(BlockState arg, BlockState arg2, BlockState arg3) {
        this.topMaterial = arg;
        this.underMaterial = arg2;
        this.underwaterMaterial = arg3;
    }

    @Override
    public BlockState getTopMaterial() {
        return this.topMaterial;
    }

    @Override
    public BlockState getUnderMaterial() {
        return this.underMaterial;
    }

    public BlockState getUnderwaterMaterial() {
        return this.underwaterMaterial;
    }
}

