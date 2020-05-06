/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.mojang.datafixers.Dynamic
 *  com.mojang.datafixers.types.DynamicOps
 */
package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Map;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.world.gen.feature.FeatureConfig;

public class HugeFungusFeatureConfig
implements FeatureConfig {
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

    @Override
    public <T> Dynamic<T> serialize(DynamicOps<T> dynamicOps) {
        return new Dynamic(dynamicOps, dynamicOps.createMap((Map)ImmutableMap.of((Object)dynamicOps.createString("valid_base_block"), (Object)BlockState.serialize(dynamicOps, this.validBaseBlock).getValue(), (Object)dynamicOps.createString("stem_state"), (Object)BlockState.serialize(dynamicOps, this.stemState).getValue(), (Object)dynamicOps.createString("hat_state"), (Object)BlockState.serialize(dynamicOps, this.hatState).getValue(), (Object)dynamicOps.createString("decor_state"), (Object)BlockState.serialize(dynamicOps, this.decorationState).getValue(), (Object)dynamicOps.createString("planted"), (Object)dynamicOps.createBoolean(this.planted))));
    }

    public static <T> HugeFungusFeatureConfig deserialize(Dynamic<T> dynamic) {
        BlockState lv = dynamic.get("valid_base_state").map(BlockState::deserialize).orElse(Blocks.AIR.getDefaultState());
        BlockState lv2 = dynamic.get("stem_state").map(BlockState::deserialize).orElse(Blocks.AIR.getDefaultState());
        BlockState lv3 = dynamic.get("hat_state").map(BlockState::deserialize).orElse(Blocks.AIR.getDefaultState());
        BlockState lv4 = dynamic.get("decor_state").map(BlockState::deserialize).orElse(Blocks.AIR.getDefaultState());
        boolean bl = dynamic.get("planted").asBoolean(false);
        return new HugeFungusFeatureConfig(lv, lv2, lv3, lv4, bl);
    }
}

