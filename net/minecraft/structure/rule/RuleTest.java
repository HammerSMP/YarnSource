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
import net.minecraft.block.BlockState;
import net.minecraft.structure.rule.RuleTestType;
import net.minecraft.util.registry.Registry;

public abstract class RuleTest {
    public abstract boolean test(BlockState var1, Random var2);

    public <T> Dynamic<T> serializeWithId(DynamicOps<T> dynamicOps) {
        return new Dynamic(dynamicOps, dynamicOps.mergeInto(this.serialize(dynamicOps).getValue(), dynamicOps.createString("predicate_type"), dynamicOps.createString(Registry.RULE_TEST.getId(this.getType()).toString())));
    }

    protected abstract RuleTestType getType();

    protected abstract <T> Dynamic<T> serialize(DynamicOps<T> var1);
}

