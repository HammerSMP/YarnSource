/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.mojang.datafixers.Dynamic
 *  com.mojang.datafixers.types.DynamicOps
 *  javax.annotation.Nullable
 */
package net.minecraft.structure.processor;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Map;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.datafixer.NbtOps;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.structure.rule.AlwaysTruePosRuleTest;
import net.minecraft.structure.rule.AlwaysTrueRuleTest;
import net.minecraft.structure.rule.PosRuleTest;
import net.minecraft.structure.rule.RuleTest;
import net.minecraft.util.dynamic.DynamicDeserializer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;

public class StructureProcessorRule {
    private final RuleTest inputPredicate;
    private final RuleTest locationPredicate;
    private final PosRuleTest positionPredicate;
    private final BlockState outputState;
    @Nullable
    private final CompoundTag tag;

    public StructureProcessorRule(RuleTest arg, RuleTest arg2, BlockState arg3) {
        this(arg, arg2, AlwaysTruePosRuleTest.INSTANCE, arg3, null);
    }

    public StructureProcessorRule(RuleTest arg, RuleTest arg2, PosRuleTest arg3, BlockState arg4) {
        this(arg, arg2, arg3, arg4, null);
    }

    public StructureProcessorRule(RuleTest arg, RuleTest arg2, PosRuleTest arg3, BlockState arg4, @Nullable CompoundTag arg5) {
        this.inputPredicate = arg;
        this.locationPredicate = arg2;
        this.positionPredicate = arg3;
        this.outputState = arg4;
        this.tag = arg5;
    }

    public boolean test(BlockState arg, BlockState arg2, BlockPos arg3, BlockPos arg4, BlockPos arg5, Random random) {
        return this.inputPredicate.test(arg, random) && this.locationPredicate.test(arg2, random) && this.positionPredicate.test(arg3, arg4, arg5, random);
    }

    public BlockState getOutputState() {
        return this.outputState;
    }

    @Nullable
    public CompoundTag getTag() {
        return this.tag;
    }

    public <T> Dynamic<T> toDynamic(DynamicOps<T> dynamicOps) {
        Object object = dynamicOps.createMap((Map)ImmutableMap.of((Object)dynamicOps.createString("input_predicate"), (Object)this.inputPredicate.serializeWithId(dynamicOps).getValue(), (Object)dynamicOps.createString("location_predicate"), (Object)this.locationPredicate.serializeWithId(dynamicOps).getValue(), (Object)dynamicOps.createString("position_predicate"), (Object)this.positionPredicate.serialize(dynamicOps).getValue(), (Object)dynamicOps.createString("output_state"), (Object)BlockState.serialize(dynamicOps, this.outputState).getValue()));
        if (this.tag == null) {
            return new Dynamic(dynamicOps, object);
        }
        return new Dynamic(dynamicOps, dynamicOps.mergeInto(object, dynamicOps.createString("output_nbt"), new Dynamic((DynamicOps)NbtOps.INSTANCE, (Object)this.tag).convert(dynamicOps).getValue()));
    }

    public static <T> StructureProcessorRule fromDynamic(Dynamic<T> dynamic2) {
        Dynamic dynamic22 = dynamic2.get("input_predicate").orElseEmptyMap();
        Dynamic dynamic3 = dynamic2.get("location_predicate").orElseEmptyMap();
        Dynamic dynamic4 = dynamic2.get("position_predicate").orElseEmptyMap();
        RuleTest lv = DynamicDeserializer.deserialize(dynamic22, Registry.RULE_TEST, "predicate_type", AlwaysTrueRuleTest.INSTANCE);
        RuleTest lv2 = DynamicDeserializer.deserialize(dynamic3, Registry.RULE_TEST, "predicate_type", AlwaysTrueRuleTest.INSTANCE);
        PosRuleTest lv3 = DynamicDeserializer.deserialize(dynamic4, Registry.POS_RULE_TEST, "predicate_type", AlwaysTruePosRuleTest.INSTANCE);
        BlockState lv4 = BlockState.deserialize(dynamic2.get("output_state").orElseEmptyMap());
        CompoundTag lv5 = dynamic2.get("output_nbt").map(dynamic -> (Tag)dynamic.convert((DynamicOps)NbtOps.INSTANCE).getValue()).orElse(null);
        return new StructureProcessorRule(lv, lv2, lv3, lv4, lv5);
    }
}

