/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.gen.feature;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.class_5428;
import net.minecraft.world.gen.feature.FeatureConfig;

public class NetherrackReplaceBlobsFeatureConfig
implements FeatureConfig {
    public static final Codec<NetherrackReplaceBlobsFeatureConfig> field_25848 = RecordCodecBuilder.create(instance -> instance.group((App)BlockState.CODEC.fieldOf("target").forGetter(arg -> arg.field_25849), (App)BlockState.CODEC.fieldOf("state").forGetter(arg -> arg.field_25850), (App)class_5428.field_25809.fieldOf("radius").forGetter(arg -> arg.field_25851)).apply((Applicative)instance, NetherrackReplaceBlobsFeatureConfig::new));
    public final BlockState field_25849;
    public final BlockState field_25850;
    private final class_5428 field_25851;

    public NetherrackReplaceBlobsFeatureConfig(BlockState arg, BlockState arg2, class_5428 arg3) {
        this.field_25849 = arg;
        this.field_25850 = arg2;
        this.field_25851 = arg3;
    }

    public class_5428 method_30405() {
        return this.field_25851;
    }
}

