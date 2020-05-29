/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.structure.rule;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.class_5323;
import net.minecraft.structure.rule.RuleTest;
import net.minecraft.structure.rule.RuleTestType;
import net.minecraft.tag.Tag;

public class TagMatchRuleTest
extends RuleTest {
    public static final Codec<TagMatchRuleTest> field_25014 = Tag.method_28134(class_5323.method_29223()::method_29218).fieldOf("tag").xmap(TagMatchRuleTest::new, arg -> arg.tag).codec();
    private final Tag<Block> tag;

    public TagMatchRuleTest(Tag<Block> arg) {
        this.tag = arg;
    }

    @Override
    public boolean test(BlockState arg, Random random) {
        return arg.isIn(this.tag);
    }

    @Override
    protected RuleTestType<?> getType() {
        return RuleTestType.TAG_MATCH;
    }
}

