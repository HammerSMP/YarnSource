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
import net.minecraft.block.Blocks;
import net.minecraft.world.gen.feature.FeatureConfig;

public class HugeFungusFeatureConfig
implements FeatureConfig {
    public static final Codec<HugeFungusFeatureConfig> field_24838 = RecordCodecBuilder.create(instance -> instance.group((App)BlockState.field_24734.fieldOf("valid_base_block").forGetter(arg -> arg.validBaseBlock), (App)BlockState.field_24734.fieldOf("stem_state").forGetter(arg -> arg.stemState), (App)BlockState.field_24734.fieldOf("hat_state").forGetter(arg -> arg.hatState), (App)BlockState.field_24734.fieldOf("decor_state").forGetter(arg -> arg.decorationState), (App)Codec.BOOL.fieldOf("planted").withDefault((Object)false).forGetter(arg -> arg.planted)).apply((Applicative)instance, HugeFungusFeatureConfig::new));
    public static final HugeFungusFeatureConfig CRIMSON_FUNGUS_CONFIG = new HugeFungusFeatureConfig(Blocks.CRIMSON_NYLIUM.getDefaultState(), Blocks.CRIMSON_STEM.getDefaultState(), Blocks.NETHER_WART_BLOCK.getDefaultState(), Blocks.SHROOMLIGHT.getDefaultState(), true);
    public static final HugeFungusFeatureConfig CRIMSON_FUNGUS_NOT_PLANTED_CONFIG = new HugeFungusFeatureConfig(HugeFungusFeatureConfig.CRIMSON_FUNGUS_CONFIG.validBaseBlock, HugeFungusFeatureConfig.CRIMSON_FUNGUS_CONFIG.stemState, HugeFungusFeatureConfig.CRIMSON_FUNGUS_CONFIG.hatState, HugeFungusFeatureConfig.CRIMSON_FUNGUS_CONFIG.decorationState, false);
    public static final HugeFungusFeatureConfig WARPED_FUNGUS_CONFIG = new HugeFungusFeatureConfig(Blocks.WARPED_NYLIUM.getDefaultState(), Blocks.WARPED_STEM.getDefaultState(), Blocks.WARPED_WART_BLOCK.getDefaultState(), Blocks.SHROOMLIGHT.getDefaultState(), true);
    public static final HugeFungusFeatureConfig WARPED_FUNGUS_NOT_PLANTED_CONFIG = new HugeFungusFeatureConfig(HugeFungusFeatureConfig.WARPED_FUNGUS_CONFIG.validBaseBlock, HugeFungusFeatureConfig.WARPED_FUNGUS_CONFIG.stemState, HugeFungusFeatureConfig.WARPED_FUNGUS_CONFIG.hatState, HugeFungusFeatureConfig.WARPED_FUNGUS_CONFIG.decorationState, false);
    public final BlockState validBaseBlock;
    public final BlockState stemState;
    public final BlockState hatState;
    public final BlockState decorationState;
    public final boolean planted;

    public HugeFungusFeatureConfig(BlockState arg, BlockState arg2, BlockState arg3, BlockState arg4, boolean bl) {
        this.validBaseBlock = arg;
        this.stemState = arg2;
        this.hatState = arg3;
        this.decorationState = arg4;
        this.planted = bl;
    }
}

