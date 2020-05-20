/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.structure.rule;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.structure.rule.RuleTest;
import net.minecraft.structure.rule.RuleTestType;

public class BlockStateMatchRuleTest
extends RuleTest {
    public static final Codec<BlockStateMatchRuleTest> field_25001 = BlockState.field_24734.fieldOf("block_state").xmap(BlockStateMatchRuleTest::new, arg -> arg.blockState).codec();
    private final BlockState blockState;

    public BlockStateMatchRuleTest(BlockState arg) {
        this.blockState = arg;
    }

    @Override
    public boolean test(BlockState arg, Random random) {
        return arg == this.blockState;
    }

    @Override
    protected RuleTestType<?> getType() {
        return RuleTestType.BLOCKSTATE_MATCH;
    }
}

