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
import net.minecraft.structure.rule.BlockMatchRuleTest;
import net.minecraft.structure.rule.RuleTest;
import net.minecraft.structure.rule.TagMatchRuleTest;
import net.minecraft.tag.BlockTags;
import net.minecraft.world.gen.feature.FeatureConfig;

public class OreFeatureConfig
implements FeatureConfig {
    public static final Codec<OreFeatureConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)RuleTest.field_25012.fieldOf("target").forGetter(arg -> arg.target), (App)BlockState.CODEC.fieldOf("state").forGetter(arg -> arg.state), (App)Codec.intRange((int)0, (int)64).fieldOf("size").forGetter(arg -> arg.size)).apply((Applicative)instance, OreFeatureConfig::new));
    public final RuleTest target;
    public final int size;
    public final BlockState state;

    public OreFeatureConfig(RuleTest arg, BlockState state, int size) {
        this.size = size;
        this.state = state;
        this.target = arg;
    }

    public static final class class_5436 {
        public static final RuleTest field_25845 = new TagMatchRuleTest(BlockTags.BASE_STONE_OVERWORLD);
        public static final RuleTest field_25846 = new BlockMatchRuleTest(Blocks.NETHERRACK);
        public static final RuleTest field_25847 = new TagMatchRuleTest(BlockTags.BASE_STONE_NETHER);
    }
}

