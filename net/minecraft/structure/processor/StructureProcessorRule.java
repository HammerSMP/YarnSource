/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  javax.annotation.Nullable
 */
package net.minecraft.structure.processor;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.structure.rule.AlwaysTruePosRuleTest;
import net.minecraft.structure.rule.PosRuleTest;
import net.minecraft.structure.rule.RuleTest;
import net.minecraft.util.math.BlockPos;

public class StructureProcessorRule {
    public static final Codec<StructureProcessorRule> field_25008 = RecordCodecBuilder.create(instance -> instance.group((App)RuleTest.field_25012.fieldOf("input_predicate").forGetter(arg -> arg.inputPredicate), (App)RuleTest.field_25012.fieldOf("location_predicate").forGetter(arg -> arg.locationPredicate), (App)PosRuleTest.field_25007.fieldOf("position_predicate").forGetter(arg -> arg.positionPredicate), (App)BlockState.field_24734.fieldOf("output_state").forGetter(arg -> arg.outputState), (App)CompoundTag.field_25128.optionalFieldOf("output_nbt").forGetter(arg -> Optional.ofNullable(arg.tag))).apply((Applicative)instance, StructureProcessorRule::new));
    private final RuleTest inputPredicate;
    private final RuleTest locationPredicate;
    private final PosRuleTest positionPredicate;
    private final BlockState outputState;
    @Nullable
    private final CompoundTag tag;

    public StructureProcessorRule(RuleTest arg, RuleTest arg2, BlockState arg3) {
        this(arg, arg2, AlwaysTruePosRuleTest.INSTANCE, arg3, Optional.empty());
    }

    public StructureProcessorRule(RuleTest arg, RuleTest arg2, PosRuleTest arg3, BlockState arg4) {
        this(arg, arg2, arg3, arg4, Optional.empty());
    }

    public StructureProcessorRule(RuleTest arg, RuleTest arg2, PosRuleTest arg3, BlockState arg4, Optional<CompoundTag> optional) {
        this.inputPredicate = arg;
        this.locationPredicate = arg2;
        this.positionPredicate = arg3;
        this.outputState = arg4;
        this.tag = optional.orElse(null);
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
}

