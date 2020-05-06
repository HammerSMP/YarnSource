/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.Dynamic
 *  com.mojang.datafixers.types.DynamicOps
 */
package net.minecraft.structure.rule;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;
import net.minecraft.structure.rule.PosRuleTestType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;

public abstract class PosRuleTest {
    public abstract boolean test(BlockPos var1, BlockPos var2, BlockPos var3, Random var4);

    public <T> Dynamic<T> serialize(DynamicOps<T> dynamicOps) {
        return new Dynamic(dynamicOps, dynamicOps.mergeInto(this.serializeContents(dynamicOps).getValue(), dynamicOps.createString("predicate_type"), dynamicOps.createString(Registry.POS_RULE_TEST.getId(this.getType()).toString())));
    }

    protected abstract PosRuleTestType getType();

    protected abstract <T> Dynamic<T> serializeContents(DynamicOps<T> var1);
}

